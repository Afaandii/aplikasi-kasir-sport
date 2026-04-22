package org.view;

// import com.formdev.flatlaf.FlatClientProperties;
import org.model.DetailTransaksi;
import org.model.Transaksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StrukModal extends JDialog {

    private final Transaksi transaksi;
    private final List<DetailTransaksi> items;
    private final String kasirName;
    private final NumberFormat rbFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public StrukModal(Window parent, Transaksi t, List<DetailTransaksi> items, String kasirName) {
        super(parent, "Struk Belanja", ModalityType.APPLICATION_MODAL);
        this.transaksi = t;
        this.items = items;
        this.kasirName = kasirName;

        initComponents();
    }

    private void initComponents() {
        setSize(400, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));
        setLayout(new BorderLayout());

        // Receipt Paper
        JPanel paper = new JPanel();
        paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
        paper.setBackground(Color.WHITE);
        paper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 30, 20, 30)));

        // 1. Header
        addCenteredLabel(paper, "SPORT SHOES POS", new Font("Monospaced", Font.BOLD, 18));
        addCenteredLabel(paper, "Griya Sport - Jakarta", new Font("Monospaced", Font.PLAIN, 12));
        addCenteredLabel(paper, "--------------------------------", new Font("Monospaced", Font.PLAIN, 12));

        // 2. Transaksi Info
        addLabelValue(paper, "Tgl   : ",
                df.format(transaksi.getCreatedAt() != null ? transaksi.getCreatedAt() : new java.util.Date()));
        addLabelValue(paper, "No    : ", transaksi.getKodeTransaksi());
        addLabelValue(paper, "Kasir : ", kasirName);
        addLabelValue(paper, "Cust  : ", transaksi.getNamaCustomer());
        addCenteredLabel(paper, "================================", new Font("Monospaced", Font.PLAIN, 12));

        // 3. Items
        for (DetailTransaksi item : items) {
            String name = item.getNamaProduk();
            if (name.length() > 25)
                name = name.substring(0, 22) + "...";

            // Line 1: Item Name (Varian)
            addCenteredLabel(paper, name + " (" + item.getUkuranNama() + "/" + item.getWarnaNama() + ")",
                    new Font("Monospaced", Font.PLAIN, 12), FlowLayout.LEFT);

            // Line 2: Qty x Price ... Subtotal
            String line2 = String.format("%2d x %10s  %12s",
                    item.getJumlah(),
                    formatCurrency(item.getHargaSatuan()),
                    formatCurrency(item.getSubtotal()));
            addCenteredLabel(paper, line2, new Font("Monospaced", Font.PLAIN, 12));
        }

        addCenteredLabel(paper, "--------------------------------", new Font("Monospaced", Font.PLAIN, 12));

        // 4. Totals
        addSummaryLine(paper, "TOTAL      : ", formatCurrency(transaksi.getTotalPembayaran()));
        addSummaryLine(paper, transaksi.getMetodePembayaran().toUpperCase() + "      : ",
                formatCurrency(transaksi.getUangMasuk()));
        addSummaryLine(paper, "KEMBALIAN  : ", formatCurrency(transaksi.getKembalian()));

        addCenteredLabel(paper, "================================", new Font("Monospaced", Font.PLAIN, 12));

        // 5. Footer
        addCenteredLabel(paper, "Terima kasih atas kunjungan Anda", new Font("Monospaced", Font.ITALIC, 11));
        addCenteredLabel(paper, "Barang yang sudah dibeli", new Font("Monospaced", Font.PLAIN, 10));
        addCenteredLabel(paper, "tidak dapat ditukar/dikembalikan", new Font("Monospaced", Font.PLAIN, 10));

        // Note: No close button as per user request (use window close)
        JScrollPane scroll = new JScrollPane(paper);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private void addCenteredLabel(JPanel p, String text, Font f) {
        addCenteredLabel(p, text, f, FlowLayout.CENTER);
    }

    private void addCenteredLabel(JPanel p, String text, Font f, int align) {
        JPanel line = new JPanel(new FlowLayout(align));
        line.setOpaque(false);
        JLabel lbl = new JLabel(text);
        lbl.setFont(f);
        lbl.setForeground(Color.BLACK); // Make it clearly visible
        line.add(lbl);
        p.add(line);
    }

    private void addLabelValue(JPanel p, String label, String value) {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line.setOpaque(false);
        JLabel lbl = new JLabel(label + value);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lbl.setForeground(Color.BLACK);
        line.add(lbl);
        p.add(line);
    }

    private void addSummaryLine(JPanel p, String label, String value) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setBorder(new EmptyBorder(0, 5, 0, 5));

        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblL.setForeground(Color.BLACK);

        JLabel lblR = new JLabel(value);
        lblR.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblR.setForeground(Color.BLACK);

        line.add(lblL, BorderLayout.WEST);
        line.add(lblR, BorderLayout.EAST);
        p.add(line);
    }

    private String formatCurrency(int amount) {
        return rbFormat.format(amount).replace(",00", "");
    }
}
