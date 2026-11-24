# Quản lý Thư viện Trường học (lưu dữ liệu bằng XML)

Bài tập lớn môn Lập trình hướng đối tượng. Phần mềm quản lý sách, độc giả (học sinh, giáo viên) và phiếu mượn – trả của thư viện trường học, viết bằng Java Swing. Toàn bộ dữ liệu lưu trong 3 file XML ở thư mục `data/`: `sach.xml`, `docgia.xml`, `phieumuon.xml`.

Yêu cầu: JDK 23, Apache NetBeans 23 (project Maven).

## Tài khoản đăng nhập

| Tên đăng nhập | Mật khẩu | Quyền |
|---|---|---|
| admin | 123456 | Quản trị viên: thêm, sửa, xóa tất cả |
| thuthu | 123456 | Nhân viên thư viện: dùng được mọi chức năng, riêng các nút Xóa bị khóa |

## Chức năng chính

- Đăng nhập và phân quyền Admin / nhân viên.
- Quản lý sách, độc giả, phiếu mượn: thêm, sửa, xóa (xóa có hộp thoại xác nhận). Bấm vào một dòng trên bảng thì dữ liệu hiện lên các ô nhập để sửa hoặc xóa. Có thể bấm tiêu đề cột để sắp xếp.
- Mã sách, mã độc giả, mã phiếu là số nguyên, tự tăng.
- Ngày mượn, hạn trả, ngày trả chọn bằng ô ngày có lịch (bấm nút lịch bên phải ô ngày, hoặc gõ trực tiếp, hoặc bấm mũi tên tăng giảm).
- Thể loại sách, loại độc giả, độc giả và sách khi lập phiếu chọn từ danh sách xổ xuống.
- Quy định mượn: học sinh mượn tối đa 7 ngày, phạt 5.000đ/ngày trễ. Giáo viên mượn tối đa 14 ngày, phạt 3.000đ/ngày trễ.
- Cảnh báo tự động theo ngày hôm nay: số phiếu quá hạn và sắp đến hạn, phiếu được tô màu trên bảng.
- Tìm kiếm: theo chuỗi (gần đúng, không phân biệt hoa thường và dấu), theo khoảng số (giá bìa sách, tiền phạt của phiếu).
- Thống kê: tổng số đầu sách, đang mượn, quá hạn, tổng giá trị kho, sách giá cao nhất và thấp nhất, tổng tiền phạt, phiếu phạt cao nhất và thấp nhất, kèm biểu đồ cột.
- Xuất báo cáo ra file HTML để in (nút "Xuất File In Báo Cáo (.HTML)" ở tab 3).
- Báo lỗi rõ ràng khi nhập sai kiểu, bỏ trống, trùng CCCD hoặc trùng mã. Ghi file XML lỗi thì thao tác được hoàn tác, chương trình không bị dừng.
- Tiền hiển thị có dấu phân cách hàng nghìn.

## Cấu trúc mã nguồn

- `entity`: các lớp đối tượng (Nguoi → DocGia → HocSinh / GiaoVien, TaiKhoan, Sach, PhieuMuon).
- `dao`: `XmlDatabaseDAO` đọc/ghi XML, `DuLieuException` báo lỗi đọc/ghi.
- `controller`: xử lý nghiệp vụ cho sách, độc giả, phiếu mượn, cùng implement interface `IQuanLy<T>`.
- `view`: giao diện Swing (`LoginFrame` là màn hình chạy đầu tiên, `ChonNgaySpinner` là ô chọn ngày có lịch).

## Mở và chạy bằng NetBeans 23

1. Chọn File → Open Project, chọn thư mục `QuanLyThuVienXML` (thư mục có file `pom.xml`, NetBeans sẽ hiện biểu tượng Maven).
2. Chuột phải vào project, chọn Run (hoặc bấm F5). Lớp chạy chính là `view.LoginFrame`.

## Đóng gói và chạy file .jar

1. Trong NetBeans, chuột phải vào project, chọn Clean and Build. File `target/QuanLyThuVien.jar` được tạo ra.
2. Tạo một thư mục phần mềm, chép vào đó `QuanLyThuVien.jar`, thư mục `data/` và file `chay.bat`.
3. Bấm đúp `QuanLyThuVien.jar` hoặc `chay.bat` để chạy. Máy cần cài JDK 23 (hoặc mới hơn).

Thư mục `data/` phải nằm cạnh file jar. Nếu chưa có dữ liệu, chương trình tự tạo thư mục `data/` và nạp dữ liệu mẫu. Chạy bằng dòng lệnh: `java -jar QuanLyThuVien.jar`.
