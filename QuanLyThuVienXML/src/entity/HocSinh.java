package entity;

public class HocSinh extends DocGia {
    public HocSinh(int maDocGia, String hoTen, String soCCCD, String soDienThoai, String lop) {
        super(maDocGia, hoTen, soCCCD, soDienThoai, lop);
    }

    @Override
    public String getLoaiDocGia() {
        return "Học sinh";
    }

    @Override
    public int getSoNgayMuonToiDa() {
        return 7;
    }

    @Override
    public double tinhTienPhat(long soNgayQuaHan) {
        return Math.max(0, soNgayQuaHan) * 5000.0;
    }
}
