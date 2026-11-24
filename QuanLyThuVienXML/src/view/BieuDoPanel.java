package view;

import javax.swing.*;
import java.awt.*;

public class BieuDoPanel extends JPanel {
    private int tongSach;
    private int dangMuon;
    private int quaHan;

    public BieuDoPanel() {
        setPreferredSize(new Dimension(420, 220));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Biểu Đồ Trạng Thái Kho Sách & Phiếu Mượn"));
    }

    public void capNhatSoLieu(int tongSach, int dangMuon, int quaHan) {
        this.tongSach = tongSach;
        this.dangMuon = dangMuon;
        this.quaHan = quaHan;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int coSan = Math.max(0, tongSach - dangMuon);
        int maxVal = Math.max(1, Math.max(tongSach, Math.max(dangMuon, quaHan)));

        String[] labels = {"Tổng sách", "Có sẵn", "Đang mượn", "Quá hạn"};
        int[] values = {tongSach, coSan, dangMuon, quaHan};
        Color[] colors = {
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(241, 196, 15),
                new Color(231, 76, 60)
        };

        int baseLineY = getHeight() - 35;
        int maxBarHeight = getHeight() - 75;
        int barWidth = 48;
        int startX = 40;
        int gap = 85;

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(25, baseLineY, getWidth() - 25, baseLineY);

        for (int i = 0; i < labels.length; i++) {
            int barHeight = (int) ((double) values[i] / maxVal * maxBarHeight);
            int x = startX + i * gap;
            int y = baseLineY - barHeight;

            g2.setColor(colors[i]);
            g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(String.valueOf(values[i]), x + barWidth / 2 - 5, y - 5);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString(labels[i], x - 4, baseLineY + 16);
        }
    }
}
