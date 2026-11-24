package controller;

import dao.DuLieuException;
import dao.XmlDatabaseDAO;
import entity.DocGia;
import entity.PhieuMuon;
import java.util.ArrayList;
import java.util.List;

public class DocGiaController implements IQuanLy<DocGia> {
    private final List<DocGia> dsDocGia;
    private final List<PhieuMuon> dsPhieuMuon;
    private final XmlDatabaseDAO dao;

    public DocGiaController(List<DocGia> dsDocGia, List<PhieuMuon> dsPhieuMuon, XmlDatabaseDAO dao) {
        this.dsDocGia = dsDocGia;
        this.dsPhieuMuon = dsPhieuMuon;
        this.dao = dao;
    }

    @Override
    public List<DocGia> layTatCa() {
        return dsDocGia;
    }

    @Override
    public int layIdTiepTheo() {
        int max = 0;
        for (DocGia dg : dsDocGia) {
            if (dg.getMaDocGia() > max) max = dg.getMaDocGia();
        }
        return max + 1;
    }

    @Override
    public String them(DocGia dgMoi) {
        if (dgMoi.getHoTen().isEmpty() || dgMoi.getSoCCCD().isEmpty() || dgMoi.getLopHoacDonVi().isEmpty()) {
            return "Họ tên, Số CCCD/Mã thẻ và Lớp/Đơn vị không được để trống!";
        }
        if (!dgMoi.getSoCCCD().matches("\\d{9,12}")) {
            return "Số CCCD/Mã thẻ phải gồm từ 9 đến 12 chữ số!";
        }
        if (!dgMoi.getSoDienThoai().isEmpty() && !dgMoi.getSoDienThoai().matches("\\d{9,11}")) {
            return "Số điện thoại chỉ gồm 9 đến 11 chữ số!";
        }
        for (DocGia dg : dsDocGia) {
            if (dg.getMaDocGia() == dgMoi.getMaDocGia()) {
                return "Lỗi trùng mã độc giả (ID = " + dgMoi.getMaDocGia() + ")!";
            }
            if (dg.getSoCCCD().equals(dgMoi.getSoCCCD())) {
                return "Số CCCD/Mã thẻ '" + dgMoi.getSoCCCD() + "' đã tồn tại!";
            }
        }
        dsDocGia.add(dgMoi);
        try {
            dao.luuDanhSachDocGia(dsDocGia);
        } catch (DuLieuException e) {
            dsDocGia.remove(dgMoi);
            return "Không lưu được dữ liệu: " + e.getMessage();
        }
        return null;
    }

    @Override
    public String sua(DocGia dgCapNhat) {
        if (dgCapNhat.getHoTen().isEmpty() || dgCapNhat.getSoCCCD().isEmpty() || dgCapNhat.getLopHoacDonVi().isEmpty()) {
            return "Vui lòng nhập đầy đủ thông tin bắt buộc!";
        }
        if (!dgCapNhat.getSoCCCD().matches("\\d{9,12}")) {
            return "Số CCCD/Mã thẻ phải gồm từ 9 đến 12 chữ số!";
        }
        if (!dgCapNhat.getSoDienThoai().isEmpty() && !dgCapNhat.getSoDienThoai().matches("\\d{9,11}")) {
            return "Số điện thoại chỉ gồm 9 đến 11 chữ số!";
        }
        for (DocGia dg : dsDocGia) {
            if (dg.getMaDocGia() != dgCapNhat.getMaDocGia() && dg.getSoCCCD().equals(dgCapNhat.getSoCCCD())) {
                return "Số CCCD/Mã thẻ '" + dgCapNhat.getSoCCCD() + "' đã thuộc về độc giả khác!";
            }
        }
        for (int i = 0; i < dsDocGia.size(); i++) {
            if (dsDocGia.get(i).getMaDocGia() == dgCapNhat.getMaDocGia()) {
                DocGia cu = dsDocGia.get(i);
                dsDocGia.set(i, dgCapNhat);
                // các phiếu mượn đang trỏ tới độc giả cũ thì phải trỏ sang bản mới
                for (PhieuMuon pm : dsPhieuMuon) {
                    if (pm.getDocGia().getMaDocGia() == cu.getMaDocGia()) {
                        pm.setDocGia(dgCapNhat);
                    }
                }
                try {
                    dao.luuDanhSachDocGia(dsDocGia);
                    dao.luuDanhSachPhieuMuon(dsPhieuMuon);
                } catch (DuLieuException e) {
                    dsDocGia.set(i, cu);
                    for (PhieuMuon pm : dsPhieuMuon) {
                        if (pm.getDocGia().getMaDocGia() == cu.getMaDocGia()) pm.setDocGia(cu);
                    }
                    return "Không lưu được dữ liệu: " + e.getMessage();
                }
                return null;
            }
        }
        return "Không tìm thấy độc giả cần sửa!";
    }

    @Override
    public String xoa(int maDocGia) {
        for (PhieuMuon pm : dsPhieuMuon) {
            if (pm.getDocGia().getMaDocGia() == maDocGia && pm.getNgayTra() == null) {
                return "Không thể xóa độc giả đang mượn sách chưa trả!";
            }
        }
        for (int i = 0; i < dsDocGia.size(); i++) {
            if (dsDocGia.get(i).getMaDocGia() == maDocGia) {
                DocGia daXoa = dsDocGia.remove(i);
                try {
                    dao.luuDanhSachDocGia(dsDocGia);
                } catch (DuLieuException e) {
                    dsDocGia.add(i, daXoa);
                    return "Không lưu được dữ liệu: " + e.getMessage();
                }
                return null;
            }
        }
        return "Không tìm thấy độc giả cần xóa!";
    }

    @Override
    public List<DocGia> timKiem(String tuKhoa, double minSo, double maxSo) {
        List<DocGia> kq = new ArrayList<>();
        String kw = SachController.boDau(tuKhoa);
        for (DocGia dg : dsDocGia) {
            boolean khop = kw.isEmpty()
                    || SachController.boDau(dg.getHoTen()).contains(kw)
                    || dg.getSoCCCD().contains(kw)
                    || SachController.boDau(dg.getLopHoacDonVi()).contains(kw)
                    || SachController.boDau(dg.getLoaiDocGia()).contains(kw)
                    || String.valueOf(dg.getMaDocGia()).contains(kw);
            if (khop) kq.add(dg);
        }
        return kq;
    }
}
