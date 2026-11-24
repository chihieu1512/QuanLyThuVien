package entity;

public class TaiKhoan extends Nguoi {
    private String tenDangNhap;
    private String matKhau;
    private String quyen; // "ADMIN" hoặc "NHAN_VIEN"

    public TaiKhoan(String tenDangNhap, String matKhau, String hoTen, String soCCCD, String soDienThoai, String quyen) {
        super(hoTen, soCCCD, soDienThoai);
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyen = quyen;
    }

    @Override
    public String getVaiTroHienThi() {
        return "ADMIN".equalsIgnoreCase(quyen) ? "Quản trị viên (Admin)" : "Nhân viên thư viện";
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(quyen);
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }
    public String getMatKhau() {
        return matKhau;
    }
    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
    public String getQuyen() {
        return quyen;
    }
    public void setQuyen(String quyen) {
        this.quyen = quyen;
    }
}
