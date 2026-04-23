package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import raven.datetime.DatePicker;
import org.dao.TransaksiDAO;
import org.dao.UserDAO;
import org.model.DetailTransaksi;
import org.model.Transaksi;
import org.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LaporanKeuanganPanel extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NumberFormat rbFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalTerjual, lblTotalOmzet, lblTotalLaba;
    private JFormattedTextField txtStartDate, txtEndDate;
    private DatePicker dtStart, dtEnd;
    private JComboBox<String> cbKasir, cbMetode;
    private List<User> kasirList;
    private List<Transaksi> currentTransactions;

    // Brand Colors (Matching AdminDashboard)
    private final Color MAIN_BG = new Color(18, 18, 18);
    private final Color CARD_BG = new Color(38, 38, 40);
    private final Color LINE_BLUE = new Color(59, 130, 246);
    private final Color LINE_GREEN = new Color(34, 197, 94);
    private final Color LINE_PURPLE = new Color(168, 85, 247);

    public LaporanKeuanganPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // 1. Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        lblTotalTerjual = new JLabel("0");
        lblTotalOmzet = new JLabel("Rp 0");
        lblTotalLaba = new JLabel("Rp 0");

        statsPanel.add(createStatCard("Total Produk Terjual", lblTotalTerjual, LINE_BLUE));
        statsPanel.add(createStatCard("Total Omzet", lblTotalOmzet, LINE_GREEN));
        statsPanel.add(createStatCard("Estimasi Laba", lblTotalLaba, LINE_PURPLE));

        add(statsPanel, BorderLayout.NORTH);

        // 2. Filter and Table Center
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setOpaque(false);
        filterBar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                "Seluruh Riwayat Transaksi",
                0, 0,
                new Font("Inter", Font.PLAIN, 12),
                Color.GRAY));

        // Date Inputs using Raven DatePicker
        txtStartDate = new JFormattedTextField(12);
        txtStartDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Pilih Tanggal");
        txtStartDate.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        dtStart = new DatePicker();
        dtStart.setEditor(txtStartDate);
        dtStart.setDateFormat("yyyy-MM-dd");

        txtEndDate = new JFormattedTextField(12);
        txtEndDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Pilih Tanggal");
        txtEndDate.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        dtEnd = new DatePicker();
        dtEnd.setEditor(txtEndDate);
        dtEnd.setDateFormat("yyyy-MM-dd");

        // Kasir Dropdown
        cbKasir = new JComboBox<>();
        cbKasir.addItem("Semua Kasir");
        kasirList = userDAO.findAllCashiers();
        for (User u : kasirList)
            cbKasir.addItem(u.getUsername());
        cbKasir.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        cbKasir.addActionListener(e -> loadData());

        // Metode Dropdown
        cbMetode = new JComboBox<>(new String[] { "Semua", "Tunai", "QRIS" });
        cbMetode.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        cbMetode.addActionListener(e -> loadData());

        JButton btnFilter = new JButton("Filter");
        btnFilter.putClientProperty(FlatClientProperties.STYLE, "background: #3b82f6; foreground: #ffffff; arc: 8;");
        btnFilter.addActionListener(e -> loadData());

        filterBar.add(new JLabel("Dari Tanggal:"));
        filterBar.add(txtStartDate);
        filterBar.add(new JLabel("Sampai:"));
        filterBar.add(txtEndDate);
        filterBar.add(btnFilter);
        filterBar.add(new JLabel("Kasir:"));
        filterBar.add(cbKasir);
        filterBar.add(new JLabel("Metode:"));
        filterBar.add(cbMetode);

        centerPanel.add(filterBar, BorderLayout.NORTH);

        // Table
        String[] cols = { "No", "Kode TRX", "Customer", "Kasir", "Total Produk", "Total Bayar", "Metode", "Waktu" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showDetails();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Actions
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel lblNote = new JLabel("Laporan Keuangan Global");
        lblNote.setForeground(Color.GRAY);
        lblNote.setFont(new Font("Inter", Font.ITALIC, 12));

        JButton btnReset = new JButton("Reset & Refresh");
        btnReset.putClientProperty(FlatClientProperties.STYLE, "background: #252527; foreground: #ffffff; arc: 8;");
        btnReset.addActionListener(e -> {
            txtStartDate.setText("");
            txtEndDate.setText("");
            cbKasir.setSelectedIndex(0);
            cbMetode.setSelectedIndex(0);
            loadData();
        });

        bottomBar.add(lblNote, BorderLayout.WEST);
        bottomBar.add(btnReset, BorderLayout.EAST);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color lineColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15;");
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel line = new JPanel();
        line.setBackground(lineColor);
        line.setPreferredSize(new Dimension(0, 3));
        card.add(line, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setFont(new Font("Inter", Font.PLAIN, 14));

        valueLabel.setForeground(lineColor);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 28));

        content.add(lblTitle);
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private void loadData() {
        String start = dtStart.isDateSelected() ? txtStartDate.getText() : null;
        String end = dtEnd.isDateSelected() ? txtEndDate.getText() : null;
        int kasirId = 0;
        if (cbKasir.getSelectedIndex() > 0) {
            kasirId = kasirList.get(cbKasir.getSelectedIndex() - 1).getId();
        }
        String method = cbMetode.getSelectedItem().toString();

        // 1. Load Stats
        Object[] stats = transaksiDAO.getReportSummary(start, end, kasirId, method);
        lblTotalTerjual.setText(stats[0] + " Produk");
        lblTotalOmzet.setText(rbFormat.format(stats[1]));
        lblTotalLaba.setText(rbFormat.format(stats[2]));

        // 2. Load Table
        tableModel.setRowCount(0);
        currentTransactions = transaksiDAO.getFilteredTransactions(start, end, kasirId, method);
        int no = 1;
        for (Transaksi t : currentTransactions) {
            tableModel.addRow(new Object[] {
                    no++,
                    t.getKodeTransaksi(),
                    t.getNamaCustomer(),
                    t.getKasirNama(),
                    t.getTotalItem() + " Produk",
                    rbFormat.format(t.getTotalPembayaran()),
                    t.getMetodePembayaran(),
                    df.format(t.getCreatedAt())
            });
        }
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row != -1) {
            Transaksi t = currentTransactions.get(row);
            List<DetailTransaksi> details = transaksiDAO.findDetails(t.getId());
            new StrukModal(SwingUtilities.getWindowAncestor(this), t, details, t.getKasirNama()).setVisible(true);
        }
    }
}
