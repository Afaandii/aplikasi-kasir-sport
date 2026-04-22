package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.TransaksiDAO;
import org.model.DetailTransaksi;
import org.model.Transaksi;
import org.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutModal extends JDialog {

    private final int totalAmount;
    private final User user;
    private final List<DetailTransaksi> cartItems;
    private final String customerName;
    private final Runnable onSuccess;

    private String selectedMethod = "Tunai";
    private int amountPaid = 0;
    private final NumberFormat rbFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    // UI Components
    private JTextField txtAmount;
    private JLabel lblChange;
    private JButton btnTunai, btnQRIS;
    private JPanel cashPanel;

    private final Color MAIN_BG = new Color(18, 18, 18);
    private final Color ACCENT_BLUE = new Color(59, 130, 246);
    // private final Color SIDEBAR_BG = new Color(28, 28, 30);

    public CheckoutModal(JFrame parent, User user, int totalAmount, List<DetailTransaksi> cartItems,
            String customerName, Runnable onSuccess) {
        super(parent, "Proses Pembayaran", true);
        this.user = user;
        this.totalAmount = totalAmount;
        this.cartItems = cartItems;
        this.customerName = customerName;
        this.onSuccess = onSuccess;

        initComponents();
    }

    private void initComponents() {
        setSize(550, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(MAIN_BG);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Header (Total Display)
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel lblTotalTitle = new JLabel("TOTAL TAGIHAN");
        lblTotalTitle.setForeground(Color.GRAY);
        lblTotalTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTotalTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalAmount = new JLabel(rbFormat.format(totalAmount));
        lblTotalAmount.setForeground(ACCENT_BLUE);
        lblTotalAmount.setFont(new Font("Inter", Font.BOLD, 42));
        lblTotalAmount.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(lblTotalTitle);
        header.add(lblTotalAmount);
        content.add(header, BorderLayout.NORTH);

        // 2. Center Section (Payment Method & Inputs)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // Payment Method Tabs
        JPanel methodPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        methodPanel.setOpaque(false);
        methodPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        btnTunai = createMethodButton("Tunai", true);
        btnQRIS = createMethodButton("QRIS", false);

        btnTunai.addActionListener(e -> switchMethod("Tunai"));
        btnQRIS.addActionListener(e -> switchMethod("QRIS"));

        methodPanel.add(btnTunai);
        methodPanel.add(btnQRIS);
        centerPanel.add(methodPanel, BorderLayout.NORTH);

        // Input Area
        cashPanel = new JPanel(new BorderLayout(0, 20));
        cashPanel.setOpaque(false);

        // Cash Input Field
        txtAmount = new JTextField();
        txtAmount.setFont(new Font("Inter", Font.BOLD, 28));
        txtAmount.setHorizontalAlignment(JTextField.CENTER);
        txtAmount.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        txtAmount.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #252527; foreground: #ffffff;");
        txtAmount.setPreferredSize(new Dimension(0, 70));

        txtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculateChange();
            }
        });

        // Quick Buttons (Pecahan)
        JPanel gridQuick = new JPanel(new GridLayout(1, 4, 10, 0));
        gridQuick.setOpaque(false);
        gridQuick.add(createQuickButton("20k", 20000));
        gridQuick.add(createQuickButton("50k", 50000));
        gridQuick.add(createQuickButton("100k", 100000));
        gridQuick.add(createQuickButton("Pas", totalAmount));

        // Change Status
        JPanel changePanel = new JPanel(new BorderLayout());
        changePanel.setOpaque(false);
        JLabel lblChangeTitle = new JLabel("KEMBALIAN");
        lblChangeTitle.setForeground(Color.GRAY);
        lblChangeTitle.setFont(new Font("Inter", Font.BOLD, 12));

        lblChange = new JLabel("Rp 0");
        lblChange.setForeground(Color.WHITE);
        lblChange.setFont(new Font("Inter", Font.BOLD, 24));

        changePanel.add(lblChangeTitle, BorderLayout.NORTH);
        changePanel.add(lblChange, BorderLayout.SOUTH);

        JPanel inputGroup = new JPanel(new BorderLayout(0, 15));
        inputGroup.setOpaque(false);
        inputGroup.add(txtAmount, BorderLayout.NORTH);
        inputGroup.add(gridQuick, BorderLayout.CENTER);
        inputGroup.add(changePanel, BorderLayout.SOUTH);

        cashPanel.add(inputGroup, BorderLayout.CENTER);
        centerPanel.add(cashPanel, BorderLayout.CENTER);
        content.add(centerPanel, BorderLayout.CENTER);

        // 3. Footer (Action Buttons)
        JPanel footer = new JPanel(new GridLayout(1, 2, 15, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(30, 0, 0, 0));

        JButton btnCancel = new JButton("BATAL");
        btnCancel.setFont(new Font("Inter", Font.BOLD, 14));
        btnCancel.setPreferredSize(new Dimension(0, 55));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "background: #333335; arc: 12; borderWidth: 0;");
        btnCancel.addActionListener(e -> dispose());

        JButton btnProcess = new JButton("PROSES TRANSAKSI");
        btnProcess.setFont(new Font("Inter", Font.BOLD, 14));
        btnProcess.setPreferredSize(new Dimension(0, 55));
        btnProcess.setForeground(Color.WHITE);
        btnProcess.putClientProperty(FlatClientProperties.STYLE, "background: #3b82f6; arc: 12; borderWidth: 0;");
        btnProcess.addActionListener(e -> processTransaction());

        footer.add(btnCancel);
        footer.add(btnProcess);
        content.add(footer, BorderLayout.SOUTH);

        add(content);
    }

    private JButton createMethodButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(0, 60));
        updateMethodButtonStyle(btn, active);
        return btn;
    }

    private void updateMethodButtonStyle(JButton btn, boolean active) {
        String bg = active ? "#3b82f6" : "#252527";
        String fg = active ? "#ffffff" : "#888888";
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: " + bg + "; foreground: " + fg + "; arc: 12; borderWidth: 0;");
    }

    private JButton createQuickButton(String name, int val) {
        JButton btn = new JButton(name);
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(0, 45));
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: #333335; foreground: #ffffff; arc: 10; borderWidth: 0;");
        btn.addActionListener(e -> {
            txtAmount.setText(String.valueOf(val));
            calculateChange();
        });
        return btn;
    }

    private void switchMethod(String method) {
        this.selectedMethod = method;
        updateMethodButtonStyle(btnTunai, method.equals("Tunai"));
        updateMethodButtonStyle(btnQRIS, method.equals("QRIS"));

        cashPanel.setVisible(method.equals("Tunai"));
        if (method.equals("QRIS")) {
            txtAmount.setText(String.valueOf(totalAmount));
            lblChange.setText("Rp 0");
        }
        revalidate();
        repaint();
    }

    private void calculateChange() {
        try {
            String val = txtAmount.getText().replaceAll("[^0-9]", "");
            if (val.isEmpty()) {
                amountPaid = 0;
            } else {
                amountPaid = Integer.parseInt(val);
            }

            int change = amountPaid - totalAmount;
            lblChange.setText(rbFormat.format(Math.max(0, change)));
            lblChange.setForeground(change >= 0 ? Color.GREEN : Color.RED);
        } catch (Exception e) {
            lblChange.setText("Input Error");
        }
    }

    private void processTransaction() {
        calculateChange();

        if (amountPaid < totalAmount) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar kurang dari total tagihan!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int kembalian = amountPaid - totalAmount;

        Transaksi t = new Transaksi();
        t.setUserId(user.getId());
        t.setKodeTransaksi(transaksiDAO.generateNextCode());
        t.setNamaCustomer(customerName.isEmpty() ? "Umum" : customerName);
        t.setTotalPembayaran(totalAmount);
        t.setUangMasuk(amountPaid);
        t.setKembalian(kembalian);
        t.setMetodePembayaran(selectedMethod);
        t.setStatus("Selesai");

        if (transaksiDAO.saveTransaction(t, cartItems)) {
            dispose(); // Close checkout modal immediately

            // Show digital receipt with cashier name
            StrukModal struk = new StrukModal((JFrame) getParent(), t, cartItems, user.getUsername());
            struk.setVisible(true);

            onSuccess.run();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
