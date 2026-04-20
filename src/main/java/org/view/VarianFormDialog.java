package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.UkuranDAO;
import org.dao.VarianDAO;
import org.dao.WarnaDAO;
import org.model.Ukuran;
import org.model.Varian;
import org.model.Warna;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class VarianFormDialog extends JDialog {

    private JComboBox<Ukuran> cbUkuran;
    private JComboBox<Warna> cbWarna;
    private JTextField txtStok;
    private JButton btnSimpan;
    private Varian varian;
    private int produkId;
    private VarianDAO varianDAO;
    private boolean isSuccess = false;

    public VarianFormDialog(Frame owner, Varian varian, int produkId) {
        super(owner, true);
        this.varian = varian;
        this.produkId = produkId;
        this.varianDAO = new VarianDAO();
        initComponents();
        loadDropdowns();
        
        if (varian != null) {
            setTitle("Edit Varian");
            txtStok.setText(String.valueOf(varian.getStokProduk()));
            btnSimpan.setText("Update Varian");
        } else {
            setTitle("Tambah Varian Baru");
        }
    }

    private void initComponents() {
        setSize(400, 450);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(30, 30, 32));
        content.setBorder(new EmptyBorder(25, 35, 25, 35));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel(varian == null ? "Tambah Varian" : "Update Varian");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        content.add(lblTitle, gbc);

        // Combo Ukuran
        addLabel(content, "Ukuran", 1, gbc);
        cbUkuran = new JComboBox<>();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        content.add(cbUkuran, gbc);

        // Combo Warna
        addLabel(content, "Warna", 3, gbc);
        cbWarna = new JComboBox<>();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        content.add(cbWarna, gbc);

        // Stok
        addLabel(content, "Stok Produk", 5, gbc);
        txtStok = new JTextField();
        txtStok.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        txtStok.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #3c3c3e; borderWidth: 0; margin: 8,12,8,12;");
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 30, 0);
        content.add(txtStok, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton btnBatal = new JButton("Batal");
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.putClientProperty(FlatClientProperties.STYLE, "background: #444; foreground: #eee; arc: 10; borderWidth: 0; margin: 8,20,8,20;");
        btnBatal.addActionListener(e -> dispose());
        
        btnSimpan = new JButton("Simpan");
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.putClientProperty(FlatClientProperties.STYLE, "background: #34a853; foreground: #ffffff; arc: 10; borderWidth: 0; margin: 8,20,8,20;");
        btnSimpan.addActionListener(e -> handleAction());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buttonPanel, gbc);

        add(content);
        
        // Renderers
        cbUkuran.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ukuran) setText(((Ukuran) value).getNamaUkuran());
                return this;
            }
        });
        
        cbWarna.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof org.model.Warna) setText(((org.model.Warna) value).getNamaWarna());
                return this;
            }
        });
    }

    private void addLabel(JPanel p, String text, int y, GridBagConstraints gbc) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 180, 180));
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(l, gbc);
    }

    private void loadDropdowns() {
        List<Ukuran> uks = new UkuranDAO().findAll();
        for (Ukuran u : uks) {
            cbUkuran.addItem(u);
            if (varian != null && u.getId() == varian.getUkuranId()) cbUkuran.setSelectedItem(u);
        }
        
        List<org.model.Warna> warns = new WarnaDAO().findAll();
        for (org.model.Warna w : warns) {
            cbWarna.addItem(w);
            if (varian != null && w.getId() == varian.getWarnaId()) cbWarna.setSelectedItem(w);
        }
    }

    private void handleAction() {
        try {
            Ukuran uk = (Ukuran) cbUkuran.getSelectedItem();
            org.model.Warna warn = (org.model.Warna) cbWarna.getSelectedItem();
            String stokStr = txtStok.getText().trim();

            if (uk == null || warn == null || stokStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (varian == null) varian = new Varian();
            varian.setProdukId(produkId);
            varian.setUkuranId(uk.getId());
            varian.setWarnaId(warn.getId());
            varian.setStokProduk(Integer.parseInt(stokStr));

            if (varian.getId() == 0) {
                if (varianDAO.insert(varian)) {
                    isSuccess = true;
                    dispose();
                }
            } else {
                if (varianDAO.update(varian)) {
                    isSuccess = true;
                    dispose();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() { return isSuccess; }
}
