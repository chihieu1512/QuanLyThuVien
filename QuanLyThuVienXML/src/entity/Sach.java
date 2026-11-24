package entity;

public class Sach {
    private int maSach;
    private String tenSach;
    private String theLoai;
    private String nhaXuatBan;
    private double giaBia;
    private boolean dangMuon;

    public Sach(int maSach, String tenSach, String theLoai, String nhaXuatBan, double giaBia, boolean dangMuon) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.theLoai = theLoai;
        this.nhaXuatBan = nhaXuatBan;
        this.giaBia = giaBia;
        this.dangMuon = dangMuon;
    }

    public int getMaSach() {
        return maSach;
    }
    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }
    public String getTenSach() {
        return tenSach;
    }
    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }
    public String getTheLoai() {
        return theLoai;
    }
    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }
    public String getNhaXuatBan() {
        return nhaXuatBan;
    }
    public void setNhaXuatBan(String nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }
    public double getGiaBia() {
        return giaBia;
    }
    public void setGiaBia(double giaBia) {
        this.giaBia = giaBia;
    }
    public boolean isDangMuon() {
        return dangMuon;
    }
    public void setDangMuon(boolean dangMuon) {
        this.dangMuon = dangMuon;
    }

    @Override
    public String toString() {
        return maSach + " - " + tenSach + " (" + theLoai + ")";
    }
}
