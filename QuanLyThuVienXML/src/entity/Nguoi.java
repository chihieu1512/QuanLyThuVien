package entity;

public abstract class Nguoi {
    private String hoTen;
    private String soCCCD;
    private String soDienThoai;

    public Nguoi() {}

    public Nguoi(String hoTen, String soCCCD, String soDienThoai) {
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.soDienThoai = soDienThoai;
    }

    public abstract String getVaiTroHienThi();

    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    public String getSoCCCD() {
        return soCCCD;
    }
    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }
    public String getSoDienThoai() {
        return soDienThoai;
    }
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
