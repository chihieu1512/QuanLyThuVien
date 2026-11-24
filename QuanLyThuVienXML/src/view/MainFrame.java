package view;

import controller.DocGiaController;
import controller.PhieuMuonController;
import controller.SachController;
import dao.XmlDatabaseDAO;
import entity.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final TaiKhoan taiKhoanHienTai;
    private final XmlDatabaseDAO dao = new XmlDatabaseDAO();
    private final SachController sachCtrl;
    private final DocGiaController docGiaCtrl;
    private final PhieuMuonController phieuCtrl;

    private final DecimalFormat dfTien = new DecimalFormat("#,###");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private DefaultTableModel modelSach, modelDocGia, modelPhieu;
    private JTable tblSach, tblDocGia, tblPhieu;

    private JTextField txtMaSach, txtTenSach, txtNXB, txtGiaBia, txtTimSach, txtGiaTu, txtGiaDen;
    private JComboBox<String> cboTheLoai;

    private JTextField txtMaDG, txtHoTenDG, txtCCCD, txtSDT, txtLopDonVi, txtTimDG;
    private JComboBox<String> cboLoaiDG;

    private JTextField txtMaPhieu, txtTimPhieu, txtPhatTu, txtPhatDen;
    private JComboBox<DocGia> cboChonDocGia;
    private JComboBox<Sach> cboChonSach;
    private JSpinner spnNgayMuon, spnHanTra, spnNgayTra;
    private JLabel lblCanhBao, lblTK1, lblTK2, lblTK3, lblTK4;
    private BieuDoPanel bieuDoPanel;

    public MainFrame(TaiKhoan tk) {
        this.taiKhoanHienTai = tk;

        List<Sach> dsSach = dao.docDanhSachSach();
        List<DocGia> dsDocGia = dao.docDanhSachDocGia();
        List<PhieuMuon> dsPhieu = dao.docDanhSachPhieuMuon(dsDocGia, dsSach);

        this.sachCtrl = new SachController(dsSach, dao);
        this.docGiaCtrl = new DocGiaController(dsDocGia, dsPhieu, dao);
        this.phieuCtrl = new PhieuMuonController(dsPhieu, dsSach, dao);

        if (!dao.daCoDuLieuXml()) {
            khoiTaoDuLieuXmlMau();
        }

        setTitle("Hệ Thống Quản Lý Thư Viện (XML) - Xin chào: " + tk.getHoTen() + " [" + tk.getVaiTroHienThi() + "]");
        setSize(1200, 740);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        topBar.setBackground(new Color(235, 243, 252));

        lblCanhBao = new JLabel();
        lblCanhBao.setFont(new Font("SansSerif", Font.BOLD, 13));
        JButton btnDangXuat = new JButton("Đăng Xuất");
        btnDangXuat.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        topBar.add(lblCanhBao, BorderLayout.CENTER);
        topBar.add(btnDangXuat, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("1. Quản Lý Sách", taoTabSach());
        tabs.addTab("2. Quản Lý Độc Giả", taoTabDocGia());
        tabs.addTab("3. Mượn – Trả & Thống Kê", taoTabMuonTraThongKe());

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        capNhatToanBo();
        hienThiPopupCanhBaoKhiMo();
    }

    private JPanel taoTabSach() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 10, 8));
        pnlForm.setBorder(new TitledBorder("Thông Tin Sách (ID Tự Động Tăng)"));

        txtMaSach = new JTextField();
        txtMaSach.setEditable(false);
        txtMaSach.setBackground(new Color(235, 235, 235));
        txtTenSach = new JTextField();
        cboTheLoai = new JComboBox<>(new String[]{"Giáo trình", "Sách giáo khoa", "Tham khảo", "Tiểu thuyết", "Tin học", "Kỹ năng sống"});
        txtNXB = new JTextField();
        txtGiaBia = new JTextField();

        JButton btnThem = new JButton("Thêm Sách");
        JButton btnSua = new JButton("Sửa Sách");
        JButton btnXoa = new JButton("Xóa Sách");
        JButton btnMoi = new JButton("Làm Mới Form");
        btnXoa.setEnabled(taiKhoanHienTai.isAdmin());

        pnlForm.add(new JLabel("Mã sách (ID tự động):")); pnlForm.add(txtMaSach);
        pnlForm.add(new JLabel("Tên sách (*):")); pnlForm.add(txtTenSach);
        pnlForm.add(new JLabel("Thể loại:")); pnlForm.add(cboTheLoai);
        pnlForm.add(new JLabel("Nhà xuất bản (*):")); pnlForm.add(txtNXB);
        pnlForm.add(new JLabel("Giá bìa VNĐ (*):")); pnlForm.add(txtGiaBia);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlBtn.add(btnThem); pnlBtn.add(btnSua); pnlBtn.add(btnXoa); pnlBtn.add(btnMoi);
        pnlForm.add(new JLabel("Thao tác:")); pnlForm.add(pnlBtn);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlSearch.setBorder(new TitledBorder("Tìm Kiếm Sách (Gần Đúng Theo Chuỗi & Khoảng Giá Bìa)"));
        txtTimSach = new JTextField(18);
        txtGiaTu = new JTextField(8);
        txtGiaDen = new JTextField(8);
        JButton btnTim = new JButton("Tìm Kiếm");
        JButton btnHienAll = new JButton("Tất Cả");

        pnlSearch.add(new JLabel("Từ khóa (Tên/Loại/NXB):")); pnlSearch.add(txtTimSach);
        pnlSearch.add(new JLabel("Giá từ:")); pnlSearch.add(txtGiaTu);
        pnlSearch.add(new JLabel("đến:")); pnlSearch.add(txtGiaDen);
        pnlSearch.add(btnTim); pnlSearch.add(btnHienAll);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(pnlForm, BorderLayout.NORTH);
        top.add(pnlSearch, BorderLayout.SOUTH);

        modelSach = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Thể Loại", "Nhà Xuất Bản", "Giá Bìa (VNĐ)", "Trạng Thái"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };
        tblSach = new JTable(modelSach);
        tblSach.setAutoCreateRowSorter(true);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblSach), BorderLayout.CENTER);

        tblSach.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblSach.getSelectedRow();
                if (viewRow >= 0) {
                    int row = tblSach.convertRowIndexToModel(viewRow);
                    int ma = (int) modelSach.getValueAt(row, 0);
                    for (Sach s : sachCtrl.layTatCa()) {
                        if (s.getMaSach() == ma) {
                            txtMaSach.setText(String.valueOf(s.getMaSach()));
                            txtTenSach.setText(s.getTenSach());
                            cboTheLoai.setSelectedItem(s.getTheLoai());
                            txtNXB.setText(s.getNhaXuatBan());
                            txtGiaBia.setText(String.valueOf((long) s.getGiaBia()));
                            break;
                        }
                    }
                }
            }
        });

        btnThem.addActionListener(e -> {
            try {
                int id = sachCtrl.layIdTiepTheo();
                double gia = Double.parseDouble(txtGiaBia.getText().trim());
                Sach s = new Sach(id, txtTenSach.getText().trim(), cboTheLoai.getSelectedItem().toString(), txtNXB.getText().trim(), gia, false);
                String err = sachCtrl.them(s);
                if (err != null) JOptionPane.showMessageDialog(this, err, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                else { lamMoiFormSach(); capNhatToanBo(); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Giá bìa phải là số hợp lệ!", "Lỗi kiểu dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            if (tblSach.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng sách trên bảng để sửa!");
                return;
            }
            try {
                int id = Integer.parseInt(txtMaSach.getText().trim());
                double gia = Double.parseDouble(txtGiaBia.getText().trim());
                Sach s = new Sach(id, txtTenSach.getText().trim(), cboTheLoai.getSelectedItem().toString(), txtNXB.getText().trim(), gia, false);
                String err = sachCtrl.sua(s);
                if (err != null) JOptionPane.showMessageDialog(this, err, "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
                else { lamMoiFormSach(); capNhatToanBo(); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Giá bìa phải là số hợp lệ!", "Lỗi kiểu dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            if (tblSach.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!");
                return;
            }
            int id = Integer.parseInt(txtMaSach.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách mã #" + id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String err = sachCtrl.xoa(id);
                if (err != null) JOptionPane.showMessageDialog(this, err, "Không thể xóa", JOptionPane.ERROR_MESSAGE);
                else { lamMoiFormSach(); capNhatToanBo(); }
            }
        });

        btnMoi.addActionListener(e -> lamMoiFormSach());

        btnTim.addActionListener(e -> {
            try {
                double tu = txtGiaTu.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtGiaTu.getText().trim());
                double den = txtGiaDen.getText().trim().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(txtGiaDen.getText().trim());
                if (tu > den) {
                    JOptionPane.showMessageDialog(this, "Giá từ không được lớn hơn Giá đến!");
                    return;
                }
                doDuLieuBangSach(sachCtrl.timKiem(txtTimSach.getText(), tu, den));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Khoảng giá tìm kiếm phải là số!");
            }
        });

        btnHienAll.addActionListener(e -> {
            txtTimSach.setText(""); txtGiaTu.setText(""); txtGiaDen.setText("");
            doDuLieuBangSach(sachCtrl.layTatCa());
        });

        return panel;
    }

    private void lamMoiFormSach() {
        txtMaSach.setText(String.valueOf(sachCtrl.layIdTiepTheo()));
        txtTenSach.setText("");
        txtNXB.setText("");
        txtGiaBia.setText("");
        cboTheLoai.setSelectedIndex(0);
        tblSach.clearSelection();
    }

    private JPanel taoTabDocGia() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 10, 8));
        pnlForm.setBorder(new TitledBorder("Thông Tin Độc Giả (Kế thừa Nguoi -> DocGia -> HocSinh / GiaoVien)"));

        txtMaDG = new JTextField();
        txtMaDG.setEditable(false);
        txtMaDG.setBackground(new Color(235, 235, 235));
        cboLoaiDG = new JComboBox<>(new String[]{"Học sinh", "Giáo viên"});
        txtHoTenDG = new JTextField();
        txtCCCD = new JTextField();
        txtSDT = new JTextField();
        txtLopDonVi = new JTextField();

        JButton btnThem = new JButton("Thêm Độc Giả");
        JButton btnSua = new JButton("Sửa Độc Giả");
        JButton btnXoa = new JButton("Xóa Độc Giả");
        JButton btnMoi = new JButton("Làm Mới Form");
        btnXoa.setEnabled(taiKhoanHienTai.isAdmin());

        pnlForm.add(new JLabel("Mã độc giả (ID tự động):")); pnlForm.add(txtMaDG);
        pnlForm.add(new JLabel("Đối tượng (Đa hình):")); pnlForm.add(cboLoaiDG);
        pnlForm.add(new JLabel("Họ và tên (*):")); pnlForm.add(txtHoTenDG);
        pnlForm.add(new JLabel("Số CCCD / Mã thẻ (*):")); pnlForm.add(txtCCCD);
        pnlForm.add(new JLabel("Số điện thoại:")); pnlForm.add(txtSDT);
        pnlForm.add(new JLabel("Lớp / Tổ bộ môn (*):")); pnlForm.add(txtLopDonVi);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlBtns.add(btnThem); pnlBtns.add(btnSua); pnlBtns.add(btnXoa); pnlBtns.add(btnMoi);
        pnlBtns.add(new JLabel("   * Học sinh: mượn 7 ngày, phạt 5.000đ/ngày | Giáo viên: mượn 14 ngày, phạt 3.000đ/ngày"));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlSearch.setBorder(new TitledBorder("Tìm Kiếm Độc Giả (Gần Đúng Không Phân Biệt Hoa Thường)"));
        txtTimDG = new JTextField(25);
        JButton btnTimDG = new JButton("Tìm Kiếm");
        JButton btnAllDG = new JButton("Hiển Thị Tất Cả");
        pnlSearch.add(new JLabel("Từ khóa (Tên/CCCD/Lớp):")); pnlSearch.add(txtTimDG);
        pnlSearch.add(btnTimDG); pnlSearch.add(btnAllDG);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(pnlForm, BorderLayout.NORTH);
        top.add(pnlBtns, BorderLayout.CENTER);
        top.add(pnlSearch, BorderLayout.SOUTH);

        modelDocGia = new DefaultTableModel(new String[]{"Mã ĐG", "Họ Tên", "Đối Tượng", "Số CCCD", "SĐT", "Lớp / Tổ", "Hạn Mượn"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };
        tblDocGia = new JTable(modelDocGia);
        tblDocGia.setAutoCreateRowSorter(true);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblDocGia), BorderLayout.CENTER);

        tblDocGia.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblDocGia.getSelectedRow();
                if (viewRow >= 0) {
                    int row = tblDocGia.convertRowIndexToModel(viewRow);
                    txtMaDG.setText(modelDocGia.getValueAt(row, 0).toString());
                    txtHoTenDG.setText(modelDocGia.getValueAt(row, 1).toString());
                    cboLoaiDG.setSelectedItem(modelDocGia.getValueAt(row, 2).toString());
                    txtCCCD.setText(modelDocGia.getValueAt(row, 3).toString());
                    txtSDT.setText(modelDocGia.getValueAt(row, 4).toString());
                    txtLopDonVi.setText(modelDocGia.getValueAt(row, 5).toString());
                }
            }
        });

        btnThem.addActionListener(e -> {
            int id = docGiaCtrl.layIdTiepTheo();
            DocGia dg = taoDoiTuongDocGia(id);
            String err = docGiaCtrl.them(dg);
            if (err != null) JOptionPane.showMessageDialog(this, err, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            else { lamMoiFormDG(); capNhatToanBo(); }
        });

        btnSua.addActionListener(e -> {
            if (tblDocGia.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả trên bảng để sửa!");
                return;
            }
            int id = Integer.parseInt(txtMaDG.getText().trim());
            DocGia dg = taoDoiTuongDocGia(id);
            String err = docGiaCtrl.sua(dg);
            if (err != null) JOptionPane.showMessageDialog(this, err, "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
            else { lamMoiFormDG(); capNhatToanBo(); }
        });

        btnXoa.addActionListener(e -> {
            if (tblDocGia.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần xóa!");
                return;
            }
            int id = Integer.parseInt(txtMaDG.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa độc giả mã #" + id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String err = docGiaCtrl.xoa(id);
                if (err != null) JOptionPane.showMessageDialog(this, err, "Không thể xóa", JOptionPane.ERROR_MESSAGE);
                else { lamMoiFormDG(); capNhatToanBo(); }
            }
        });

        btnMoi.addActionListener(e -> lamMoiFormDG());
        btnTimDG.addActionListener(e -> doDuLieuBangDocGia(docGiaCtrl.timKiem(txtTimDG.getText(), 0, Double.MAX_VALUE)));
        btnAllDG.addActionListener(e -> { txtTimDG.setText(""); doDuLieuBangDocGia(docGiaCtrl.layTatCa()); });

        return panel;
    }

    private DocGia taoDoiTuongDocGia(int id) {
        String loai = cboLoaiDG.getSelectedItem().toString();
        if ("Giáo viên".equals(loai)) {
            return new GiaoVien(id, txtHoTenDG.getText().trim(), txtCCCD.getText().trim(), txtSDT.getText().trim(), txtLopDonVi.getText().trim());
        }
        return new HocSinh(id, txtHoTenDG.getText().trim(), txtCCCD.getText().trim(), txtSDT.getText().trim(), txtLopDonVi.getText().trim());
    }

    private void lamMoiFormDG() {
        txtMaDG.setText(String.valueOf(docGiaCtrl.layIdTiepTheo()));
        txtHoTenDG.setText(""); txtCCCD.setText(""); txtSDT.setText(""); txtLopDonVi.setText("");
        cboLoaiDG.setSelectedIndex(0);
        tblDocGia.clearSelection();
    }

    private JPanel taoTabMuonTraThongKe() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 8, 6));
        pnlForm.setBorder(new TitledBorder("Quản Lý Phiếu Mượn – Trả (Thời gian chọn theo Calendar)"));

        txtMaPhieu = new JTextField();
        txtMaPhieu.setEditable(false);
        txtMaPhieu.setBackground(new Color(235, 235, 235));
        cboChonDocGia = new JComboBox<>();
        cboChonSach = new JComboBox<>();

        spnNgayMuon = taoDateSpinner();
        spnHanTra = taoDateSpinner();
        spnNgayTra = taoDateSpinner();

        cboChonDocGia.addActionListener(e -> capNhatHanTraTheoDocGia());
        spnNgayMuon.addChangeListener(e -> capNhatHanTraTheoDocGia());

        JButton btnLapPhieu = new JButton("Lập Phiếu Mượn");
        JButton btnSuaPhieu = new JButton("Sửa Hạn Trả");
        JButton btnTraSach = new JButton("Xác Nhận Trả Sách");
        JButton btnXoaPhieu = new JButton("Xóa Phiếu");
        btnXoaPhieu.setEnabled(taiKhoanHienTai.isAdmin());

        pnlForm.add(new JLabel("Mã phiếu (Tự động):")); pnlForm.add(txtMaPhieu);
        pnlForm.add(new JLabel("Chọn độc giả:")); pnlForm.add(cboChonDocGia);
        pnlForm.add(new JLabel("Chọn sách có sẵn:")); pnlForm.add(cboChonSach);
        pnlForm.add(new JLabel("Ngày mượn (Calendar):")); pnlForm.add(spnNgayMuon);
        pnlForm.add(new JLabel("Hạn trả (Calendar):")); pnlForm.add(spnHanTra);
        pnlForm.add(new JLabel("Ngày trả thực tế:")); pnlForm.add(spnNgayTra);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlActions.add(btnLapPhieu);
        pnlActions.add(btnSuaPhieu);
        pnlActions.add(btnTraSach);
        pnlActions.add(btnXoaPhieu);

        txtTimPhieu = new JTextField(14);
        txtPhatTu = new JTextField(7);
        txtPhatDen = new JTextField(7);
        JButton btnTimPhieu = new JButton("Tìm Phiếu");
        JButton btnAllPhieu = new JButton("Tất Cả");
        JButton btnInBaoCao = new JButton("Xuất File In Báo Cáo (.HTML)");

        pnlActions.add(new JLabel(" | Tìm (Tên/Sách):")); pnlActions.add(txtTimPhieu);
        pnlActions.add(new JLabel("Phạt từ:")); pnlActions.add(txtPhatTu);
        pnlActions.add(new JLabel("đến:")); pnlActions.add(txtPhatDen);
        pnlActions.add(btnTimPhieu);
        pnlActions.add(btnAllPhieu);

        // nút xuất báo cáo để hàng riêng, để chung thì bị tràn dòng và mất nút
        JPanel pnlXuat = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlXuat.add(btnInBaoCao);
        JPanel pnlNut = new JPanel(new GridLayout(2, 1));
        pnlNut.add(pnlActions);
        pnlNut.add(pnlXuat);

        JPanel top = new JPanel(new BorderLayout());
        top.add(pnlForm, BorderLayout.NORTH);
        top.add(pnlNut, BorderLayout.SOUTH);

        modelPhieu = new DefaultTableModel(new String[]{
                "Mã Phiếu", "Độc Giả", "Đối Tượng", "Sách Mượn", "Ngày Mượn", "Hạn Trả", "Ngày Trả", "Trễ (Ngày)", "Tiền Phạt (VNĐ)", "Trạng Thái"
        }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0 || columnIndex == 7) ? Integer.class : String.class;
            }
        };
        tblPhieu = new JTable(modelPhieu);
        tblPhieu.setAutoCreateRowSorter(true);

        tblPhieu.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String tt = table.getValueAt(row, 9).toString();
                    if (tt.startsWith("Quá hạn")) c.setBackground(new Color(255, 220, 220));
                    else if (tt.startsWith("Sắp đến hạn")) c.setBackground(new Color(255, 248, 210));
                    else if ("Đã trả".equals(tt)) c.setBackground(new Color(225, 248, 230));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JPanel bottomStats = new JPanel(new BorderLayout(10, 0));
        JPanel pnlTextStats = new JPanel(new GridLayout(4, 1, 4, 4));
        pnlTextStats.setBorder(new TitledBorder("Thống Kê Tổng Hợp (Tổng số, Lớn nhất, Nhỏ nhất, Tổng tiền)"));

        lblTK1 = new JLabel();
        lblTK2 = new JLabel();
        lblTK3 = new JLabel();
        lblTK4 = new JLabel();
        Font fStat = new Font("SansSerif", Font.BOLD, 13);
        lblTK1.setFont(fStat); lblTK2.setFont(fStat); lblTK3.setFont(fStat); lblTK4.setFont(fStat);
        lblTK3.setForeground(new Color(180, 30, 30));

        pnlTextStats.add(lblTK1);
        pnlTextStats.add(lblTK2);
        pnlTextStats.add(lblTK3);
        pnlTextStats.add(lblTK4);

        bieuDoPanel = new BieuDoPanel();
        bottomStats.add(pnlTextStats, BorderLayout.CENTER);
        bottomStats.add(bieuDoPanel, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblPhieu), BorderLayout.CENTER);
        panel.add(bottomStats, BorderLayout.SOUTH);

        tblPhieu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblPhieu.getSelectedRow();
                if (viewRow >= 0) {
                    int row = tblPhieu.convertRowIndexToModel(viewRow);
                    int ma = (int) modelPhieu.getValueAt(row, 0);
                    for (PhieuMuon pm : phieuCtrl.layTatCa()) {
                        if (pm.getMaPhieu() == ma) {
                            txtMaPhieu.setText(String.valueOf(pm.getMaPhieu()));
                            spnNgayMuon.setValue(pm.getNgayMuon().getTime());
                            spnHanTra.setValue(pm.getHanTra().getTime());
                            if (pm.getNgayTra() != null) spnNgayTra.setValue(pm.getNgayTra().getTime());
                            else spnNgayTra.setValue(Calendar.getInstance().getTime());
                            break;
                        }
                    }
                }
            }
        });

        btnLapPhieu.addActionListener(e -> {
            int id = phieuCtrl.layIdTiepTheo();
            DocGia dg = (DocGia) cboChonDocGia.getSelectedItem();
            Sach s = (Sach) cboChonSach.getSelectedItem();
            Calendar calMuon = layCalendarTuSpinner(spnNgayMuon);
            Calendar calHan = layCalendarTuSpinner(spnHanTra);

            String err = phieuCtrl.them(new PhieuMuon(id, dg, s, calMuon, calHan, null));
            if (err != null) JOptionPane.showMessageDialog(this, err, "Lỗi lập phiếu", JOptionPane.ERROR_MESSAGE);
            else capNhatToanBo();
        });

        btnSuaPhieu.addActionListener(e -> {
            if (tblPhieu.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu trên bảng để sửa!");
                return;
            }
            int id = Integer.parseInt(txtMaPhieu.getText().trim());
            Calendar calMuon = layCalendarTuSpinner(spnNgayMuon);
            Calendar calHan = layCalendarTuSpinner(spnHanTra);
            String err = phieuCtrl.sua(new PhieuMuon(id, null, null, calMuon, calHan, null));
            if (err != null) JOptionPane.showMessageDialog(this, err, "Lỗi sửa phiếu", JOptionPane.ERROR_MESSAGE);
            else capNhatToanBo();
        });

        btnTraSach.addActionListener(e -> {
            if (tblPhieu.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu mượn trên bảng để xác nhận trả!");
                return;
            }
            int id = Integer.parseInt(txtMaPhieu.getText().trim());
            Calendar calTra = layCalendarTuSpinner(spnNgayTra);
            String err = phieuCtrl.xacNhanTraSach(id, calTra);
            if (err != null) {
                JOptionPane.showMessageDialog(this, err, "Lỗi trả sách", JOptionPane.ERROR_MESSAGE);
            } else {
                capNhatToanBo();
                JOptionPane.showMessageDialog(this, "Đã xác nhận trả sách cho phiếu #" + id);
            }
        });

        btnXoaPhieu.addActionListener(e -> {
            if (tblPhieu.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xóa!");
                return;
            }
            int id = Integer.parseInt(txtMaPhieu.getText().trim());
            int cf = JOptionPane.showConfirmDialog(this, "Xác nhận xóa phiếu mượn #" + id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (cf == JOptionPane.YES_OPTION) {
                String err = phieuCtrl.xoa(id);
                if (err != null) JOptionPane.showMessageDialog(this, err);
                else capNhatToanBo();
            }
        });

        btnTimPhieu.addActionListener(e -> {
            try {
                double tu = txtPhatTu.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtPhatTu.getText().trim());
                double den = txtPhatDen.getText().trim().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(txtPhatDen.getText().trim());
                doDuLieuBangPhieu(phieuCtrl.timKiem(txtTimPhieu.getText(), tu, den));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Khoảng tiền phạt phải là số!");
            }
        });

        btnAllPhieu.addActionListener(e -> {
            txtTimPhieu.setText(""); txtPhatTu.setText(""); txtPhatDen.setText("");
            doDuLieuBangPhieu(phieuCtrl.layTatCa());
        });

        btnInBaoCao.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("BaoCaoThuVien.html"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    String tomTat = lblTK1.getText() + " | " + lblTK3.getText();
                    dao.xuatBaoCaoInHTML(f, sachCtrl.layTatCa(), phieuCtrl.layTatCa(), tomTat);
                    Desktop.getDesktop().browse(f.toURI());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xuất file in: " + ex.getMessage());
                }
            }
        });

        return panel;
    }

    private JSpinner taoDateSpinner() {
        return new ChonNgaySpinner();
    }

    private Calendar layCalendarTuSpinner(JSpinner spn) {
        Calendar c = Calendar.getInstance();
        c.setTime((Date) spn.getValue());
        return c;
    }

    private void capNhatHanTraTheoDocGia() {
        DocGia dg = (DocGia) cboChonDocGia.getSelectedItem();
        if (dg != null && spnNgayMuon != null && spnHanTra != null) {
            Calendar han = layCalendarTuSpinner(spnNgayMuon);
            han.add(Calendar.DAY_OF_MONTH, dg.getSoNgayMuonToiDa());
            spnHanTra.setValue(han.getTime());
        }
    }

    private void doDuLieuBangSach(List<Sach> list) {
        modelSach.setRowCount(0);
        for (Sach s : list) {
            modelSach.addRow(new Object[]{
                    s.getMaSach(), s.getTenSach(), s.getTheLoai(), s.getNhaXuatBan(),
                    dfTien.format(s.getGiaBia()), s.isDangMuon() ? "Đang mượn" : "Có sẵn"
            });
        }
    }

    private void doDuLieuBangDocGia(List<DocGia> list) {
        modelDocGia.setRowCount(0);
        for (DocGia dg : list) {
            modelDocGia.addRow(new Object[]{
                    dg.getMaDocGia(), dg.getHoTen(), dg.getLoaiDocGia(), dg.getSoCCCD(),
                    dg.getSoDienThoai(), dg.getLopHoacDonVi(), dg.getSoNgayMuonToiDa() + " ngày"
            });
        }
    }

    private void doDuLieuBangPhieu(List<PhieuMuon> list) {
        modelPhieu.setRowCount(0);
        for (PhieuMuon pm : list) {
            String ngayTra = pm.getNgayTra() != null ? sdf.format(pm.getNgayTra().getTime()) : "Chưa trả";
            String tt = pm.getNgayTra() != null ? "Đã trả"
                    : (pm.isQuaHan() ? "Quá hạn" : (pm.isSapDenHan() ? "Sắp đến hạn" : "Đang mượn"));
            modelPhieu.addRow(new Object[]{
                    pm.getMaPhieu(), pm.getDocGia().getHoTen(), pm.getDocGia().getLoaiDocGia(),
                    pm.getSach().getTenSach(), sdf.format(pm.getNgayMuon().getTime()),
                    sdf.format(pm.getHanTra().getTime()), ngayTra,
                    (int) pm.getSoNgayQuaHan(), dfTien.format(pm.getTienPhat()), tt
            });
        }
    }

    private void capNhatToanBo() {
        txtMaSach.setText(String.valueOf(sachCtrl.layIdTiepTheo()));
        txtMaDG.setText(String.valueOf(docGiaCtrl.layIdTiepTheo()));
        txtMaPhieu.setText(String.valueOf(phieuCtrl.layIdTiepTheo()));

        doDuLieuBangSach(sachCtrl.layTatCa());
        doDuLieuBangDocGia(docGiaCtrl.layTatCa());
        doDuLieuBangPhieu(phieuCtrl.layTatCa());

        cboChonDocGia.removeAllItems();
        for (DocGia dg : docGiaCtrl.layTatCa()) cboChonDocGia.addItem(dg);

        cboChonSach.removeAllItems();
        for (Sach s : sachCtrl.layTatCa()) {
            if (!s.isDangMuon()) cboChonSach.addItem(s);
        }

        int tongSach = sachCtrl.layTatCa().size();
        int dangMuon = phieuCtrl.demSachDangMuon();
        int quaHan = phieuCtrl.demPhieuQuaHan();
        int sapHan = phieuCtrl.demPhieuSapDenHan();

        Sach maxSach = sachCtrl.timSachGiaCaoNhat();
        Sach minSach = sachCtrl.timSachGiaThapNhat();
        PhieuMuon maxPhat = phieuCtrl.timPhieuPhatCaoNhat();
        PhieuMuon minPhat = phieuCtrl.timPhieuPhatThapNhat();

        lblTK1.setText("Tổng đầu sách: " + tongSach + " | Đang mượn: " + dangMuon + " | Quá hạn: " + quaHan + " | Tổng giá trị kho: " + dfTien.format(sachCtrl.tinhTongGiaTriKhoSach()) + " VNĐ");
        lblTK2.setText("Sách giá cao nhất: " + (maxSach != null ? maxSach.getTenSach() + " (" + dfTien.format(maxSach.getGiaBia()) + " VNĐ)" : "N/A")
                + "  |  Thấp nhất: " + (minSach != null ? minSach.getTenSach() + " (" + dfTien.format(minSach.getGiaBia()) + " VNĐ)" : "N/A"));
        lblTK3.setText("TỔNG TIỀN PHẠT THU ĐƯỢC: " + dfTien.format(phieuCtrl.tinhTongTienPhat()) + " VNĐ");
        lblTK4.setText("Phiếu phạt cao nhất: " + (maxPhat != null ? "#" + maxPhat.getMaPhieu() + " (" + dfTien.format(maxPhat.getTienPhat()) + " VNĐ)" : "0 VNĐ")
                + "  |  Phiếu phạt thấp nhất: " + (minPhat != null ? "#" + minPhat.getMaPhieu() + " (" + dfTien.format(minPhat.getTienPhat()) + " VNĐ)" : "0 VNĐ"));

        lblCanhBao.setText("CẢNH BÁO TỰ ĐỘNG HÔM NAY (" + sdf.format(Calendar.getInstance().getTime()) + "): Có " + quaHan + " phiếu QUÁ HẠN và " + sapHan + " phiếu SẮP ĐẾN HẠN trả!");
        lblCanhBao.setForeground(quaHan > 0 ? Color.RED : new Color(0, 120, 60));

        bieuDoPanel.capNhatSoLieu(tongSach, dangMuon, quaHan);
    }

    private void hienThiPopupCanhBaoKhiMo() {
        int quaHan = phieuCtrl.demPhieuQuaHan();
        int sapHan = phieuCtrl.demPhieuSapDenHan();
        if (quaHan > 0 || sapHan > 0) {
            JOptionPane.showMessageDialog(this,
                    "Hệ thống phát hiện tự động theo ngày hôm nay:\n- Số phiếu đang QUÁ HẠN trả: " + quaHan + "\n- Số phiếu SẮP ĐẾN HẠN (trong 2 ngày tới): " + sapHan,
                    "Cảnh Báo Tự Động Theo Ngày", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void khoiTaoDuLieuXmlMau() {
        Sach s1 = new Sach(1, "Giải Tích 12", "Sách giáo khoa", "NXB Giáo Dục", 45000, false);
        Sach s2 = new Sach(2, "Lập Trình Hướng Đối Tượng Java", "Giáo trình", "NXB Khoa Học Kỹ Thuật", 125000, false);
        Sach s3 = new Sach(3, "Dế Mèn Phiêu Lưu Ký", "Tiểu thuyết", "NXB Kim Đồng", 65000, false);
        Sach s4 = new Sach(4, "Cấu Trúc Dữ Liệu & Giải Thuật", "Tin học", "NXB Đại Học Quốc Gia", 180000, false);
        sachCtrl.them(s1); sachCtrl.them(s2); sachCtrl.them(s3); sachCtrl.them(s4);

        DocGia dg1 = new HocSinh(1, "Nguyễn Văn An", "001208001122", "0912345678", "12A1");
        DocGia dg2 = new GiaoVien(2, "Trần Thị Lan", "001185003344", "0987654321", "Tổ Toán - Tin");
        DocGia dg3 = new HocSinh(3, "Lê Hoàng Nam", "001209005566", "0905112233", "11B2");
        docGiaCtrl.them(dg1); docGiaCtrl.them(dg2); docGiaCtrl.them(dg3);

        Calendar muon1 = Calendar.getInstance();
        muon1.add(Calendar.DAY_OF_MONTH, -10);
        Calendar han1 = (Calendar) muon1.clone();
        han1.add(Calendar.DAY_OF_MONTH, 7);
        phieuCtrl.them(new PhieuMuon(1, dg1, s1, muon1, han1, null));

        Calendar muon2 = Calendar.getInstance();
        muon2.add(Calendar.DAY_OF_MONTH, -13);
        Calendar han2 = (Calendar) muon2.clone();
        han2.add(Calendar.DAY_OF_MONTH, 14);
        phieuCtrl.them(new PhieuMuon(2, dg2, s2, muon2, han2, null));
    }
}
