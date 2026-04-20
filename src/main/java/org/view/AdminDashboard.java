package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private User loggedInUser;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Brand Colors
    private final Color SIDEBAR_BG = new Color(28, 28, 30); // ~#1C1C1E
    private final Color MAIN_BG = new Color(18, 18, 18); // ~#121212
    private final Color CARD_BG = new Color(38, 38, 40); // ~#262628

    // Card Line Colors
    private final Color LINE_BLUE = new Color(59, 130, 246);
    private final Color LINE_GREEN = new Color(34, 197, 94);
    private final Color LINE_PURPLE = new Color(168, 85, 247);
    private final Color LINE_RED = new Color(239, 68, 68);
    private final Color LINE_YELLOW = new Color(245, 158, 11);
    private final Color LINE_CYAN = new Color(6, 182, 212);

    public AdminDashboard(User user) {
        this.loggedInUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Admin Dashboard - Toko Sport");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Base Panel
        JPanel basePanel = new JPanel(new BorderLayout());
        basePanel.setBackground(MAIN_BG);

        // Sidebar
        JPanel sidebar = createSidebar();
        basePanel.add(sidebar, BorderLayout.WEST);

        // Container for Main Content with Persistent Header
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(MAIN_BG);

        // Add Persistent Header
        contentArea.add(createPersistentHeader(), BorderLayout.NORTH);

        // Main Content Area (Cards)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        // Add views
        mainContentPanel.add(createDashboardOverview(), "Dashboard");
        mainContentPanel.add(new KategoriManagementPanel(), "Kategori");
        mainContentPanel.add(new MerekManagementPanel(), "Merek");
        mainContentPanel.add(createPlaceholder("Produk & Varian"), "Produk");
        mainContentPanel.add(new UkuranManagementPanel(), "Ukuran");
        mainContentPanel.add(new WarnaManagementPanel(), "Warna");
        mainContentPanel.add(new UserManagementPanel(), "Users");
        mainContentPanel.add(createPlaceholder("Laporan Keuangan"), "Laporan");

        contentArea.add(mainContentPanel, BorderLayout.CENTER);

        basePanel.add(contentArea, BorderLayout.CENTER);
        add(basePanel);
    }

    private JPanel createPersistentHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(30, 40, 40, 40));

        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel(
                "Selamat Datang, " + (loggedInUser != null ? loggedInUser.getUsername() : "admin"));
        lblWelcome.setFont(new Font("Inter", Font.PLAIN, 14));
        lblWelcome.setForeground(new Color(170, 170, 170));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblWelcome, BorderLayout.EAST);

        return headerPanel;
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

        // 1. App Title
        JLabel lblAppTitle = new JLabel("SPORT SHOES ADMIN");
        lblAppTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblAppTitle.setForeground(Color.WHITE);
        lblAppTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 10, 40, 10);
        sidebar.add(lblAppTitle, gbc);

        // Navigation Menu Container (to stack buttons tightly)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        // Add Menu Items corresponding to ERD tables
        menuPanel.add(createNavButton("Dashboard", "Dashboard"));
        menuPanel.add(Box.createVerticalStrut(2));
        menuPanel.add(createNavButton("Kategori", "Kategori"));
        menuPanel.add(Box.createVerticalStrut(2));
        menuPanel.add(createNavButton("Merek", "Merek"));
        menuPanel.add(Box.createVerticalStrut(2));
        menuPanel.add(createNavButton("Produk & Varian", "Produk"));
        menuPanel.add(Box.createVerticalStrut(2));
        menuPanel.add(createNavButton("Ukuran", "Ukuran"));
        menuPanel.add(Box.createVerticalStrut(2));
        menuPanel.add(createNavButton("Warna", "Warna"));
        menuPanel.add(Box.createVerticalStrut(2));
        menuPanel.add(createNavButton("Users", "Users"));
        menuPanel.add(Box.createVerticalStrut(2));
        menuPanel.add(createNavButton("Laporan Keuangan", "Laporan"));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 0, 5);
        sidebar.add(menuPanel, gbc);

        // Push lower items down
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        sidebar.add(Box.createVerticalGlue(), gbc);
        gbc.weighty = 0.0;

        // Logout Button at Bottom
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Inter", Font.BOLD, 14));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: #ff4b4b;" +
                "foreground: #ffffff;" +
                "arc: 0;" +
                "borderWidth: 0;" +
                "margin: 15,15,15,15;" +
                "hoverBackground: #e04444;" +
                "pressedBackground: #cc3e3e;");
        btnLogout.addActionListener(e -> handleLogout());

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0); // Flush to bottom and sides
        sidebar.add(btnLogout, gbc);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        btn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: #252527;" +
                "foreground: #dddddd;" +
                "arc: 0;" +
                "borderWidth: 0;" +
                "margin: 15,20,15,20;" +
                "hoverBackground: #333335;");
        btn.addActionListener(e -> cardLayout.show(mainContentPanel, cardName));
        return btn;
    }

    private JPanel createDashboardOverview() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Overview Section
        JPanel overviewContainer = new JPanel(new BorderLayout());
        overviewContainer.setOpaque(false);
        overviewContainer.setBorder(new EmptyBorder(40, 0, 0, 0));

        JLabel lblOverviewTitle = new JLabel("Dashboard Overview");
        lblOverviewTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblOverviewTitle.setForeground(Color.WHITE);
        lblOverviewTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        overviewContainer.add(lblOverviewTitle, BorderLayout.NORTH);

        // Cards Grid
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setOpaque(false);

        // 1. Total Kategori
        gridPanel.add(createStatCard("Total Kategori", "0", LINE_BLUE));
        // 2. Total Merek
        gridPanel.add(createStatCard("Total Merek", "0", LINE_GREEN));
        // 3. Total Produk
        gridPanel.add(createStatCard("Total Produk", "0", LINE_PURPLE));
        // 4. Total Transaksi
        gridPanel.add(createStatCard("Total Transaksi", "0", LINE_RED));
        // 5. Total Users
        gridPanel.add(createStatCard("Total Users", "0", LINE_YELLOW));
        // 6. Total Pendapatan
        gridPanel.add(createStatCard("Total Pendapatan", "Rp 0", LINE_CYAN));

        overviewContainer.add(gridPanel, BorderLayout.CENTER);

        // Push everything to top
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(overviewContainer, BorderLayout.NORTH);

        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String labelStr, String valueStr, Color lineColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15;");
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top line indicator (simulate with a thin panel)
        JPanel topIndicator = new JPanel();
        topIndicator.setBackground(lineColor);
        topIndicator.setPreferredSize(new Dimension(0, 3));

        // Wrapper for indicator with padding
        JPanel indicatorWrapper = new JPanel(new BorderLayout());
        indicatorWrapper.setOpaque(false);
        indicatorWrapper.add(topIndicator, BorderLayout.NORTH);
        indicatorWrapper.setBorder(new EmptyBorder(0, 0, 15, 0)); // space below line

        card.add(indicatorWrapper, BorderLayout.NORTH);

        // Center Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JLabel lblLabel = new JLabel(labelStr);
        lblLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        lblLabel.setForeground(new Color(200, 200, 200));

        JLabel lblValue = new JLabel(valueStr);
        lblValue.setFont(new Font("Inter", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBorder(new EmptyBorder(15, 0, 0, 0));

        contentPanel.add(lblLabel, BorderLayout.NORTH);
        contentPanel.add(lblValue, BorderLayout.CENTER);

        card.add(contentPanel, BorderLayout.CENTER);

        // Return wrapped in another panel if we want to enforce height, or let
        // GridLayout stretch it
        return card;
    }

    private JPanel createPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MAIN_BG);
        JLabel l = new JLabel("Halaman " + text + " - Dalam Pengembangan");
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Inter", Font.PLAIN, 24));
        p.add(l);
        return p;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            this.dispose();
        }
    }
}
