package entity;

public abstract class DocGia extends Nguoi {
    private int maDocGia;
    private String lopHoacDonVi;

    public DocGia(int maDocGia, String hoTen, String soCCCD, String soDienThoai, String lopHoacDonVi) {
        super(hoTen, soCCCD, soDienThoai);
        this.maDocGia = maDocGia;
        this.lopHoacDonVi = lopHoacDonVi;
    }

    // 3 hàm dưới để HocSinh và GiaoVien tự quy định (đa hình)
    public abstract String getLoaiDocGia();
    public abstract int getSoNgayMuonToiDa();
    public abstract double tinhTienPhat(long soNgayQuaHan);

    @Override
    public String getVaiTroHienThi() {
        return getLoaiDocGia() + " - " + lopHoacDonVi;
    }

    public int getMaDocGia() {
        return maDocGia;
    }
    public void setMaDocGia(int maDocGia) {
        this.maDocGia = maDocGia;
    }
    public String getLopHoacDonVi() {
        return lopHoacDonVi;
    }
    public void setLopHoacDonVi(String lopHoacDonVi) {
        this.lopHoacDonVi = lopHoacDonVi;
    }
    @Override
    public String toString() {
        return maDocGia + " - " + getHoTen() + " (" + getLoaiDocGia() + " - " + lopHoacDonVi + ")";
    }
}
