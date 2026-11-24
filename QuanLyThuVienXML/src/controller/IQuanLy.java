package controller;

import java.util.List;

public interface IQuanLy<T> {
    List<T> layTatCa();
    int layIdTiepTheo();
    String them(T item);
    String sua(T item);
    String xoa(int id);
    List<T> timKiem(String tuKhoa, double minSo, double maxSo);
}
