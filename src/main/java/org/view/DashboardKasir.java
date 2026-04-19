package org.view;

import javax.swing.*;

public class DashboardKasir extends JFrame {
    public DashboardKasir(org.model.User user) {
        setTitle("Dashboard Kasir - Segera Hadir");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Halaman Kasir untuk " + user.getUsername() + " masih dalam tahap pengembangan.", SwingConstants.CENTER);
        add(label);
    }
}
