package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.UkuranDAO;
import org.model.Ukuran;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UkuranFormDialog extends JDialog {

    private JTextField txtNama;
    private JButton btnSimpan;
    private Ukuran ukuran;
    private UkuranDAO ukuranDAO;
    private boolean isSuccess = false;

    public UkuranFormDialog(Frame owner, Ukuran ukuran) {
        super(owner, true);
        this.ukuran = ukuran;
        this.ukuranDAO = new UkuranDAO();
        initComponents();
        
        if (ukuran != null) {
            setTitle("Edit Ukuran");
            txtNama.setText(ukuran.getNamaUkuran());
            btnSimpan.setText("Update Ukuran");
        } else {
            setTitle("Tambah Ukuran");
        }
    }

    private void initComponents() {
        setSize(400, 250);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(30, 30, 32));
        content.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel(ukuran == null ? "Tambah Ukuran Baru" : "Update Data Ukuran");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        content.add(lblTitle, gbc);

        JLabel lblNama = new JLabel("Nama Ukuran (misal: 40, XL, dll)");
        lblNama.setForeground(new Color(180, 180, 180));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        content.add(lblNama, gbc);

        txtNama = new JTextField();
        txtNama.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan nama ukuran...");
        txtNama.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 12;" +
                "background: #3c3c3e;" +
                "borderWidth: 0;" +
                "margin: 8,12,8,12;");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        content.add(txtNama, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnBatal = new JButton("Batal");
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.putClientProperty(FlatClientProperties.STYLE, "background: #444; foreground: #eee; arc: 10; borderWidth: 0;");
        btnBatal.addActionListener(e -> dispose());
        
        btnSimpan = new JButton("Simpan");
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.putClientProperty(FlatClientProperties.STYLE, "background: #3c78d8; foreground: #ffffff; arc: 10; borderWidth: 0;");
        btnSimpan.addActionListener(e -> handleAction());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buttonPanel, gbc);

        add(content);
        
        // Enter key to save
        txtNama.addActionListener(e -> handleAction());
    }

    private void handleAction() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama ukuran tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ukuran == null) {
            ukuran = new Ukuran();
            ukuran.setNamaUkuran(nama);
            if (ukuranDAO.insert(ukuran)) {
                isSuccess = true;
                dispose();
            }
        } else {
            ukuran.setNamaUkuran(nama);
            if (ukuranDAO.update(ukuran)) {
                isSuccess = true;
                dispose();
            }
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
