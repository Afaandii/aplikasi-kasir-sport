package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoadingScreen extends JWindow {

  private JProgressBar progressBar;
  private JLabel lblStatus;

  public LoadingScreen() {
    initComponents();
  }

  private void initComponents() {
    setSize(700, 500);
    setLocationRelativeTo(null);

    JPanel content = new JPanel(new GridBagLayout());
    content.setBackground(new Color(18, 18, 18)); // Dark background
    content.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45), 2));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;

    // 1. Logo
    JLabel lblLogo = new JLabel();
    URL logoUrl = getClass().getResource("/logo-sport.jpg");
    if (logoUrl != null) {
      ImageIcon icon = new ImageIcon(logoUrl);
      Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
      lblLogo.setIcon(new ImageIcon(img));
    } else {
      lblLogo.setText("TOKO SPORT");
      lblLogo.setFont(new Font("SansSerif", Font.BOLD, 32));
      lblLogo.setForeground(Color.WHITE);
    }
    gbc.gridy = 0;
    gbc.insets = new Insets(0, 0, 20, 0);
    content.add(lblLogo, gbc);

    // 2. App Name
    JLabel lblName = new JLabel("Aplikasi Penjualan Toko Sport");
    lblName.setFont(new Font("SansSerif", Font.BOLD, 24));
    lblName.setForeground(Color.WHITE);
    gbc.gridy = 1;
    gbc.insets = new Insets(0, 0, 40, 0);
    content.add(lblName, gbc);

    // 3. Progress Bar
    progressBar = new JProgressBar(0, 100);
    progressBar.setPreferredSize(new Dimension(400, 8));
    progressBar.putClientProperty(FlatClientProperties.STYLE, "" +
        "arc: 10;" +
        "background: #2b2b2b;" +
        "foreground: #3c78d8;");
    progressBar.setPreferredSize(new Dimension(400, 10));
    gbc.gridy = 2;
    gbc.insets = new Insets(0, 0, 10, 0);
    content.add(progressBar, gbc);

    // 4. Status Label
    lblStatus = new JLabel("Memuat aplikasi...");
    lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
    lblStatus.setForeground(new Color(150, 150, 150));
    gbc.gridy = 3;
    gbc.insets = new Insets(0, 0, 0, 0);
    content.add(lblStatus, gbc);

    add(content);
  }

  public void startLoading() {
    setVisible(true);
    Thread thread = new Thread(() -> {
      try {
        for (int i = 0; i <= 100; i++) {
          Thread.sleep(30); // Simulate loading time
          final int progress = i;
          SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progress);
            if (progress == 20)
              lblStatus.setText("Menyiapkan database...");
            if (progress == 50)
              lblStatus.setText("Memuat modul kasir...");
            if (progress == 80)
              lblStatus.setText("Hampir selesai...");
            if (progress == 100) {
              new LoginPage().setVisible(true);
              dispose();
            }
          });
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    thread.start();
  }
}
