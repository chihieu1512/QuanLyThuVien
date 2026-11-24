package entity;

import java.util.Calendar;

public class PhieuMuon {
    private int maPhieu;
    private DocGia docGia;
    private Sach sach;
    private Calendar ngayMuon;
    private Calendar hanTra;
    private Calendar ngayTra;

    public PhieuMuon(int maPhieu, DocGia docGia, Sach sach, Calendar ngayMuon, Calendar hanTra, Calendar ngayTra) {
        this.maPhieu = maPhieu;
        this.docGia = docGia;
        this.sach = sach;
        this.ngayMuon = (Calendar) ngayMuon.clone();
        this.hanTra = (Calendar) hanTra.clone();
        this.ngayTra = (ngayTra != null) ? (Calendar) ngayTra.clone() : null;
    }

    // Đưa giờ/phút/giây về 0 để so sánh theo ngày, không bị lệch vì giờ trong ngày
    public static Calendar chuanHoaNgay(Calendar c) {
        Calendar clone = (Calendar) c.clone();
        clone.set(Calendar.HOUR_OF_DAY, 0);
        clone.set(Calendar.MINUTE, 0);
        clone.set(Calendar.SECOND, 0);
        clone.set(Calendar.MILLISECOND, 0);
        return clone;
    }

    // Chưa trả thì so với hôm nay, đã trả thì so với ngày trả. Chưa quá hạn thì trả 0.
    public long getSoNgayQuaHan() {
        Calendar mocSoSanh = (ngayTra != null) ? ngayTra : Calendar.getInstance();
        long diff = chuanHoaNgay(mocSoSanh).getTimeInMillis() - chuanHoaNgay(hanTra).getTimeInMillis();
        long ngay = diff / (1000L * 60 * 60 * 24);
        return Math.max(0, ngay);
    }

    public double getTienPhat() {
        return docGia.tinhTienPhat(getSoNgayQuaHan());
    }

    public boolean isQuaHan() {
        return getSoNgayQuaHan() > 0;
    }

    // "Sắp đến hạn" = còn 0 đến 2 ngày nữa là tới hạn trả (và chưa trả)
    public boolean isSapDenHan() {
        if (ngayTra != null || isQuaHan()) return false;
        long diff = chuanHoaNgay(hanTra).getTimeInMillis() - chuanHoaNgay(Calendar.getInstance()).getTimeInMillis();
        long ngayConLai = diff / (1000L * 60 * 60 * 24);
        return ngayConLai >= 0 && ngayConLai <= 2;
    }

    public int getMaPhieu() {
        return maPhieu;
    }
    public void setMaPhieu(int maPhieu) {
        this.maPhieu = maPhieu;
    }
    public DocGia getDocGia() {
        return docGia;
    }
    public void setDocGia(DocGia docGia) {
        this.docGia = docGia;
    }
    public Sach getSach() {
        return sach;
    }
    public void setSach(Sach sach) {
        this.sach = sach;
    }
    public Calendar getNgayMuon() {
        return ngayMuon;
    }
    public void setNgayMuon(Calendar ngayMuon) {
        this.ngayMuon = ngayMuon;
    }
    public Calendar getHanTra() {
        return hanTra;
    }
    public void setHanTra(Calendar hanTra) {
        this.hanTra = hanTra;
    }
    public Calendar getNgayTra() {
        return ngayTra;
    }
    public void setNgayTra(Calendar ngayTra) {
        this.ngayTra = ngayTra;
    }
}
