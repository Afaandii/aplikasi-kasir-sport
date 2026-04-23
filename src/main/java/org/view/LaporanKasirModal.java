package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.TransaksiDAO;
import org.model.Transaksi;
import org.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LaporanKasirModal extends JDialog {

    private TransaksiDAO transaksiDAO = new TransaksiDAO();
    private User cashier;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotalPenjualan;
    private final NumberFormat rbFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public LaporanKasirModal(JFrame parent, User user) {
        super(parent, "Laporan Penjualan Saya Hari Ini", true);
        this.cashier = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(800, 500);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(18, 18, 18));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Rekap Transaksi Hari Ini");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Kasir: " + cashier.getUsername());
        lblSub.setForeground(new Color(150, 150, 150));
        
        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        mainPanel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Waktu", "Kode Transaksi", "Customer", "Metode", "Total"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setShowGrid(false);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(28, 28, 30));
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Footer Summary
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblTotalPenjualan = new JLabel("Total Pendapatan: Rp 0");
        lblTotalPenjualan.setFont(new Font("Inter", Font.BOLD, 18));
        lblTotalPenjualan.setForeground(new Color(59, 130, 246));
        
        JButton btnClose = new JButton("Tutup");
        btnClose.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #333335; foreground: #ffffff;");
        btnClose.addActionListener(e -> dispose());
        
        footer.add(lblTotalPenjualan, BorderLayout.WEST);
        footer.add(btnClose, BorderLayout.EAST);
        
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadData() {
        List<Transaksi> list = transaksiDAO.findByKasirToday(cashier.getId());
        model.setRowCount(0);
        int total = 0;
        
        for (Transaksi t : list) {
            model.addRow(new Object[]{
                    new java.text.SimpleDateFormat("HH:mm:ss").format(t.getCreatedAt()),
                    t.getKodeTransaksi(),
                    t.getNamaCustomer(),
                    t.getMetodePembayaran(),
                    rbFormat.format(t.getTotalPembayaran())
            });
            total += t.getTotalPembayaran();
        }
        
        lblTotalPenjualan.setText("Total Pendapatan: " + rbFormat.format(total));
        
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada transaksi untuk hari ini.");
        }
    }
}
