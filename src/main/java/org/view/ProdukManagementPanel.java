package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.ProdukDAO;
import org.dao.VarianDAO;
import org.model.Produk;
import org.model.Varian;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ProdukManagementPanel extends JPanel {

    private JTable tableProduk, tableVarian;
    private DefaultTableModel modelProduk, modelVarian;
    private JTextField txtSearch;
    private JButton btnTambahProduk, btnEditProduk, btnHapusProduk;
    private JButton btnTambahVarian, btnEditVarian, btnHapusVarian;

    private ProdukDAO produkDAO;
    private VarianDAO varianDAO;
    private List<Produk> initialProdukList;
    private Produk selectedProduk;

    public ProdukManagementPanel() {
        produkDAO = new ProdukDAO();
        varianDAO = new VarianDAO();
        initComponents();
        loadProdukData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- MASTER SECTION (Produk) ---
        JPanel masterPanel = new JPanel(new BorderLayout(0, 15));
        masterPanel.setOpaque(false);

        // Header
        JPanel headerProduk = new JPanel(new BorderLayout());
        headerProduk.setOpaque(false);
        JLabel lblTitle = new JLabel("Kelola Data Produk (Master)");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerProduk.add(lblTitle, BorderLayout.WEST);

        JPanel actionsProduk = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsProduk.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Produk...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #2b2b2b; borderWidth: 0;");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterProduk();
            }

            public void removeUpdate(DocumentEvent e) {
                filterProduk();
            }

            public void changedUpdate(DocumentEvent e) {
                filterProduk();
            }
        });

        btnTambahProduk = createButton("Tambah Produk", "#3c78d8");
        btnTambahProduk.addActionListener(e -> handleAddProduk());

        actionsProduk.add(txtSearch);
        actionsProduk.add(btnTambahProduk);
        headerProduk.add(actionsProduk, BorderLayout.EAST);
        masterPanel.add(headerProduk, BorderLayout.NORTH);

        // Table Produk
        String[] colProduk = { "No", "Kode", "Nama Produk", "Kategori", "Merek", "H. Pokok", "H. Jual", "ID" };
        modelProduk = new DefaultTableModel(colProduk, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableProduk = createStyledTable(modelProduk);
        tableProduk.getColumnModel().removeColumn(tableProduk.getColumnModel().getColumn(7));

        tableProduk.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                handleProdukSelection();
        });

        JScrollPane scrollProduk = new JScrollPane(tableProduk);
        scrollProduk.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45)));
        masterPanel.add(scrollProduk, BorderLayout.CENTER);

        // Footer Actions Produk
        JPanel footerProduk = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerProduk.setOpaque(false);
        btnEditProduk = createButton("Edit Produk", "#2e7d32");
        btnEditProduk.addActionListener(e -> handleEditProduk());
        btnHapusProduk = createButton("Hapus", "#d32f2f");
        btnHapusProduk.addActionListener(e -> handleHapusProduk());
        footerProduk.add(btnEditProduk);
        footerProduk.add(btnHapusProduk);
        masterPanel.add(footerProduk, BorderLayout.SOUTH);

        // --- DETAIL SECTION (Varian) ---
        JPanel detailPanel = new JPanel(new BorderLayout(0, 15));
        detailPanel.setOpaque(false);
        detailPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel headerVarian = new JPanel(new BorderLayout());
        headerVarian.setOpaque(false);
        JLabel lblVarianTitle = new JLabel("Varian & Stok (Detail)");
        lblVarianTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblVarianTitle.setForeground(new Color(200, 200, 200));
        headerVarian.add(lblVarianTitle, BorderLayout.WEST);

        btnTambahVarian = createButton("Tambah Varian", "#34a853");
        btnTambahVarian.setEnabled(false);
        btnTambahVarian.addActionListener(e -> handleAddVarian());
        headerVarian.add(btnTambahVarian, BorderLayout.EAST);
        detailPanel.add(headerVarian, BorderLayout.NORTH);

        // Table Varian
        String[] colVarian = { "No", "Ukuran", "Warna", "Stok", "ID" };
        modelVarian = new DefaultTableModel(colVarian, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableVarian = createStyledTable(modelVarian);
        tableVarian.getColumnModel().removeColumn(tableVarian.getColumnModel().getColumn(4));

        JScrollPane scrollVarian = new JScrollPane(tableVarian);
        scrollVarian.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45)));
        detailPanel.add(scrollVarian, BorderLayout.CENTER);

        // Footer Actions Varian
        JPanel footerVarian = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerVarian.setOpaque(false);
        btnEditVarian = createButton("Edit Varian", "#f9ab00");
        btnEditVarian.setEnabled(false);
        btnEditVarian.addActionListener(e -> handleEditVarian());
        btnHapusVarian = createButton("Hapus Varian", "#d32f2f");
        btnHapusVarian.setEnabled(false);
        btnHapusVarian.addActionListener(e -> handleHapusVarian());
        footerVarian.add(btnEditVarian);
        footerVarian.add(btnHapusVarian);
        detailPanel.add(footerVarian, BorderLayout.SOUTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(35);
        t.setBackground(new Color(30, 30, 30));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(45, 45, 45));
        t.getTableHeader().setBackground(new Color(35, 35, 37));
        t.getTableHeader().setForeground(new Color(180, 180, 180));
        t.setSelectionBackground(new Color(50, 50, 55));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        t.getColumnModel().getColumn(0).setCellRenderer(center);
        t.getColumnModel().getColumn(0).setMaxWidth(50);

        return t;
    }

    private JButton createButton(String text, String bgColor) {
        JButton b = new JButton(text);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.putClientProperty(FlatClientProperties.STYLE,
                "background: " + bgColor + "; foreground: #fff; arc: 10; borderWidth: 0; margin: 5,15,5,15;");
        return b;
    }

    private void loadProdukData() {
        initialProdukList = produkDAO.findAll();
        displayProduk(initialProdukList);
    }

    private void displayProduk(List<Produk> list) {
        modelProduk.setRowCount(0);
        int no = 1;
        for (Produk p : list) {
            modelProduk.addRow(new Object[] {
                    no++, p.getKodeProduk(), p.getNamaProduk(), p.getKategoriNama(),
                    p.getMerekNama(), p.getHargaPokok(), p.getHargaJual(), p.getId()
            });
        }
        clearVarianSelection();
    }

    private void filterProduk() {
        String key = txtSearch.getText().toLowerCase();
        List<Produk> filtered = initialProdukList.stream()
                .filter(p -> p.getNamaProduk().toLowerCase().contains(key)
                        || p.getKodeProduk().toLowerCase().contains(key))
                .collect(Collectors.toList());
        displayProduk(filtered);
    }

    private void handleProdukSelection() {
        int row = tableProduk.getSelectedRow();
        if (row != -1) {
            int id = (int) tableProduk.getModel().getValueAt(tableProduk.convertRowIndexToModel(row), 7);
            selectedProduk = initialProdukList.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            if (selectedProduk != null) {
                loadVarianData(selectedProduk.getId());
                btnTambahVarian.setEnabled(true);
                btnEditVarian.setEnabled(true);
                btnHapusVarian.setEnabled(true);
            }
        } else {
            clearVarianSelection();
        }
    }

    private void clearVarianSelection() {
        selectedProduk = null;
        modelVarian.setRowCount(0);
        btnTambahVarian.setEnabled(false);
        btnEditVarian.setEnabled(false);
        btnHapusVarian.setEnabled(false);
    }

    private void loadVarianData(int produkId) {
        List<Varian> variants = varianDAO.findByProdukId(produkId);
        modelVarian.setRowCount(0);
        int no = 1;
        for (Varian v : variants) {
            modelVarian
                    .addRow(new Object[] { no++, v.getUkuranNama(), v.getWarnaNama(), v.getStokProduk(), v.getId() });
        }
    }

    // --- Action Handlers --- (Simplified for brevity)
    private void handleAddProduk() {
        ProdukFormDialog diag = new ProdukFormDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        diag.setVisible(true);
        if (diag.isSuccess())
            loadProdukData();
    }

    private void handleEditProduk() {
        if (selectedProduk == null)
            return;
        ProdukFormDialog diag = new ProdukFormDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedProduk);
        diag.setVisible(true);
        if (diag.isSuccess())
            loadProdukData();
    }

    private void handleHapusProduk() {
        if (selectedProduk == null)
            return;
        if (JOptionPane.showConfirmDialog(this, "Hapus produk & semua variannya?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == 0) {
            if (produkDAO.delete(selectedProduk.getId()))
                loadProdukData();
        }
    }

    private void handleAddVarian() {
        VarianFormDialog diag = new VarianFormDialog((Frame) SwingUtilities.getWindowAncestor(this), null,
                selectedProduk.getId());
        diag.setVisible(true);
        if (diag.isSuccess())
            loadVarianData(selectedProduk.getId());
    }

    private void handleEditVarian() {
        int row = tableVarian.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) tableVarian.getModel().getValueAt(tableVarian.convertRowIndexToModel(row), 4);
        Varian v = varianDAO.findByProdukId(selectedProduk.getId()).stream().filter(var -> var.getId() == id)
                .findFirst().orElse(null);
        if (v != null) {
            VarianFormDialog diag = new VarianFormDialog((Frame) SwingUtilities.getWindowAncestor(this), v,
                    selectedProduk.getId());
            diag.setVisible(true);
            if (diag.isSuccess())
                loadVarianData(selectedProduk.getId());
        }
    }

    private void handleHapusVarian() {
        int row = tableVarian.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) tableVarian.getModel().getValueAt(tableVarian.convertRowIndexToModel(row), 4);
        if (JOptionPane.showConfirmDialog(this, "Hapus varian ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
            if (varianDAO.delete(id))
                loadVarianData(selectedProduk.getId());
        }
    }
}
