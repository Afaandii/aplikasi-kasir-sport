package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.KategoriDAO;
import org.model.Kategori;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class KategoriManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnTambah, btnEdit, btnHapus;
    private KategoriDAO kategoriDAO;
    private List<Kategori> initialList;

    public KategoriManagementPanel() {
        kategoriDAO = new KategoriDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(18, 18, 18));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- TOP SECTION (Header) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Kelola Data Kategori");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Header Actions (Search + Add)
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerActions.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Nama Kategori...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 15;" +
                "background: #2b2b2b;" +
                "margin: 0,10,0,10;" +
                "borderWidth: 0;");

        // Search Logic
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterData();
            }
        });

        btnTambah = new JButton("Tambah Kategori");
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.setFont(new Font("Inter", Font.BOLD, 14));
        btnTambah.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: #3c78d8;" +
                "foreground: #ffffff;" +
                "arc: 15;" +
                "borderWidth: 0;" +
                "margin: 10,20,10,20;");
        btnTambah.addActionListener(e -> handleAdd());

        headerActions.add(txtSearch);
        headerActions.add(btnTambah);
        topPanel.add(headerActions, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER SECTION (Table) ---
        String[] columns = { "No", "Nama Kategori", "Tanggal Dibuat", "ID" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        // Hide the ID column (index 3)
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(3));

        table.setRowHeight(40);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(new Color(45, 45, 45));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(35, 35, 37));
        table.getTableHeader().setForeground(new Color(180, 180, 180));
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(50, 50, 55));
        table.setSelectionForeground(Color.WHITE);

        // Center align the "No" column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM SECTION (Footer Actions) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnEdit = new JButton("Edit Terpilih");
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.setFont(new Font("Inter", Font.BOLD, 14));
        btnEdit.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: #2e7d32;" +
                "foreground: #ffffff;" +
                "arc: 12;" +
                "borderWidth: 0;" +
                "margin: 8,25,8,25;");
        btnEdit.addActionListener(e -> handleEdit());

        btnHapus = new JButton("Hapus");
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.setFont(new Font("Inter", Font.BOLD, 14));
        btnHapus.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: #d32f2f;" +
                "foreground: #ffffff;" +
                "arc: 12;" +
                "borderWidth: 0;" +
                "margin: 8,25,8,25;");
        btnHapus.addActionListener(e -> handleDelete());

        bottomPanel.add(btnEdit);
        bottomPanel.add(btnHapus);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        initialList = kategoriDAO.findAll();
        displayData(initialList);
    }

    private void displayData(List<Kategori> list) {
        tableModel.setRowCount(0);
        int no = 1;
        for (Kategori k : list) {
            tableModel.addRow(new Object[] {
                    no++,
                    k.getNamaKategori(),
                    k.getCreatedAt(),
                    k.getIdKategori() // Hidden index 3
            });
        }
    }

    private void filterData() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            displayData(initialList);
        } else {
            List<Kategori> filtered = initialList.stream()
                    .filter(k -> k.getNamaKategori().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
            displayData(filtered);
        }
    }

    private void handleAdd() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        KategoriFormDialog dialog = new KategoriFormDialog((Frame) parentWindow, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadData();
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang ingin diedit!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getModel().getValueAt(table.convertRowIndexToModel(row), 3);
        Kategori selected = initialList.stream().filter(k -> k.getIdKategori() == id).findFirst().orElse(null);

        if (selected != null) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            KategoriFormDialog dialog = new KategoriFormDialog((Frame) parentWindow, selected);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadData();
            }
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang ingin dihapus!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getModel().getValueAt(table.convertRowIndexToModel(row), 3);
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus kategori ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (kategoriDAO.delete(id)) {
                loadData();
            }
        }
    }
}
