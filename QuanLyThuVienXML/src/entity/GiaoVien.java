package entity;

public class GiaoVien extends DocGia {
    public GiaoVien(int maDocGia, String hoTen, String soCCCD, String soDienThoai, String toBoMon) {
        super(maDocGia, hoTen, soCCCD, soDienThoai, toBoMon);
    }

    @Override
    public String getLoaiDocGia() {
        return "Giáo viên";
    }

    @Override
    public int getSoNgayMuonToiDa() {
        return 14;
    }

    @Override
    public double tinhTienPhat(long soNgayQuaHan) {
        return Math.max(0, soNgayQuaHan) * 3000.0;
    }
}
