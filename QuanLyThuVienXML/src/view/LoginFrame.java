package view;

import entity.TaiKhoan;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LoginFrame extends JFrame {
    private final List<TaiKhoan> dsTaiKhoan = new ArrayList<>();
    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginFrame() {
        dsTaiKhoan.add(new TaiKhoan("admin", "123456", "Bùi Thế Chí Hiếu", "001200000001", "0901000001", "ADMIN"));
        dsTaiKhoan.add(new TaiKhoan("thuthu", "123456", "Hà Phương Đông", "001200000002", "0901000002", "NHAN_VIEN"));

        setTitle("Đăng Nhập Hệ Thống Quản Lý Thư Viện");
        setSize(430, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("QUẢN LÝ THƯ VIỆN TRƯỜNG HỌC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(new Color(0, 90, 170));
        root.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 10));
        txtUser = new JTextField("admin");
        txtPass = new JPasswordField("123456");

        form.add(new JLabel("Tên đăng nhập:"));
        form.add(txtUser);
        form.add(new JLabel("Mật khẩu:"));
        form.add(txtPass);
        form.add(new JLabel("Gợi ý tài khoản:"));
        form.add(new JLabel("admin / 123456  hoặc  thuthu / 123456"));
        root.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        JButton btnLogin = new JButton("Đăng Nhập");
        JButton btnExit = new JButton("Thoát");
        btns.add(btnLogin);
        btns.add(btnExit);
        root.add(btns, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> xuLyDangNhap());
        txtPass.addActionListener(e -> xuLyDangNhap());
        btnExit.addActionListener(e -> System.exit(0));

        add(root);
    }

    private void xuLyDangNhap() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (TaiKhoan tk : dsTaiKhoan) {
            if (tk.getTenDangNhap().equals(u) && tk.getMatKhau().equals(p)) {
                try {
                    MainFrame main = new MainFrame(tk);
                    dispose();
                    main.setVisible(true);
                } catch (dao.DuLieuException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
