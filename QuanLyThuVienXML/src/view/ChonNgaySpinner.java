package view;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Ô chọn ngày dùng thay cho JSpinner thường. Vẫn gõ trực tiếp hoặc bấm mũi tên
 * tăng/giảm ngày được như cũ, thêm nút lịch bên phải để chọn ngày/tháng/năm
 * trên bảng lịch. Giá trị lấy ra vẫn là java.util.Date qua getValue().
 */
public class ChonNgaySpinner extends JSpinner {
    private static final String[] TEN_THU = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
    private JButton btnLich;

    public ChonNgaySpinner() {
        super(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor oNhap = new JSpinner.DateEditor(this, "dd/MM/yyyy");

        btnLich = new JButton(new BieuTuongLich());
        btnLich.setToolTipText("Chọn ngày trên lịch");
        btnLich.setFocusable(false);
        btnLich.setMargin(new Insets(0, 2, 0, 2));
        btnLich.setPreferredSize(new Dimension(28, 24));
        btnLich.addActionListener(e -> hienLich());

        JPanel pnl = new JPanel(new BorderLayout(2, 0));
        pnl.setOpaque(false);
        pnl.add(oNhap, BorderLayout.CENTER);
        pnl.add(btnLich, BorderLayout.EAST);
        setEditor(pnl);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (btnLich != null) btnLich.setEnabled(b);
    }

    private void hienLich() {
        Calendar dangChon = Calendar.getInstance();
        dangChon.setTime((Date) getValue());

        JPopupMenu popup = new JPopupMenu();
        popup.add(new BangLich(dangChon, ngay -> {
            setValue(ngay.getTime());
            popup.setVisible(false);
        }));
        popup.show(btnLich, btnLich.getWidth() - popup.getPreferredSize().width, btnLich.getHeight());
    }

    /** Bảng lịch của một tháng, có nút đổi tháng/năm và nút "Hôm nay". */
    static class BangLich extends JPanel {
        private final Calendar ngayGoc;       // giữ lại giờ phút của giá trị cũ
        private final Calendar thangDangXem;  // luôn trỏ vào ngày 1 của tháng đang xem
        private final Consumer<Calendar> khiChon;
        private final JLabel lblThang = new JLabel("", SwingConstants.CENTER);
        private final JPanel luoi = new JPanel(new GridLayout(0, 7, 2, 2));

        BangLich(Calendar ngayDangChon, Consumer<Calendar> khiChon) {
            super(new BorderLayout(4, 4));
            this.ngayGoc = (Calendar) ngayDangChon.clone();
            this.thangDangXem = (Calendar) ngayDangChon.clone();
            this.thangDangXem.set(Calendar.DAY_OF_MONTH, 1);
            this.khiChon = khiChon;
            setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            JPanel dau = new JPanel(new BorderLayout(2, 0));
            JPanel trai = new JPanel(new GridLayout(1, 2, 2, 0));
            trai.add(nutDoi("\u00AB", -12));
            trai.add(nutDoi("\u2039", -1));
            JPanel phai = new JPanel(new GridLayout(1, 2, 2, 0));
            phai.add(nutDoi("\u203A", 1));
            phai.add(nutDoi("\u00BB", 12));
            lblThang.setFont(lblThang.getFont().deriveFont(Font.BOLD));
            dau.add(trai, BorderLayout.WEST);
            dau.add(lblThang, BorderLayout.CENTER);
            dau.add(phai, BorderLayout.EAST);

            JButton btnHomNay = new JButton("Hôm nay");
            btnHomNay.setFocusable(false);
            btnHomNay.addActionListener(e -> khiChon.accept(ghepNgay(Calendar.getInstance())));

            add(dau, BorderLayout.NORTH);
            add(luoi, BorderLayout.CENTER);
            add(btnHomNay, BorderLayout.SOUTH);
            veLai();
        }

        private JButton nutDoi(String chu, int soThang) {
            JButton b = new JButton(chu);
            b.setFocusable(false);
            b.setMargin(new Insets(0, 4, 0, 4));
            b.addActionListener(e -> {
                thangDangXem.add(Calendar.MONTH, soThang);
                veLai();
            });
            return b;
        }

        /** Lấy ngày/tháng/năm của "ngay" nhưng giữ giờ phút giây của giá trị ban đầu. */
        private Calendar ghepNgay(Calendar ngay) {
            Calendar kq = (Calendar) ngayGoc.clone();
            kq.set(ngay.get(Calendar.YEAR), ngay.get(Calendar.MONTH), ngay.get(Calendar.DAY_OF_MONTH));
            return kq;
        }

        private boolean cungNgay(Calendar a, Calendar b) {
            return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                    && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                    && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH);
        }

        private void veLai() {
            lblThang.setText("Tháng " + (thangDangXem.get(Calendar.MONTH) + 1) + " / " + thangDangXem.get(Calendar.YEAR));
            luoi.removeAll();
            for (String thu : TEN_THU) {
                JLabel l = new JLabel(thu, SwingConstants.CENTER);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                luoi.add(l);
            }
            // Calendar tính Chủ nhật = 1, còn lịch mình vẽ bắt đầu từ Thứ 2 nên phải lệch lại
            int soOTrong = (thangDangXem.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            for (int i = 0; i < soOTrong; i++) luoi.add(new JLabel(""));

            Calendar homNay = Calendar.getInstance();
            int soNgay = thangDangXem.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int d = 1; d <= soNgay; d++) {
                Calendar ngay = (Calendar) thangDangXem.clone();
                ngay.set(Calendar.DAY_OF_MONTH, d);
                JButton b = new JButton(String.valueOf(d));
                b.setFocusable(false);
                b.setMargin(new Insets(2, 2, 2, 2));
                if (cungNgay(ngay, ngayGoc)) {
                    b.setBackground(new Color(0x2E86DE));
                    b.setForeground(Color.WHITE);
                } else if (cungNgay(ngay, homNay)) {
                    b.setForeground(new Color(0xC0392B));
                }
                b.addActionListener(e -> khiChon.accept(ghepNgay(ngay)));
                luoi.add(b);
            }
            revalidate();
            repaint();
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.pack();
        }
    }

    /** Biểu tượng tờ lịch nhỏ, tự vẽ để khỏi phụ thuộc file ảnh hay font emoji. */
    private static class BieuTuongLich implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(c.isEnabled() ? new Color(0x444444) : Color.GRAY);
            g2.drawRect(x + 1, y + 3, 13, 11);
            g2.fillRect(x + 1, y + 3, 14, 3);
            g2.fillRect(x + 4, y + 1, 2, 3);
            g2.fillRect(x + 10, y + 1, 2, 3);
            for (int r = 0; r < 2; r++)
                for (int k = 0; k < 3; k++)
                    g2.fillRect(x + 3 + k * 4, y + 8 + r * 3, 2, 2);
            g2.dispose();
        }

        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    }
}
