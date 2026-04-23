package org.dao;

import org.model.Transaksi;
import org.model.DetailTransaksi;
import org.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransaksiDAO {

    public boolean saveTransaction(Transaksi t, List<DetailTransaksi> details) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Transaksi
            String sqlTransaksi = "INSERT INTO transaksi (user_id, kode_transaksi, nama_customer, total_pembayaran, uang_masuk, kembalian, metode_pembayaran, status, created_at, updated_at) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            int transaksiId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTransaksi, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, t.getUserId());
                pstmt.setString(2, t.getKodeTransaksi());
                pstmt.setString(3, t.getNamaCustomer());
                pstmt.setInt(4, t.getTotalPembayaran());
                pstmt.setInt(5, t.getUangMasuk());
                pstmt.setInt(6, t.getKembalian());
                pstmt.setString(7, t.getMetodePembayaran());
                pstmt.setString(8, t.getStatus());

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        transaksiId = rs.getInt(1);
                    }
                }
            }

            if (transaksiId == -1)
                throw new SQLException("Gagal mendapatkan ID Transaksi.");

            // 2. Insert Detail & Update Stock
            String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id, varian_id, jumlah, harga_satuan, subtotal, created_at, updated_at) "
                    +
                    "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
            String sqlUpdateStock = "UPDATE varian SET stok_produk = stok_produk - ?, updated_at = NOW() WHERE id = ?";

            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail);
                    PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {

                for (DetailTransaksi d : details) {
                    // Insert Detail
                    pstmtDetail.setInt(1, transaksiId);
                    pstmtDetail.setInt(2, d.getVarianId());
                    pstmtDetail.setInt(3, d.getJumlah());
                    pstmtDetail.setInt(4, d.getHargaSatuan());
                    pstmtDetail.setInt(5, d.getSubtotal());
                    pstmtDetail.addBatch();

                    // Update Stock
                    pstmtStock.setInt(1, d.getJumlah());
                    pstmtStock.setInt(2, d.getVarianId());
                    pstmtStock.addBatch();
                }

                pstmtDetail.executeBatch();
                pstmtStock.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error saveTransaction: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String generateNextCode() {
        String datePrefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sql = "SELECT kode_transaksi FROM transaksi WHERE kode_transaksi LIKE ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "TRX-" + datePrefix + "-%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastCode = rs.getString("kode_transaksi");
                    int lastNum = Integer.parseInt(lastCode.substring(lastCode.lastIndexOf("-") + 1));
                    return String.format("TRX-%s-%03d", datePrefix, lastNum + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generateNextCode: " + e.getMessage());
        }
        return String.format("TRX-%s-001", datePrefix);
    }

    public List<Transaksi> getFilteredTransactions(String startDate, String endDate, int userId, String method) {
        List<Transaksi> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, u.username as kasir_nama, " +
                        "(SELECT SUM(jumlah) FROM detail_transaksi WHERE transaksi_id = t.id) as total_item " +
                        "FROM transaksi t JOIN user u ON t.user_id = u.id WHERE 1=1 ");

        if (startDate != null && !startDate.isEmpty())
            sql.append("AND DATE(t.created_at) >= ? ");
        if (endDate != null && !endDate.isEmpty())
            sql.append("AND DATE(t.created_at) <= ? ");
        if (userId > 0)
            sql.append("AND t.user_id = ? ");
        if (method != null && !method.equals("Semua"))
            sql.append("AND t.metode_pembayaran = ? ");

        sql.append("ORDER BY t.created_at DESC");

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIdx = 1;
            if (startDate != null && !startDate.isEmpty())
                pstmt.setString(paramIdx++, startDate);
            if (endDate != null && !endDate.isEmpty())
                pstmt.setString(paramIdx++, endDate);
            if (userId > 0)
                pstmt.setInt(paramIdx++, userId);
            if (method != null && !method.equals("Semua"))
                pstmt.setString(paramIdx++, method);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setKodeTransaksi(rs.getString("kode_transaksi"));
                    t.setNamaCustomer(rs.getString("nama_customer"));
                    t.setTotalPembayaran(rs.getInt("total_pembayaran"));
                    t.setUangMasuk(rs.getInt("uang_masuk"));
                    t.setKembalian(rs.getInt("kembalian"));
                    t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                    t.setStatus(rs.getString("status"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    t.setKasirNama(rs.getString("kasir_nama"));
                    t.setTotalItem(rs.getInt("total_item"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getFilteredTransactions: " + e.getMessage());
        }
        return list;
    }

    public Object[] getReportSummary(String startDate, String endDate, int userId, String method) {
        // [0] Total Terjual, [1] Total Omzet, [2] Total Laba
        long totalTerjual = 0;
        long totalOmzet = 0;
        long totalLaba = 0;

        StringBuilder sql = new StringBuilder(
                "SELECT IFNULL(SUM(dt.jumlah), 0) as terjual, IFNULL(SUM(dt.subtotal), 0) as omzet, " +
                        "IFNULL(SUM((dt.harga_satuan - p.harga_pokok) * dt.jumlah), 0) as laba " +
                        "FROM detail_transaksi dt " +
                        "JOIN transaksi t ON dt.transaksi_id = t.id " +
                        "JOIN varian v ON dt.varian_id = v.id " +
                        "JOIN produk p ON v.produk_id = p.id " +
                        "WHERE 1=1 ");

        if (startDate != null && !startDate.isEmpty())
            sql.append("AND DATE(t.created_at) >= ? ");
        if (endDate != null && !endDate.isEmpty())
            sql.append("AND DATE(t.created_at) <= ? ");
        if (userId > 0)
            sql.append("AND t.user_id = ? ");
        if (method != null && !method.equals("Semua"))
            sql.append("AND t.metode_pembayaran = ? ");

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIdx = 1;
            if (startDate != null && !startDate.isEmpty())
                pstmt.setString(paramIdx++, startDate);
            if (endDate != null && !endDate.isEmpty())
                pstmt.setString(paramIdx++, endDate);
            if (userId > 0)
                pstmt.setInt(paramIdx++, userId);
            if (method != null && !method.equals("Semua"))
                pstmt.setString(paramIdx++, method);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalTerjual = rs.getLong("terjual");
                    totalOmzet = rs.getLong("omzet");
                    totalLaba = rs.getLong("laba");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getReportSummary: " + e.getMessage());
        }

        return new Object[] { totalTerjual, totalOmzet, totalLaba };
    }

    public List<Transaksi> findByKasirToday(int userId) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE user_id = ? AND DATE(created_at) = CURDATE() ORDER BY created_at DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setKodeTransaksi(rs.getString("kode_transaksi"));
                    t.setNamaCustomer(rs.getString("nama_customer"));
                    t.setTotalPembayaran(rs.getInt("total_pembayaran"));
                    t.setUangMasuk(rs.getInt("uang_masuk"));
                    t.setKembalian(rs.getInt("kembalian"));
                    t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                    t.setStatus(rs.getString("status"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error findByKasirToday: " + e.getMessage());
        }
        return list;
    }

    public List<DetailTransaksi> findDetails(int transaksiId) {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT dt.*, p.nama_produk, u.nama_ukuran, w.nama_warna " +
                "FROM detail_transaksi dt " +
                "JOIN varian v ON dt.varian_id = v.id " +
                "JOIN produk p ON v.produk_id = p.id " +
                "JOIN ukuran u ON v.ukuran_id = u.id " +
                "JOIN warna w ON v.warna_id = w.id " +
                "WHERE dt.transaksi_id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaksiId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailTransaksi d = new DetailTransaksi();
                    d.setId(rs.getInt("id"));
                    d.setTransaksiId(rs.getInt("transaksi_id"));
                    d.setVarianId(rs.getInt("varian_id"));
                    d.setJumlah(rs.getInt("jumlah"));
                    d.setHargaSatuan(rs.getInt("harga_satuan"));
                    d.setSubtotal(rs.getInt("subtotal"));
                    d.setNamaProduk(rs.getString("nama_produk"));
                    d.setUkuranNama(rs.getString("nama_ukuran"));
                    d.setWarnaNama(rs.getString("nama_warna"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error findDetails: " + e.getMessage());
        }
        return list;
    }
}
