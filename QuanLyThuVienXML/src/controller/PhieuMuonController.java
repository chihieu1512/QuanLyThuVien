package controller;

import dao.DuLieuException;
import dao.XmlDatabaseDAO;
import entity.PhieuMuon;
import entity.Sach;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PhieuMuonController implements IQuanLy<PhieuMuon> {
    private final List<PhieuMuon> dsPhieu;
    private final List<Sach> dsSach;
    private final XmlDatabaseDAO dao;

    public PhieuMuonController(List<PhieuMuon> dsPhieu, List<Sach> dsSach, XmlDatabaseDAO dao) {
        this.dsPhieu = dsPhieu;
        this.dsSach = dsSach;
        this.dao = dao;
    }

    @Override
    public List<PhieuMuon> layTatCa() {
        return dsPhieu;
    }

    @Override
    public int layIdTiepTheo() {
        int max = 0;
        for (PhieuMuon pm : dsPhieu) {
            if (pm.getMaPhieu() > max) max = pm.getMaPhieu();
        }
        return max + 1;
    }

    @Override
    public String them(PhieuMuon pmMoi) {
        if (pmMoi.getDocGia() == null || pmMoi.getSach() == null) {
            return "Vui lòng chọn Độc giả và Sách có sẵn!";
        }
        if (pmMoi.getSach().isDangMuon()) {
            return "Cuốn sách này hiện đang được mượn!";
        }
        if (PhieuMuon.chuanHoaNgay(pmMoi.getHanTra()).before(PhieuMuon.chuanHoaNgay(pmMoi.getNgayMuon()))) {
            return "Hạn trả không được nhỏ hơn Ngày mượn!";
        }
        for (PhieuMuon pm : dsPhieu) {
            if (pm.getMaPhieu() == pmMoi.getMaPhieu()) {
                return "Trùng mã phiếu mượn!";
            }
        }
        pmMoi.getSach().setDangMuon(true);
        dsPhieu.add(pmMoi);
        try {
            dao.luuDanhSachSach(dsSach);
            dao.luuDanhSachPhieuMuon(dsPhieu);
        } catch (DuLieuException e) {
            pmMoi.getSach().setDangMuon(false);
            dsPhieu.remove(pmMoi);
            luuLaiSachKhongBao(); // lỡ sach.xml đã ghi xong mà phieumuon.xml lỗi thì ghi lại cho khớp
            return "Không lưu được dữ liệu: " + e.getMessage();
        }
        return null;
    }

    @Override
    public String sua(PhieuMuon pmCapNhat) {
        if (PhieuMuon.chuanHoaNgay(pmCapNhat.getHanTra()).before(PhieuMuon.chuanHoaNgay(pmCapNhat.getNgayMuon()))) {
            return "Hạn trả không được nhỏ hơn Ngày mượn!";
        }
        for (PhieuMuon pm : dsPhieu) {
            if (pm.getMaPhieu() == pmCapNhat.getMaPhieu()) {
                Calendar muonCu = pm.getNgayMuon(), hanCu = pm.getHanTra();
                pm.setNgayMuon(pmCapNhat.getNgayMuon());
                pm.setHanTra(pmCapNhat.getHanTra());
                try {
                    dao.luuDanhSachPhieuMuon(dsPhieu);
                } catch (DuLieuException e) {
                    pm.setNgayMuon(muonCu);
                    pm.setHanTra(hanCu);
                    return "Không lưu được dữ liệu: " + e.getMessage();
                }
                return null;
            }
        }
        return "Không tìm thấy phiếu mượn!";
    }

    public String xacNhanTraSach(int maPhieu, Calendar ngayTra) {
        for (PhieuMuon pm : dsPhieu) {
            if (pm.getMaPhieu() == maPhieu) {
                if (pm.getNgayTra() != null) {
                    return "Phiếu mượn này đã được trả trước đó!";
                }
                if (PhieuMuon.chuanHoaNgay(ngayTra).before(PhieuMuon.chuanHoaNgay(pm.getNgayMuon()))) {
                    return "Ngày trả không được trước Ngày mượn!";
                }
                pm.setNgayTra(ngayTra);
                pm.getSach().setDangMuon(false);
                try {
                    dao.luuDanhSachSach(dsSach);
                    dao.luuDanhSachPhieuMuon(dsPhieu);
                } catch (DuLieuException e) {
                    pm.setNgayTra(null);
                    pm.getSach().setDangMuon(true);
                    luuLaiSachKhongBao();
                    return "Không lưu được dữ liệu: " + e.getMessage();
                }
                return null;
            }
        }
        return "Không tìm thấy phiếu mượn!";
    }

    @Override
    public String xoa(int maPhieu) {
        for (int i = 0; i < dsPhieu.size(); i++) {
            PhieuMuon pm = dsPhieu.get(i);
            if (pm.getMaPhieu() == maPhieu) {
                boolean chuaTra = pm.getNgayTra() == null;
                if (chuaTra) pm.getSach().setDangMuon(false);
                dsPhieu.remove(i);
                try {
                    if (chuaTra) dao.luuDanhSachSach(dsSach);
                    dao.luuDanhSachPhieuMuon(dsPhieu);
                } catch (DuLieuException e) {
                    dsPhieu.add(i, pm);
                    if (chuaTra) {
                        pm.getSach().setDangMuon(true);
                        luuLaiSachKhongBao();
                    }
                    return "Không lưu được dữ liệu: " + e.getMessage();
                }
                return null;
            }
        }
        return "Không tìm thấy phiếu cần xóa!";
    }

    @Override
    public List<PhieuMuon> timKiem(String tuKhoa, double phatTu, double phatDen) {
        List<PhieuMuon> ketQua = new ArrayList<>();
        String kw = SachController.boDau(tuKhoa);
        for (PhieuMuon pm : dsPhieu) {
            boolean khopChu = kw.isEmpty()
                    || SachController.boDau(pm.getDocGia().getHoTen()).contains(kw)
                    || SachController.boDau(pm.getDocGia().getLopHoacDonVi()).contains(kw)
                    || SachController.boDau(pm.getSach().getTenSach()).contains(kw)
                    || String.valueOf(pm.getMaPhieu()).contains(kw);
            double tienPhat = pm.getTienPhat();
            boolean khopTienPhat = tienPhat >= phatTu && tienPhat <= phatDen;
            if (khopChu && khopTienPhat) {
                ketQua.add(pm);
            }
        }
        return ketQua;
    }

    /** Cố ghi lại sach.xml sau khi hoàn tác; nếu vẫn lỗi thì thôi, vì lỗi chính đã được báo rồi. */
    private void luuLaiSachKhongBao() {
        try {
            dao.luuDanhSachSach(dsSach);
        } catch (DuLieuException ignored) {
        }
    }

    public int demSachDangMuon() {
        int c = 0;
        for (PhieuMuon pm : dsPhieu) if (pm.getNgayTra() == null) c++;
        return c;
    }

    public int demPhieuQuaHan() {
        int c = 0;
        for (PhieuMuon pm : dsPhieu) if (pm.isQuaHan()) c++;
        return c;
    }

    public int demPhieuSapDenHan() {
        int c = 0;
        for (PhieuMuon pm : dsPhieu) if (pm.isSapDenHan()) c++;
        return c;
    }

    public double tinhTongTienPhat() {
        double tong = 0;
        for (PhieuMuon pm : dsPhieu) tong += pm.getTienPhat();
        return tong;
    }

    public PhieuMuon timPhieuPhatCaoNhat() {
        PhieuMuon max = null;
        for (PhieuMuon pm : dsPhieu) {
            if (pm.getTienPhat() > 0 && (max == null || pm.getTienPhat() > max.getTienPhat())) max = pm;
        }
        return max;
    }

    public PhieuMuon timPhieuPhatThapNhat() {
        PhieuMuon min = null;
        for (PhieuMuon pm : dsPhieu) {
            if (pm.getTienPhat() > 0 && (min == null || pm.getTienPhat() < min.getTienPhat())) min = pm;
        }
        return min;
    }
}
