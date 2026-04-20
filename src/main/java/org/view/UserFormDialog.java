package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.UserDAO;
import org.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserFormDialog extends JDialog {

    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnSimpan;
    private User user;
    private UserDAO userDAO;
    private boolean isSuccess = false;

    public UserFormDialog(Frame owner, User user) {
        super(owner, true);
        this.user = user;
        this.userDAO = new UserDAO();
        initComponents();
        
        if (user != null) {
            setTitle("Edit User Kasir");
            txtUsername.setText(user.getUsername());
            txtEmail.setText(user.getEmail());
            btnSimpan.setText("Update User");
            // Placeholder hint for password
            txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Biarkan kosong jika tidak ingin mengubah password");
        } else {
            setTitle("Tambah User Kasir");
            txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan password...");
        }
    }

    private void initComponents() {
        setSize(450, 450);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(30, 30, 32));
        content.setBorder(new EmptyBorder(25, 35, 25, 35));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel(user == null ? "Tambah User Kasir" : "Update Data Kasir");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        content.add(lblTitle, gbc);

        // Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setForeground(new Color(180, 180, 180));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        content.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan username...");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 12;" +
                "background: #3c3c3e;" +
                "borderWidth: 0;" +
                "margin: 8,12,8,12;");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        content.add(txtUsername, gbc);

        // Email
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setForeground(new Color(180, 180, 180));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        content.add(lblEmail, gbc);

        txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan email...");
        txtEmail.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 12;" +
                "background: #3c3c3e;" +
                "borderWidth: 0;" +
                "margin: 8,12,8,12;");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        content.add(txtEmail, gbc);

        // Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(new Color(180, 180, 180));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        content.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 12;" +
                "background: #3c3c3e;" +
                "borderWidth: 0;" +
                "margin: 8,12,8,12;" +
                "showRevealButton: true;");
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 30, 0);
        content.add(txtPassword, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton btnBatal = new JButton("Batal");
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.setFont(new Font("Inter", Font.BOLD, 14));
        btnBatal.putClientProperty(FlatClientProperties.STYLE, "background: #444; foreground: #eee; arc: 10; borderWidth: 0; margin: 8,20,8,20;");
        btnBatal.addActionListener(e -> dispose());
        
        btnSimpan = new JButton("Simpan");
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.putClientProperty(FlatClientProperties.STYLE, "background: #3c78d8; foreground: #ffffff; arc: 10; borderWidth: 0; margin: 8,20,8,20;");
        btnSimpan.addActionListener(e -> handleAction());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buttonPanel, gbc);

        add(content);
        
        // Enter key to save
        txtUsername.addActionListener(e -> handleAction());
        txtEmail.addActionListener(e -> handleAction());
        txtPassword.addActionListener(e -> handleAction());
    }

    private void handleAction() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Email tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (user == null) {
            // Addition mode
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password tidak boleh kosong untuk user baru!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRoleId(2); // Strictly Kasir
            
            if (userDAO.insert(user)) {
                isSuccess = true;
                dispose();
            }
        } else {
            // Edit mode
            user.setUsername(username);
            user.setEmail(email);
            
            boolean updatePwd = !password.isEmpty();
            if (updatePwd) {
                user.setPassword(password);
            }
            
            if (userDAO.update(user, updatePwd)) {
                isSuccess = true;
                dispose();
            }
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
