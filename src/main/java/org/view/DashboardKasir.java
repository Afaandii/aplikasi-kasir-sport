package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.KategoriDAO;
import org.dao.ProdukDAO;
import org.dao.TransaksiDAO;
import org.dao.VarianDAO;
import org.model.DetailTransaksi;
import org.model.Kategori;
import org.model.Produk;
import org.model.User;
import org.model.Varian;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardKasir extends JFrame {

    private User loggedInUser;
    private ProdukDAO produkDAO = new ProdukDAO();
    private VarianDAO varianDAO = new VarianDAO();
    private TransaksiDAO transaksiDAO = new TransaksiDAO();
    private KategoriDAO kategoriDAO = new KategoriDAO();

    private List<Produk> allProducts;
    private List<DetailTransaksi> cartItems = new ArrayList<>();
    private int selectedKategoriId = -1; // -1 for "Semua"
    private List<Kategori> categoryList;

    // UI Components
    private JPanel catalogGrid;
    private JTextField txtSearch;
    private JComboBox<String> cbKategori;
    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JTextField txtCustomer;

    // Brand Colors
    private final Color SIDEBAR_BG = new Color(28, 28, 30);
    private final Color MAIN_BG = new Color(18, 18, 18);
    private final Color CARD_BG = new Color(38, 38, 40);
    private final Color ACCENT_BLUE = new Color(59, 130, 246);

    private final NumberFormat rbFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public DashboardKasir(User user) {
        this.loggedInUser = user;
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setTitle("Dashboard Kasir - Toko Sport");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        JPanel basePanel = new JPanel(new BorderLayout());
        basePanel.setBackground(MAIN_BG);

        // 1. Sidebar (Left)
        basePanel.add(createSidebar(), BorderLayout.WEST);

        // Container for Header + Catalog
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);

        // 2. Header (North of center)
        centerContainer.add(createHeader(), BorderLayout.NORTH);

        // 3. Catalog Section (Center of center)
        centerContainer.add(createCatalogSection(), BorderLayout.CENTER);

        basePanel.add(centerContainer, BorderLayout.CENTER);

        // 4. Cart Section (East)
        basePanel.add(createCartSection(), BorderLayout.EAST);

        add(basePanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(45, 45, 45)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Logo Text
        JLabel lblLogo = new JLabel("SPORT SHOES POS");
        lblLogo.setFont(new Font("Inter", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 20, 50, 20);
        sidebar.add(lblLogo, gbc);

        // Menu Buttons Container
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        menuPanel.setOpaque(false);

        JButton btnKatalog = createMenuButton("Katalog Produk", true);
        JButton btnLaporan = createMenuButton("Laporan Saya", false);

        btnLaporan.addActionListener(e -> showLaporanModal());

        menuPanel.add(btnKatalog);
        menuPanel.add(btnLaporan);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 15, 0, 15);
        sidebar.add(menuPanel, gbc);

        // Push lower items down
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        sidebar.add(Box.createVerticalGlue(), gbc);

        // Logout
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Inter", Font.BOLD, 15));
        btnLogout.setPreferredSize(new Dimension(0, 55));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: #ef4444;" +
                "foreground: #ffffff;" +
                "arc: 0;" +
                "borderWidth: 0;" +
                "hoverBackground: #dc2626;");
        btnLogout.addActionListener(e -> handleLogout());

        gbc.gridy = 3;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        sidebar.add(btnLogout, gbc);

        return sidebar;
    }

    private JButton createMenuButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(0, 100));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String bg = active ? "#3b82f6" : "#252527";
        String fg = active ? "#ffffff" : "#dddddd";

        btn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: " + bg + ";" +
                "foreground: " + fg + ";" +
                "arc: 12;" +
                "borderWidth: 0;");
        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(25, 30, 10, 30));

        JLabel lblTitle = new JLabel("Cashier POS Terminal");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Kasir: " + loggedInUser.getUsername());
        lblUser.setFont(new Font("Inter", Font.PLAIN, 16));
        lblUser.setForeground(new Color(150, 150, 150));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblUser, BorderLayout.EAST);

        return header;
    }

    private JPanel createCatalogSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 30, 20, 30));

        // Filter Bar (Search + Cari Button + Dropdown) - Forced Single Row
        JPanel filterBar = new JPanel(new GridBagLayout());
        filterBar.setOpaque(false);
        filterBar.setBorder(new EmptyBorder(0, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 15);
        gbc.gridy = 0;

        // 1. Search Field
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari sepatu sport...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 10; showClearButton: true;");
        txtSearch.setPreferredSize(new Dimension(250, 42));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterProducts();
            }

            public void removeUpdate(DocumentEvent e) {
                filterProducts();
            }

            public void changedUpdate(DocumentEvent e) {
                filterProducts();
            }
        });
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        filterBar.add(txtSearch, gbc);

        // 2. Search Button
        JButton btnSearch = new JButton("Cari");
        btnSearch.setFont(new Font("Inter", Font.BOLD, 14));
        btnSearch.setPreferredSize(new Dimension(80, 42));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.putClientProperty(FlatClientProperties.STYLE,
                "background: #3b82f6; foreground: #ffffff; arc: 10; borderWidth: 0;");
        btnSearch.addActionListener(e -> filterProducts());
        gbc.gridx = 1;
        filterBar.add(btnSearch, gbc);

        // 3. Kategori Dropdown (To the right of Search Button)
        cbKategori = new JComboBox<>();
        cbKategori.setPreferredSize(new Dimension(180, 42));
        cbKategori.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        loadCategoryDropdown();
        cbKategori.addActionListener(e -> {
            int idx = cbKategori.getSelectedIndex();
            if (idx == 0)
                selectedKategoriId = -1;
            else if (idx > 0)
                selectedKategoriId = categoryList.get(idx - 1).getIdKategori();
            filterProducts();
        });
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0); // No inset for last element
        filterBar.add(cbKategori, gbc);

        // Spacer to push everything to the left
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        filterBar.add(Box.createHorizontalGlue(), gbc);

        panel.add(filterBar, BorderLayout.NORTH);

        // Catalog Grid - 3 Columns
        catalogGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        catalogGrid.setOpaque(false);

        // Wrapper to prevent stretching (pushes grid to North)
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(catalogGrid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void loadCategoryDropdown() {
        categoryList = kategoriDAO.findAll();
        cbKategori.removeAllItems();
        cbKategori.addItem("Semua Kategori");
        for (Kategori k : categoryList) {
            cbKategori.addItem(k.getNamaKategori());
        }
    }

    private void loadProducts() {
        allProducts = produkDAO.findAll();
        renderProducts(allProducts);
    }

    private void renderProducts(List<Produk> products) {
        catalogGrid.removeAll();
        for (Produk p : products) {
            catalogGrid.add(createProductCard(p));
        }
        catalogGrid.revalidate();
        catalogGrid.repaint();
    }

    private JPanel createProductCard(Produk p) {
        JPanel card = new JPanel(new BorderLayout(0, 3));
        card.setBackground(CARD_BG);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        card.setBorder(new EmptyBorder(5, 5, 5, 5)); // Tighter padding
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(150, 115)); // Shorter height for 3x2 grid

        // Thumbnail
        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            String path = "/thumbnail/" + p.getThumbnail();
            URL imgUrl = getClass().getResource(path);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                // Smaller scaling
                Image img = icon.getImage().getScaledInstance(120, 75, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(img));
            } else {
                lblImg.setText("No Image");
                lblImg.setFont(new Font("Inter", Font.PLAIN, 10));
                lblImg.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
        }
        card.add(lblImg, BorderLayout.CENTER);

        // Info
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 0));
        info.setOpaque(false);

        JLabel lblName = new JLabel(p.getNamaProduk());
        lblName.setFont(new Font("Inter", Font.BOLD, 11));
        lblName.setForeground(Color.WHITE);

        JLabel lblPrice = new JLabel(rbFormat.format(p.getHargaJual()));
        lblPrice.setFont(new Font("Inter", Font.BOLD, 10));
        lblPrice.setForeground(ACCENT_BLUE);

        info.add(lblName);
        info.add(lblPrice);
        card.add(info, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showVariantPicker(p);
            }
        });

        return card;
    }

    private void filterProducts() {
        String query = txtSearch.getText().toLowerCase();
        List<Produk> filtered = new ArrayList<>();
        for (Produk p : allProducts) {
            boolean matchCategory = (selectedKategoriId == -1 || p.getKategoriId() == selectedKategoriId);
            boolean matchSearch = p.getNamaProduk().toLowerCase().contains(query) ||
                    p.getMerekNama().toLowerCase().contains(query);

            if (matchCategory && matchSearch) {
                filtered.add(p);
            }
        }
        renderProducts(filtered);
    }

    private void showVariantPicker(Produk p) {
        List<Varian> variants = varianDAO.findByProdukId(p.getId());
        if (variants.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stok atau varian belum diatur untuk produk ini.");
            return;
        }

        JDialog dialog = new JDialog(this, "Varian: " + p.getNamaProduk(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(MAIN_BG);
        main.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("Pilih Ukuran & Warna:");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        main.add(lblTitle, BorderLayout.NORTH);

        JPanel gridSizes = new JPanel(new GridLayout(0, 2, 12, 12));
        gridSizes.setOpaque(false);

        for (Varian v : variants) {
            JButton btnSize = new JButton(v.getUkuranNama() + " - " + v.getWarnaNama());
            btnSize.setFont(new Font("Inter", Font.PLAIN, 12));

            if (v.getStokProduk() <= 0) {
                btnSize.setEnabled(false);
                btnSize.setText(v.getUkuranNama() + " (Habis)");
            }

            btnSize.putClientProperty(FlatClientProperties.STYLE,
                    "background: #252527; foreground: #dddddd; arc: 10; hoverBackground: #3b82f6;");

            btnSize.addActionListener(e -> {
                addToCart(p, v);
                dialog.dispose();
            });
            gridSizes.add(btnSize);
        }

        JScrollPane scroll = new JScrollPane(gridSizes);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        main.add(scroll, BorderLayout.CENTER);
        dialog.add(main);
        dialog.setVisible(true);
    }

    private void addToCart(Produk p, Varian v) {
        for (DetailTransaksi item : cartItems) {
            if (item.getVarianId() == v.getId()) {
                if (item.getJumlah() + 1 > v.getStokProduk()) {
                    JOptionPane.showMessageDialog(this, "Stok tidak cukup!");
                    return;
                }
                item.setJumlah(item.getJumlah() + 1);
                item.setSubtotal(item.getJumlah() * item.getHargaSatuan());
                updateCartTable();
                return;
            }
        }

        DetailTransaksi dt = new DetailTransaksi();
        dt.setVarianId(v.getId());
        dt.setNamaProduk(p.getNamaProduk());
        dt.setUkuranNama(v.getUkuranNama());
        dt.setWarnaNama(v.getWarnaNama());
        dt.setHargaSatuan(p.getHargaJual());
        dt.setJumlah(1);
        dt.setSubtotal(p.getHargaJual());

        cartItems.add(dt);
        updateCartTable();
    }

    private void updateCartTable() {
        cartModel.setRowCount(0);
        int total = 0;
        for (DetailTransaksi dt : cartItems) {
            cartModel.addRow(new Object[] {
                    dt.getNamaProduk(),
                    dt.getUkuranNama() + "/" + dt.getWarnaNama(),
                    dt.getJumlah(),
                    rbFormat.format(dt.getSubtotal()),
                    "" // Column for Trash/Delete button
            });
            total += dt.getSubtotal();
        }
        lblTotal.setText(rbFormat.format(total));
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }

        int total = 0;
        for (DetailTransaksi dt : cartItems)
            total += dt.getSubtotal();

        // Use the new professional CheckoutModal
        CheckoutModal modal = new CheckoutModal(
            this, 
            loggedInUser, 
            total, 
            new ArrayList<>(cartItems), // Pass a copy to avoid concurrency issues
            txtCustomer.getText(), 
            () -> {
                // Success Callback: Reset UI
                cartItems.clear();
                updateCartTable();
                txtCustomer.setText("");
                loadProducts(); // Refresh products (stock changed)
            }
        );
        modal.setVisible(true);
    }

    private JPanel createCartSection() {
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(420, 0));
        cartPanel.setBackground(SIDEBAR_BG);
        cartPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblCartTitle = new JLabel("Keranjang Belanja");
        lblCartTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblCartTitle.setForeground(Color.WHITE);
        lblCartTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        cartPanel.add(lblCartTitle, BorderLayout.NORTH);

        // Table Cart
        String[] cols = { "Produk", "Varian", "Qty", "Total", "" };
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 2 || c == 4; // Qty and Trash column are editable
            }
        };
        tblCart = new JTable(cartModel);
        tblCart.setBackground(SIDEBAR_BG);
        tblCart.setForeground(Color.WHITE);
        tblCart.setShowGrid(false);
        tblCart.setRowHeight(50);
        tblCart.setSelectionBackground(SIDEBAR_BG); // Remove blue selection mark
        tblCart.setSelectionForeground(Color.WHITE);
        tblCart.setFocusable(false);

        // Setup Qty Column
        tblCart.getColumnModel().getColumn(2).setCellRenderer(new QtyCellRenderer());
        tblCart.getColumnModel().getColumn(2).setCellEditor(new QtyCellEditor());
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Setup Trash/Action Column
        tblCart.getColumnModel().getColumn(4).setCellRenderer(new TrashCellRenderer());
        tblCart.getColumnModel().getColumn(4).setCellEditor(new TrashCellEditor());
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(50);

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.getViewport().setBackground(SIDEBAR_BG);
        scrollCart.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(45, 45, 45)));
        cartPanel.add(scrollCart, BorderLayout.CENTER);

        // Bottom Section
        JPanel bottomPanel = new JPanel(new GridLayout(0, 1, 0, 15));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        txtCustomer = new JTextField();
        txtCustomer.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Pelanggan");
        txtCustomer.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        txtCustomer.setPreferredSize(new Dimension(0, 45));

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Inter", Font.BOLD, 32));
        lblTotal.setForeground(ACCENT_BLUE);
        JLabel lblTotalLabel = new JLabel("Total Transaksi");
        lblTotalLabel.setForeground(Color.GRAY);
        totalPanel.add(lblTotalLabel, BorderLayout.NORTH);
        totalPanel.add(lblTotal, BorderLayout.CENTER);

        JButton btnCheckout = new JButton("Bayar & Simpan");
        btnCheckout.setFont(new Font("Inter", Font.BOLD, 16));
        btnCheckout.setPreferredSize(new Dimension(0, 55));
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.putClientProperty(FlatClientProperties.STYLE,
                "background: #3b82f6; foreground: #ffffff; arc: 12; borderWidth: 0;");
        btnCheckout.addActionListener(e -> handleCheckout());

        bottomPanel.add(txtCustomer);
        bottomPanel.add(totalPanel);
        bottomPanel.add(btnCheckout);

        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        return cartPanel;
    }

    // --- Inner Classes for Interactive Table Columns ---

    class QtyCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton btnMinus = new JButton("-");
        private final JButton btnPlus = new JButton("+");
        private final JLabel lblQty = new JLabel("1", SwingConstants.CENTER);

        public QtyCellRenderer() {
            setOpaque(true);
            setBackground(SIDEBAR_BG);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            styleButton(btnMinus, false);
            styleButton(btnPlus, true);
            lblQty.setForeground(Color.WHITE);
            lblQty.setPreferredSize(new Dimension(20, 30));
            add(btnMinus);
            add(lblQty);
            add(btnPlus);
        }

        private void styleButton(JButton btn, boolean isPlus) {
            btn.setPreferredSize(new Dimension(28, 28));
            String activeBg = isPlus ? "#3b82f6" : "#444446";
            btn.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 8; background: " + activeBg + "; foreground: #ffffff; borderWidth: 0;");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            int qty = (int) value;
            lblQty.setText(String.valueOf(qty));
            btnMinus.setEnabled(qty > 1);
            setBackground(SIDEBAR_BG);
            return this;
        }
    }

    class QtyCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        private final JButton btnMinus = new JButton("-");
        private final JButton btnPlus = new JButton("+");
        private final JLabel lblQty = new JLabel("1", SwingConstants.CENTER);
        private int currentValue;
        private int currentRow;

        public QtyCellEditor() {
            panel.setOpaque(true);
            panel.setBackground(SIDEBAR_BG);
            styleButton(btnMinus, false);
            styleButton(btnPlus, true);
            lblQty.setForeground(Color.WHITE);
            lblQty.setPreferredSize(new Dimension(20, 30));

            btnPlus.addActionListener(e -> {
                DetailTransaksi dt = cartItems.get(currentRow);
                dt.setJumlah(dt.getJumlah() + 1);
                dt.setSubtotal(dt.getJumlah() * dt.getHargaSatuan());
                lblQty.setText(String.valueOf(dt.getJumlah()));
                btnMinus.setEnabled(true);
                cartModel.setValueAt(dt.getJumlah(), currentRow, 2);
                cartModel.setValueAt(rbFormat.format(dt.getSubtotal()), currentRow, 3);
                calculateTotal();
            });

            btnMinus.addActionListener(e -> {
                DetailTransaksi dt = cartItems.get(currentRow);
                if (dt.getJumlah() > 1) {
                    dt.setJumlah(dt.getJumlah() - 1);
                    dt.setSubtotal(dt.getJumlah() * dt.getHargaSatuan());
                    lblQty.setText(String.valueOf(dt.getJumlah()));
                    btnMinus.setEnabled(dt.getJumlah() > 1);
                    cartModel.setValueAt(dt.getJumlah(), currentRow, 2);
                    cartModel.setValueAt(rbFormat.format(dt.getSubtotal()), currentRow, 3);
                    calculateTotal();
                }
            });

            panel.add(btnMinus);
            panel.add(lblQty);
            panel.add(btnPlus);
        }

        private void styleButton(JButton btn, boolean isPlus) {
            btn.setPreferredSize(new Dimension(28, 28));
            String activeBg = isPlus ? "#3b82f6" : "#444446";
            btn.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 8; background: " + activeBg + "; foreground: #ffffff; borderWidth: 0;");
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentValue = (int) value;
            currentRow = row;
            lblQty.setText(String.valueOf(currentValue));
            btnMinus.setEnabled(currentValue > 1);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }
    }

    class TrashCellRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public TrashCellRenderer() {
            setOpaque(true);
            setBackground(SIDEBAR_BG);
            setText("X");
            setForeground(new Color(239, 68, 68));
            setFont(new Font("Inter", Font.BOLD, 14));
            putClientProperty(FlatClientProperties.STYLE, "background: #252527; arc: 8; borderWidth: 0;");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, 
                int row, int column) {
            return this;
        }
    }

    class TrashCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JButton btnDelete = new JButton("X");
        private int currentRow;

        public TrashCellEditor() {
            btnDelete.setForeground(new Color(239, 68, 68));
            btnDelete.setFont(new Font("Inter", Font.BOLD, 14));
            btnDelete.putClientProperty(FlatClientProperties.STYLE, "background: #252527; arc: 8; borderWidth: 0;");
            btnDelete.addActionListener(e -> {
                cartItems.remove(currentRow);
                updateCartTable();
                calculateTotal();
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return btnDelete;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void calculateTotal() {
        int total = 0;
        for (DetailTransaksi dt : cartItems) {
            total += dt.getSubtotal();
        }
        lblTotal.setText(rbFormat.format(total));
    }

    private void showLaporanModal() {
        new LaporanKasirModal(this, loggedInUser).setVisible(true);
    }

    private void handleLogout() {
        if (JOptionPane.showConfirmDialog(this, "Logout?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            this.dispose();
        }
    }
}
