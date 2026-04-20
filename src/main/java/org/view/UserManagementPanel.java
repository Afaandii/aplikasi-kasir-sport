package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.UserDAO;
import org.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnTambah, btnEdit, btnHapus;
    private UserDAO userDAO;
    private List<User> initialList;

    public UserManagementPanel() {
        userDAO = new UserDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 30));
        setBackground(new Color(18, 18, 18));
        setBorder(new EmptyBorder(10, 40, 30, 40));

        // --- TOP SECTION (Header) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Kelola Data User Kasir");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Header Actions (Search + Add)
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerActions.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Username / Email...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 15;" +
                "background: #2b2b2b;" +
                "margin: 0,10,0,10;" +
                "borderWidth: 0;");
        
        // Search Logic
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterData(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterData(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });

        btnTambah = new JButton("Tambah Kasir");
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
        String[] columns = {"No", "Username", "Email", "Tanggal Dibuat", "ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        // Hide the ID column (index 4)
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(4));
        
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
        initialList = userDAO.findAllCashiers();
        displayData(initialList);
    }

    private void displayData(List<User> list) {
        tableModel.setRowCount(0);
        int no = 1;
        for (User u : list) {
            tableModel.addRow(new Object[]{
                    no++,
                    u.getUsername(),
                    u.getEmail(),
                    u.getCreatedAt() != null ? u.getCreatedAt().toString() : "-",
                    u.getId() // Hidden index 4
            });
        }
    }

    private void filterData() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            displayData(initialList);
        } else {
            List<User> filtered = initialList.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(keyword) || 
                                u.getEmail().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
            displayData(filtered);
        }
    }

    private void handleAdd() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        UserFormDialog dialog = new UserFormDialog((Frame) parentWindow, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadData();
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
        User selected = initialList.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        
        if (selected != null) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            UserFormDialog dialog = new UserFormDialog((Frame) parentWindow, selected);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadData();
            }
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus user ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.delete(id)) {
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
