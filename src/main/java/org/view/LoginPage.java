package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.UserDAO;
import org.model.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginPage extends JFrame {

  private JTextField txtUsername;
  private JPasswordField txtPassword;
  private JButton btnLogin;
  private UserDAO userDAO;

  public LoginPage() {
    userDAO = new UserDAO();
    initComponents();
  }

  private void initComponents() {
    setTitle("Login - Toko Sport");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);
    setResizable(true); // Allow user to maximize if they want to

    // Main Panel (Dark background)
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBackground(new Color(18, 18, 18));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Card Panel (Dark card with premium styling)
    JPanel cardPanel = new JPanel(new GridBagLayout());
    cardPanel.putClientProperty(FlatClientProperties.STYLE, "" +
        "arc: 40;" +
        "background: #252525;");
    cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
    GridBagConstraints cgbc = new GridBagConstraints();
    cgbc.insets = new Insets(10, 10, 10, 10);
    cgbc.gridx = 0;

    // 1. Logo (Top Center)
    JLabel lblLogo = new JLabel();
    URL logoUrl = getClass().getResource("/logo-sport.jpg");
    if (logoUrl != null) {
      ImageIcon icon = new ImageIcon(logoUrl);
      Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
      lblLogo.setIcon(new ImageIcon(img));
    } else {
      lblLogo.setText("SHOES CENTER");
      lblLogo.setFont(new Font("SansSerif", Font.BOLD, 28));
      lblLogo.setForeground(Color.WHITE);
    }
    lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
    cgbc.gridy = 0;
    cgbc.insets = new Insets(0, 0, 10, 0);
    cardPanel.add(lblLogo, cgbc);

    // 2. Title
    JLabel lblTitle = new JLabel("Sport Center Login");
    lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
    lblTitle.putClientProperty(FlatClientProperties.STYLE, "foreground: #ffffff;");
    cgbc.gridy = 1;
    cgbc.insets = new Insets(10, 0, 30, 0);
    cardPanel.add(lblTitle, cgbc);

    // 3. Username Section
    JLabel lblUsername = new JLabel("Username");
    lblUsername.setFont(new Font("Inter", Font.PLAIN, 14));
    lblUsername.putClientProperty(FlatClientProperties.STYLE, "foreground: #aaaaaa;");
    cgbc.gridy = 2;
    cgbc.insets = new Insets(0, 0, 5, 0);
    cgbc.anchor = GridBagConstraints.WEST;
    cardPanel.add(lblUsername, cgbc);

    txtUsername = new JTextField();
    txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
    txtUsername.putClientProperty(FlatClientProperties.STYLE, "" +
        "arc: 15;" +
        "margin: 8,15,8,15;" +
        "background: #333333;" +
        "borderWidth: 0;" +
        "focusColor: #3c78d8;");
    cgbc.gridy = 3;
    cgbc.fill = GridBagConstraints.HORIZONTAL;
    cgbc.ipadx = 250;
    cgbc.insets = new Insets(0, 0, 20, 0);
    cardPanel.add(txtUsername, cgbc);

    // 4. Password Section
    JLabel lblPassword = new JLabel("Password");
    lblPassword.setFont(new Font("Inter", Font.PLAIN, 14));
    lblPassword.putClientProperty(FlatClientProperties.STYLE, "foreground: #aaaaaa;");
    cgbc.gridy = 4;
    cgbc.insets = new Insets(0, 0, 5, 0);
    cardPanel.add(lblPassword, cgbc);

    txtPassword = new JPasswordField();
    txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
    txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
        "arc: 15;" +
        "margin: 8,15,8,15;" +
        "background: #333333;" +
        "borderWidth: 0;" +
        "showRevealButton: true;" +
        "focusColor: #3c78d8;");
    cgbc.gridy = 5;
    cgbc.insets = new Insets(0, 0, 30, 0);
    cardPanel.add(txtPassword, cgbc);

    // 5. Login Button
    btnLogin = new JButton("Sign In");
    btnLogin.setFont(new Font("Inter", Font.BOLD, 16));
    btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnLogin.putClientProperty(FlatClientProperties.STYLE, "" +
        "background: #3c78d8;" +
        "foreground: #ffffff;" +
        "arc: 15;" +
        "borderWidth: 0;" +
        "hoverBackground: #4c8cf5;" +
        "pressedBackground: #2a56a2;");
    cgbc.gridy = 6;
    cgbc.ipady = 10;
    cgbc.insets = new Insets(10, 0, 0, 0);
    cardPanel.add(btnLogin, cgbc);

    // Filter focus out for nicer look
    cardPanel.setFocusable(true);

    // Action Listener
    btnLogin.addActionListener(e -> handleLogin());

    mainPanel.add(cardPanel, gbc);
    add(mainPanel);
  }

  private void handleLogin() {
    String username = txtUsername.getText();
    String password = new String(txtPassword.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!", "Peringatan",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    User user = userDAO.authenticate(username, password);
    if (user != null) {
      JOptionPane.showMessageDialog(this, "Selamat datang, " + user.getUsername() + "!", "Login Berhasil",
          JOptionPane.INFORMATION_MESSAGE);
      // Navigate to main dashboard (to be implemented)
      // new Dashboard(user).setVisible(true);
      // this.dispose();
    } else {
      JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
    }
  }
}
