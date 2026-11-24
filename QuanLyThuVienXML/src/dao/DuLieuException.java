package dao;

/** Lỗi khi đọc hoặc ghi file XML (file hỏng, không có quyền ghi, ổ đĩa đầy...). */
public class DuLieuException extends RuntimeException {
    public DuLieuException(String message, Throwable cause) {
        super(message, cause);
    }
}
