package dao;

import entity.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Đọc/ghi 3 file XML trong thư mục data/. Lỗi đọc/ghi được ném ra dưới dạng
 * DuLieuException để tầng trên còn biết đường hoàn tác và báo cho người dùng.
 */
public class XmlDatabaseDAO {
    private static final String DATA_DIR = xacDinhThuMucData();
    private static final String FILE_SACH = DATA_DIR + File.separator + "sach.xml";
    private static final String FILE_DOC_GIA = DATA_DIR + File.separator + "docgia.xml";
    private static final String FILE_PHIEU_MUON = DATA_DIR + File.separator + "phieumuon.xml";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Chạy từ file .jar thì lấy thư mục data/ nằm cạnh file jar, nhờ vậy bấm đúp jar ở đâu cũng đọc đúng dữ liệu.
     * Chạy từ NetBeans/IDE thì dùng thư mục data/ ở gốc project như bình thường.
     */
    private static String xacDinhThuMucData() {
        try {
            File nguon = new File(XmlDatabaseDAO.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (nguon.isFile() && nguon.getName().toLowerCase().endsWith(".jar")) {
                return new File(nguon.getParentFile(), "data").getPath();
            }
        } catch (Exception e) {
            // không xác định được vị trí jar thì dùng thư mục làm việc hiện tại
        }
        return "data";
    }

    public XmlDatabaseDAO() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ---------- hàm dùng chung ----------

    private Element taoElementText(Document doc, String tagName, String value) {
        Element el = doc.createElement(tagName);
        el.appendChild(doc.createTextNode(value != null ? value : ""));
        return el;
    }

    private String layTextTag(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0 && list.item(0).getFirstChild() != null) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

    /** Parse một file XML. File hỏng thì báo lỗi chứ không trả về danh sách rỗng,
     *  vì nếu trả rỗng thì lần lưu sau sẽ ghi đè mất hết dữ liệu cũ. */
    private Document docFileXml(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null); // không in lỗi ra console, mình tự báo bằng DuLieuException
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            throw new DuLieuException("Không đọc được file " + file.getName() + " (file hỏng hoặc sai định dạng XML)", e);
        }
    }

    /** Ghi ra file tạm trước rồi mới đổi tên, để lỡ ghi dở thì file cũ vẫn còn nguyên. */
    private void ghiFileXml(Document doc, String filePath) {
        File dich = new File(filePath);
        File tam = new File(filePath + ".tmp");
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(tam));
            try {
                Files.move(tam.toPath(), dich.toPath(),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tam.toPath(), dich.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            tam.delete();
            throw new DuLieuException("Không ghi được file " + dich.getName(), e);
        }
    }

    private Document taoDocumentMoi() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (Exception e) {
            throw new DuLieuException("Không tạo được tài liệu XML", e);
        }
    }

    // ---------- Sách ----------

    public void luuDanhSachSach(List<Sach> dsSach) {
        Document doc = taoDocumentMoi();
        Element root = doc.createElement("DanhSachSach");
        doc.appendChild(root);

        for (Sach s : dsSach) {
            Element node = doc.createElement("Sach"); //createElement("Sach") để tạo phần tử <Sach> để chứa thông tin của 1 cuốn
            node.setAttribute("maSach", String.valueOf(s.getMaSach())); //setAttribute() lấy mã '3' từ s.getMaSach() rồi gắn vào thẻ mở,tạo thành <Sach maSach="3">.
            node.appendChild(taoElementText(doc, "tenSach", s.getTenSach()));//taoElementText(...) tạo <tenSach>Dế Mèn Phiêu Lưu Ký</tenSach>
            node.appendChild(taoElementText(doc, "theLoai", s.getTheLoai()));//Lệnh node.appendChild(...) đặt phần tử đó vào trong <Sach>
            node.appendChild(taoElementText(doc, "nhaXuatBan", s.getNhaXuatBan()));
            node.appendChild(taoElementText(doc, "giaBia", String.valueOf(s.getGiaBia())));
            node.appendChild(taoElementText(doc, "dangMuon", String.valueOf(s.isDangMuon())));
            root.appendChild(node);//đặt cuốn sách vừa tạo vào <DanhSachSach>
        }
        ghiFileXml(doc, FILE_SACH); //ghi cả danh sách xuống data/sach.xml
    }

    public List<Sach> docDanhSachSach() {
        List<Sach> list = new ArrayList<>();
        File file = new File(FILE_SACH);
        if (!file.exists()) return list;

        NodeList nodes = docFileXml(file).getElementsByTagName("Sach");
        try {
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                int ma = Integer.parseInt(el.getAttribute("maSach"));
                double gia = Double.parseDouble(layTextTag(el, "giaBia"));
                boolean dangMuon = Boolean.parseBoolean(layTextTag(el, "dangMuon"));
                list.add(new Sach(ma, layTextTag(el, "tenSach"), layTextTag(el, "theLoai"),
                        layTextTag(el, "nhaXuatBan"), gia, dangMuon));
            }
        } catch (NumberFormatException e) {
            throw new DuLieuException("File sach.xml có số liệu sai (mã sách hoặc giá bìa)", e);
        }
        return list;
    }

    // ---------- Độc giả ----------

    public void luuDanhSachDocGia(List<DocGia> dsDocGia) {
        Document doc = taoDocumentMoi();
        Element root = doc.createElement("DanhSachDocGia");
        doc.appendChild(root);

        for (DocGia dg : dsDocGia) {
            Element node = doc.createElement("DocGia");
            node.setAttribute("maDocGia", String.valueOf(dg.getMaDocGia()));
            node.setAttribute("loaiDocGia", dg.getLoaiDocGia());
            node.appendChild(taoElementText(doc, "hoTen", dg.getHoTen()));
            node.appendChild(taoElementText(doc, "soCCCD", dg.getSoCCCD()));
            node.appendChild(taoElementText(doc, "soDienThoai", dg.getSoDienThoai()));
            node.appendChild(taoElementText(doc, "lopHoacDonVi", dg.getLopHoacDonVi()));
            root.appendChild(node);
        }
        ghiFileXml(doc, FILE_DOC_GIA);
    }

    public List<DocGia> docDanhSachDocGia() {
        List<DocGia> list = new ArrayList<>();
        File file = new File(FILE_DOC_GIA);
        if (!file.exists()) return list;

        NodeList nodes = docFileXml(file).getElementsByTagName("DocGia");
        try {
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                int ma = Integer.parseInt(el.getAttribute("maDocGia"));
                String hoTen = layTextTag(el, "hoTen");
                String cccd = layTextTag(el, "soCCCD");
                String sdt = layTextTag(el, "soDienThoai");
                String lop = layTextTag(el, "lopHoacDonVi");

                // loaiDocGia không phải "Giáo viên" thì coi là học sinh
                if ("Giáo viên".equalsIgnoreCase(el.getAttribute("loaiDocGia"))) {
                    list.add(new GiaoVien(ma, hoTen, cccd, sdt, lop));
                } else {
                    list.add(new HocSinh(ma, hoTen, cccd, sdt, lop));
                }
            }
        } catch (NumberFormatException e) {
            throw new DuLieuException("File docgia.xml có mã độc giả không phải số", e);
        }
        return list;
    }

    // ---------- Phiếu mượn ----------

    public void luuDanhSachPhieuMuon(List<PhieuMuon> dsPhieu) {
        Document doc = taoDocumentMoi();
        Element root = doc.createElement("DanhSachPhieuMuon");
        doc.appendChild(root);

        for (PhieuMuon pm : dsPhieu) {
            Element node = doc.createElement("PhieuMuon");
            node.setAttribute("maPhieu", String.valueOf(pm.getMaPhieu()));
            node.appendChild(taoElementText(doc, "maDocGia", String.valueOf(pm.getDocGia().getMaDocGia())));
            node.appendChild(taoElementText(doc, "maSach", String.valueOf(pm.getSach().getMaSach())));
            node.appendChild(taoElementText(doc, "ngayMuon", SDF.format(pm.getNgayMuon().getTime())));
            node.appendChild(taoElementText(doc, "hanTra", SDF.format(pm.getHanTra().getTime())));
            node.appendChild(taoElementText(doc, "ngayTra", pm.getNgayTra() != null ? SDF.format(pm.getNgayTra().getTime()) : ""));
            root.appendChild(node);
        }
        ghiFileXml(doc, FILE_PHIEU_MUON);
    }

    /** Phiếu nào trỏ tới độc giả/sách không còn tồn tại thì bỏ qua. */
    public List<PhieuMuon> docDanhSachPhieuMuon(List<DocGia> dsDocGia, List<Sach> dsSach) {
        List<PhieuMuon> list = new ArrayList<>();
        File file = new File(FILE_PHIEU_MUON);
        if (!file.exists()) return list;

        NodeList nodes = docFileXml(file).getElementsByTagName("PhieuMuon");
        try {
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                int maPhieu = Integer.parseInt(el.getAttribute("maPhieu"));
                int maDG = Integer.parseInt(layTextTag(el, "maDocGia"));
                int maSach = Integer.parseInt(layTextTag(el, "maSach"));

                DocGia dg = dsDocGia.stream().filter(d -> d.getMaDocGia() == maDG).findFirst().orElse(null);
                Sach sach = dsSach.stream().filter(s -> s.getMaSach() == maSach).findFirst().orElse(null);
                if (dg == null || sach == null) continue;

                Calendar calMuon = Calendar.getInstance();
                calMuon.setTime(SDF.parse(layTextTag(el, "ngayMuon")));
                Calendar calHan = Calendar.getInstance();
                calHan.setTime(SDF.parse(layTextTag(el, "hanTra")));

                String strTra = layTextTag(el, "ngayTra");
                Calendar calTra = null;
                if (!strTra.isEmpty()) {
                    calTra = Calendar.getInstance();
                    calTra.setTime(SDF.parse(strTra));
                }
                list.add(new PhieuMuon(maPhieu, dg, sach, calMuon, calHan, calTra));
            }
        } catch (NumberFormatException | java.text.ParseException e) {
            throw new DuLieuException("File phieumuon.xml có mã hoặc ngày tháng sai định dạng", e);
        }
        return list;
    }

    public boolean daCoDuLieuXml() {
        return new File(FILE_SACH).exists() || new File(FILE_DOC_GIA).exists();
    }

    // ---------- Xuất báo cáo ----------

    public void xuatBaoCaoInHTML(File file, List<Sach> dsSach, List<PhieuMuon> dsPhieu,
                                 String thongKeTomTat) throws IOException {
        DecimalFormat df = new DecimalFormat("#,###");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'>");
            pw.println("<title>Báo Cáo Thư Viện Trường Học</title>");
            pw.println("<style>body{font-family:Arial,sans-serif;margin:30px;} table{width:100%;border-collapse:collapse;margin-top:15px;}");
            pw.println("th,td{border:1px solid #444;padding:8px;text-align:left;} th{background:#f0f0f0;} .red{color:red;font-weight:bold;}</style></head><body>");
            pw.println("<h2>BÁO CÁO THỐNG KÊ HOẠT ĐỘNG THƯ VIỆN TRƯỜNG HỌC</h2>");
            pw.println("<p><strong>Ngày xuất báo cáo:</strong> " + SDF.format(Calendar.getInstance().getTime()) + "</p>");
            pw.println("<p>" + thongKeTomTat + "</p>");

            pw.println("<h3>Danh Sách Phiếu Mượn – Trả</h3><table>");
            pw.println("<tr><th>Mã Phiếu</th><th>Độc Giả</th><th>Đối Tượng</th><th>Sách Mượn</th><th>Ngày Mượn</th><th>Hạn Trả</th><th>Ngày Trả</th><th>Tiền Phạt</th><th>Trạng Thái</th></tr>");
            for (PhieuMuon pm : dsPhieu) {
                String ngayTra = pm.getNgayTra() != null ? SDF.format(pm.getNgayTra().getTime()) : "Chưa trả";
                String tt = pm.getNgayTra() != null ? "Đã trả" : (pm.isQuaHan() ? "<span class='red'>Quá hạn (" + pm.getSoNgayQuaHan() + " ngày)</span>" : "Đang mượn");
                pw.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s VNĐ</td><td>%s</td></tr>%n",
                        pm.getMaPhieu(), pm.getDocGia().getHoTen(), pm.getDocGia().getLoaiDocGia(),
                        pm.getSach().getTenSach(), SDF.format(pm.getNgayMuon().getTime()),
                        SDF.format(pm.getHanTra().getTime()), ngayTra, df.format(pm.getTienPhat()), tt);
            }
            pw.println("</table><script>window.onload=function(){window.print();}</script></body></html>");
        }
    }
}
