package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import raven.datetime.DatePicker;
import org.dao.TransaksiDAO;
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

public class LaporanKasirModal extends JDialog {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final User cashier;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPenjualan, lblTotalUangMasuk;
    private JFormattedTextField txtStartDate, txtEndDate;
    private DatePicker dtStart, dtEnd;
    private JComboBox<String> cbMetode;
    private final NumberFormat rbFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Brand Colors
    private final Color MAIN_BG = new Color(18, 18, 18);
    private final Color CARD_BG = new Color(30, 30, 32);
    private final Color LINE_BLUE = new Color(59, 130, 246);
    private final Color LINE_GREEN = new Color(34, 197, 94);

    public LaporanKasirModal(JFrame parent, User user) {
        super(parent, "Laporan Penjualan Saya", true);
        this.cashier = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(1000, 700);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(MAIN_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(MAIN_BG);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Cashier POS Terminal");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Kasir: " + cashier.getUsername());
        lblSub.setFont(new Font("Inter", Font.PLAIN, 14));
        lblSub.setForeground(new Color(150, 150, 150));
        
        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblSub, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // 2. Stats Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        statsPanel.setOpaque(false);

        lblTotalPenjualan = new JLabel("0 Produk");
        lblTotalUangMasuk = new JLabel("Rp 0");

        statsPanel.add(createStatCard("Total Penjualan", lblTotalPenjualan, LINE_BLUE));
        statsPanel.add(createStatCard("Total Uang Masuk", lblTotalUangMasuk, LINE_GREEN));
        
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // 3. Filter Bar & Table
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50)),
                " Riwayat Transaksi Saya ",
                0, 0,
                new Font("Inter", Font.PLAIN, 12),
                Color.GRAY));

        // Filter Controls
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setOpaque(false);

        txtStartDate = new JFormattedTextField();
        txtStartDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Dari Tanggal");
        txtStartDate.setPreferredSize(new Dimension(150, 35));
        dtStart = new DatePicker();
        dtStart.setEditor(txtStartDate);
        dtStart.setDateFormat("yyyy-MM-dd");

        txtEndDate = new JFormattedTextField();
        txtEndDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sampai Tanggal");
        txtEndDate.setPreferredSize(new Dimension(150, 35));
        dtEnd = new DatePicker();
        dtEnd.setEditor(txtEndDate);
        dtEnd.setDateFormat("yyyy-MM-dd");

        cbMetode = new JComboBox<>(new String[]{"Semua", "Tunai", "QRIS"});
        cbMetode.setPreferredSize(new Dimension(120, 35));
        cbMetode.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");

        JButton btnFilter = new JButton("Filter");
        btnFilter.setPreferredSize(new Dimension(100, 35));
        btnFilter.putClientProperty(FlatClientProperties.STYLE, "background: #3b82f6; foreground: #ffffff; arc: 10;");
        btnFilter.addActionListener(e -> loadData());

        filterBar.add(new JLabel("Dari:"));
        filterBar.add(txtStartDate);
        filterBar.add(new JLabel("Sampai:"));
        filterBar.add(txtEndDate);
        filterBar.add(btnFilter);
        filterBar.add(new JLabel("Metode:"));
        filterBar.add(cbMetode);

        contentPanel.add(filterBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"No", "Kode Transaksi", "Customer", "Total Bayar", "Metode", "Waktu"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45)));
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        contentPanel.add(scroll, BorderLayout.CENTER);

        centerPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 4. Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel lblNote = new JLabel("Laporan Performa Shift");
        lblNote.setForeground(Color.GRAY);
        lblNote.setFont(new Font("Inter", Font.ITALIC, 12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh Laporan");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "background: #3b82f6; foreground: #ffffff; arc: 10;");
        btnRefresh.addActionListener(e -> {
            txtStartDate.setText("");
            txtEndDate.setText("");
            cbMetode.setSelectedIndex(0);
            loadData();
        });

        JButton btnClose = new JButton("Tutup");
        btnClose.putClientProperty(FlatClientProperties.STYLE, "background: #333335; foreground: #ffffff; arc: 10;");
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnRefresh);
        btnPanel.add(btnClose);

        footer.add(lblNote, BorderLayout.WEST);
        footer.add(btnPanel, BorderLayout.EAST);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color lineColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15;");
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Border colored accent
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor, 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
        content.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setFont(new Font("Inter", Font.PLAIN, 16));

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(lineColor);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 32));

        content.add(lblTitle);
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private void loadData() {
        String start = dtStart.isDateSelected() ? txtStartDate.getText() : null;
        String end = dtEnd.isDateSelected() ? txtEndDate.getText() : null;
        String method = cbMetode.getSelectedItem().toString();

        // 1. Summary
        Object[] stats = transaksiDAO.getReportSummary(start, end, cashier.getId(), method);
        lblTotalPenjualan.setText(stats[0] + " Produk");
        lblTotalUangMasuk.setText(rbFormat.format(stats[1]));

        // 2. Table
        tableModel.setRowCount(0);
        List<Transaksi> list = transaksiDAO.getFilteredTransactions(start, end, cashier.getId(), method);
        int no = 1;
        for (Transaksi t : list) {
            tableModel.addRow(new Object[]{
                    no++,
                    t.getKodeTransaksi(),
                    t.getNamaCustomer(),
                    rbFormat.format(t.getTotalPembayaran()),
                    t.getMetodePembayaran(),
                    df.format(t.getCreatedAt())
            });
        }
    }
}

