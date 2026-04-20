package org.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.dao.KategoriDAO;
import org.dao.MerekDAO;
import org.dao.ProdukDAO;
import org.model.Kategori;
import org.model.Merek;
import org.model.Produk;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ProdukFormDialog extends JDialog {

    private JTextField txtKode, txtNama, txtHargaPokok, txtHargaJual, txtThumbnail;
    private JLabel lblPreview;
    private JComboBox<Kategori> cbKategori;
    private JComboBox<Merek> cbMerek;
    private JButton btnSimpan, btnPilih;
    private Produk produk;
    private ProdukDAO produkDAO;
    private boolean isSuccess = false;
    private File selectedFile;

    private final String THUMBNAIL_PATH = "src/main/resources/thumbnail/";

    public ProdukFormDialog(Frame owner, Produk produk) {
        super(owner, true);
        this.produk = produk;
        this.produkDAO = new ProdukDAO();
        initComponents();
        loadDropdowns();

        if (produk != null) {
            setTitle("Edit Produk");
            txtKode.setText(produk.getKodeProduk());
            txtNama.setText(produk.getNamaProduk());
            txtHargaPokok.setText(String.valueOf(produk.getHargaPokok()));
            txtHargaJual.setText(String.valueOf(produk.getHargaJual()));
            txtThumbnail.setText(produk.getThumbnail());
            btnSimpan.setText("Update Produk");
            
            if (produk.getThumbnail() != null && !produk.getThumbnail().isEmpty()) {
                previewImage(THUMBNAIL_PATH + produk.getThumbnail());
            }
        } else {
            setTitle("Tambah Produk Baru");
            txtKode.setText(produkDAO.generateNextCode());
        }

        txtKode.setEditable(false);
    }

    private void initComponents() {
        setSize(500, 650);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(30, 30, 32));
        content.setBorder(new EmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel(produk == null ? "Tambah Produk" : "Update Produk");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        content.add(lblTitle, gbc);

        // Fields
        addLabel(content, "Kode Produk", 1, gbc);
        txtKode = createTextField("");
        txtKode.setEditable(false);
        txtKode.putClientProperty(FlatClientProperties.STYLE, "background: #222;");
        gbc.gridy = 2;
        content.add(txtKode, gbc);

        addLabel(content, "Nama Produk", 3, gbc);
        txtNama = createTextField("Masukkan nama produk...");
        gbc.gridy = 4;
        content.add(txtNama, gbc);

        // Kategori & Merek in one row
        JPanel rowCombo = new JPanel(new GridLayout(1, 2, 20, 0));
        rowCombo.setOpaque(false);

        JPanel colKat = new JPanel(new BorderLayout(0, 5));
        colKat.setOpaque(false);
        JLabel lKat = new JLabel("Kategori");
        lKat.setForeground(new Color(180, 180, 180));
        cbKategori = new JComboBox<>();
        colKat.add(lKat, BorderLayout.NORTH);
        colKat.add(cbKategori, BorderLayout.CENTER);

        JPanel colMerk = new JPanel(new BorderLayout(0, 5));
        colMerk.setOpaque(false);
        JLabel lMerk = new JLabel("Merek");
        lMerk.setForeground(new Color(180, 180, 180));
        cbMerek = new JComboBox<>();
        colMerk.add(lMerk, BorderLayout.NORTH);
        colMerk.add(cbMerek, BorderLayout.CENTER);

        rowCombo.add(colKat);
        rowCombo.add(colMerk);

        gbc.gridy = 5;
        gbc.insets = new Insets(15, 0, 15, 0);
        content.add(rowCombo, gbc);

        // Prices
        JPanel rowPrice = new JPanel(new GridLayout(1, 2, 20, 0));
        rowPrice.setOpaque(false);

        JPanel colPokok = new JPanel(new BorderLayout(0, 5));
        colPokok.setOpaque(false);
        JLabel lPokok = new JLabel("Harga Pokok");
        lPokok.setForeground(new Color(180, 180, 180));
        txtHargaPokok = createTextField("0");
        colPokok.add(lPokok, BorderLayout.NORTH);
        colPokok.add(txtHargaPokok, BorderLayout.CENTER);

        JPanel colJual = new JPanel(new BorderLayout(0, 5));
        colJual.setOpaque(false);
        JLabel lJual = new JLabel("Harga Jual");
        lJual.setForeground(new Color(180, 180, 180));
        txtHargaJual = createTextField("0");
        colJual.add(lJual, BorderLayout.NORTH);
        colJual.add(txtHargaJual, BorderLayout.CENTER);

        rowPrice.add(colPokok);
        rowPrice.add(colJual);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        content.add(rowPrice, gbc);

        // Thumbnail Row
        JPanel colThumb = new JPanel(new BorderLayout(0, 5));
        colThumb.setOpaque(false);
        JLabel lThumb = new JLabel("Thumbnail");
        lThumb.setForeground(new Color(180, 180, 180));
        
        JPanel thumbAction = new JPanel(new BorderLayout(10, 0));
        thumbAction.setOpaque(false);
        txtThumbnail = createTextField("Belum ada file...");
        txtThumbnail.setEditable(false);
        btnPilih = new JButton("Pilih File");
        btnPilih.putClientProperty(FlatClientProperties.STYLE, "background: #444; foreground: #fff; arc: 10;");
        btnPilih.addActionListener(e -> handlePilihFile());
        
        thumbAction.add(txtThumbnail, BorderLayout.CENTER);
        thumbAction.add(btnPilih, BorderLayout.EAST);
        
        colThumb.add(lThumb, BorderLayout.NORTH);
        colThumb.add(thumbAction, BorderLayout.CENTER);
        
        gbc.gridy = 7;
        content.add(colThumb, gbc);

        // Preview Label
        lblPreview = new JLabel("No Preview", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(150, 150));
        lblPreview.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        lblPreview.setForeground(new Color(100, 100, 100));
        
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        content.add(lblPreview, gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset to horizontal for buttons
        gbc.anchor = GridBagConstraints.WEST;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton btnBatal = new JButton("Batal");
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.putClientProperty(FlatClientProperties.STYLE,
                "background: #444; foreground: #eee; arc: 10; borderWidth: 0; margin: 8,20,8,20;");
        btnBatal.addActionListener(e -> dispose());

        btnSimpan = new JButton("Simpan");
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.putClientProperty(FlatClientProperties.STYLE,
                "background: #3c78d8; foreground: #ffffff; arc: 10; borderWidth: 0; margin: 8,20,8,20;");
        btnSimpan.addActionListener(e -> handleAction());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);

        gbc.gridy = 9;
        gbc.insets = new Insets(30, 0, 0, 0);
        content.add(buttonPanel, gbc);

        add(content);

        // Combo Renderers
        cbKategori.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Kategori) {
                    setText(((Kategori) value).getNamaKategori());
                }
                return this;
            }
        });

        cbMerek.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Merek) {
                    setText(((Merek) value).getNamaMerek());
                }
                return this;
            }
        });
    }

    private void addLabel(JPanel p, String text, int y, GridBagConstraints gbc) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 180, 180));
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(l, gbc);
    }

    private JTextField createTextField(String placeholder) {
        JTextField t = new JTextField();
        t.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        t.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #3c3c3e; borderWidth: 0; margin: 8,12,8,12;");
        return t;
    }

    private void loadDropdowns() {
        List<Kategori> kats = new KategoriDAO().findAll();
        for (Kategori k : kats) {
            cbKategori.addItem(k);
            if (produk != null && k.getIdKategori() == produk.getKategoriId()) {
                cbKategori.setSelectedItem(k);
            }
        }

        List<Merek> merks = new MerekDAO().findAll();
        for (Merek m : merks) {
            cbMerek.addItem(m);
            if (produk != null && m.getId() == produk.getMerekId()) {
                cbMerek.setSelectedItem(m);
            }
        }
    }

    private void handleAction() {
        try {
            String kode = txtKode.getText().trim();
            String nama = txtNama.getText().trim();
            String hPokokStr = txtHargaPokok.getText().trim();
            String hJualStr = txtHargaJual.getText().trim();
            Kategori kat = (Kategori) cbKategori.getSelectedItem();
            Merek merk = (Merek) cbMerek.getSelectedItem();

            if (kode.isEmpty() || nama.isEmpty() || kat == null || merk == null) {
                JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (produk == null)
                produk = new Produk();
            
            // Handle Thumbnail Copying
            if (selectedFile != null) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    File dest = new File(THUMBNAIL_PATH + fileName);
                    Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    produk.setThumbnail(fileName);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            produk.setKodeProduk(kode);
            produk.setNamaProduk(nama);
            produk.setKategoriId(kat.getIdKategori());
            produk.setMerekId(merk.getId());
            produk.setHargaPokok(Integer.parseInt(hPokokStr));
            produk.setHargaJual(Integer.parseInt(hJualStr));

            if (produk.getId() == 0) {
                if (produkDAO.insert(produk)) {
                    isSuccess = true;
                    dispose();
                }
            } else {
                if (produkDAO.update(produk)) {
                    isSuccess = true;
                    dispose();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePilihFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (JPG, PNG, JPEG)", "jpg", "png", "jpeg");
        chooser.setFileFilter(filter);
        
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            txtThumbnail.setText(selectedFile.getName());
            previewImage(selectedFile.getAbsolutePath());
        }
    }

    private void previewImage(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            if (img != null) {
                Image scaled = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblPreview.setIcon(new ImageIcon(scaled));
                lblPreview.setText("");
            }
        } catch (IOException e) {
            lblPreview.setText("Error Preview");
            lblPreview.setIcon(null);
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
