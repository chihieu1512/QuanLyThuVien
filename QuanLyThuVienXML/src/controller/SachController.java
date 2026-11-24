package controller;

import dao.DuLieuException;
import dao.XmlDatabaseDAO;
import entity.Sach;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SachController implements IQuanLy<Sach> {
    private final List<Sach> dsSach;
    private final XmlDatabaseDAO dao;

    public SachController(List<Sach> dsSach, XmlDatabaseDAO dao) {
        this.dsSach = dsSach;
        this.dao = dao;
    }

    // Bỏ dấu tiếng Việt + chữ thường để tìm "giai tich" vẫn ra "Giải Tích".
    // Chữ đ/Đ không tách dấu được bằng Normalizer nên phải đổi riêng.
    public static String boDau(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s.trim().toLowerCase(), Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(temp).replaceAll("").replace('đ', 'd');
    }

    @Override
    public List<Sach> layTatCa() {
        return dsSach;
    }

    @Override
    public int layIdTiepTheo() {
        int max = 0;
        for (Sach s : dsSach) {
            if (s.getMaSach() > max) max = s.getMaSach();
        }
        return max + 1;
    }

    @Override
    public String them(Sach sachMoi) {
        if (sachMoi.getTenSach().isEmpty() || sachMoi.getNhaXuatBan().isEmpty()) {
            return "Tên sách và Nhà xuất bản không được để trống!";
        }
        if (sachMoi.getGiaBia() <= 0) {
            return "Giá bìa phải lớn hơn 0!";
        }
        for (Sach s : dsSach) {
            if (s.getMaSach() == sachMoi.getMaSach()) {
                return "Lỗi trùng mã sách (ID = " + sachMoi.getMaSach() + ")!";
            }
            if (s.getTenSach().equalsIgnoreCase(sachMoi.getTenSach())
                    && s.getNhaXuatBan().equalsIgnoreCase(sachMoi.getNhaXuatBan())) {
                return "Sách này của cùng Nhà xuất bản đã tồn tại trong thư viện!";
            }
        }
        dsSach.add(sachMoi);
        try {
            dao.luuDanhSachSach(dsSach);
        } catch (DuLieuException e) {
            dsSach.remove(sachMoi); // ghi file lỗi thì bỏ luôn khỏi bộ nhớ cho khớp với file
            return "Không lưu được dữ liệu: " + e.getMessage();
        }
        return null;
    }

    @Override
    public String sua(Sach sachCapNhat) {
        if (sachCapNhat.getTenSach().isEmpty() || sachCapNhat.getNhaXuatBan().isEmpty()) {
            return "Tên sách và Nhà xuất bản không được để trống!";
        }
        if (sachCapNhat.getGiaBia() <= 0) {
            return "Giá bìa phải lớn hơn 0!";
        }
        for (Sach s : dsSach) {
            if (s.getMaSach() != sachCapNhat.getMaSach()
                    && s.getTenSach().equalsIgnoreCase(sachCapNhat.getTenSach())
                    && s.getNhaXuatBan().equalsIgnoreCase(sachCapNhat.getNhaXuatBan())) {
                return "Trùng thông tin với một đầu sách khác!";
            }
        }
        for (Sach s : dsSach) {
            if (s.getMaSach() == sachCapNhat.getMaSach()) {
                // giữ lại giá trị cũ để trả về nếu ghi file lỗi
                String tenCu = s.getTenSach(), loaiCu = s.getTheLoai(), nxbCu = s.getNhaXuatBan();
                double giaCu = s.getGiaBia();
                s.setTenSach(sachCapNhat.getTenSach());
                s.setTheLoai(sachCapNhat.getTheLoai());
                s.setNhaXuatBan(sachCapNhat.getNhaXuatBan());
                s.setGiaBia(sachCapNhat.getGiaBia());
                try {
                    dao.luuDanhSachSach(dsSach);
                } catch (DuLieuException e) {
                    s.setTenSach(tenCu);
                    s.setTheLoai(loaiCu);
                    s.setNhaXuatBan(nxbCu);
                    s.setGiaBia(giaCu);
                    return "Không lưu được dữ liệu: " + e.getMessage();
                }
                return null;
            }
        }
        return "Không tìm thấy sách cần sửa!";
    }

    @Override
    public String xoa(int maSach) {
        for (int i = 0; i < dsSach.size(); i++) {
            Sach s = dsSach.get(i);
            if (s.getMaSach() == maSach) {
                if (s.isDangMuon()) {
                    return "Không thể xóa sách đang được độc giả mượn!";
                }
                dsSach.remove(i);
                try {
                    dao.luuDanhSachSach(dsSach);
                } catch (DuLieuException e) {
                    dsSach.add(i, s);
                    return "Không lưu được dữ liệu: " + e.getMessage();
                }
                return null;
            }
        }
        return "Không tìm thấy sách cần xóa!";
    }

    @Override
    public List<Sach> timKiem(String tuKhoa, double giaTu, double giaDen) {
        List<Sach> ketQua = new ArrayList<>();
        String kw = boDau(tuKhoa);
        for (Sach s : dsSach) {
            boolean khopChu = kw.isEmpty()
                    || boDau(s.getTenSach()).contains(kw)
                    || boDau(s.getTheLoai()).contains(kw)
                    || boDau(s.getNhaXuatBan()).contains(kw)
                    || String.valueOf(s.getMaSach()).contains(kw);
            boolean khopGia = s.getGiaBia() >= giaTu && s.getGiaBia() <= giaDen;
            if (khopChu && khopGia) {
                ketQua.add(s);
            }
        }
        return ketQua;
    }

    public Sach timSachGiaCaoNhat() {
        Sach max = null;
        for (Sach s : dsSach) {
            if (max == null || s.getGiaBia() > max.getGiaBia()) max = s;
        }
        return max;
    }

    public Sach timSachGiaThapNhat() {
        Sach min = null;
        for (Sach s : dsSach) {
            if (min == null || s.getGiaBia() < min.getGiaBia()) min = s;
        }
        return min;
    }

    public double tinhTongGiaTriKhoSach() {
        double sum = 0;
        for (Sach s : dsSach) sum += s.getGiaBia();
        return sum;
    }
}
