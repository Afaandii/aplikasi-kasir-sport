package org.dao;

import org.model.Produk;
import org.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {

    public List<Produk> findAll() {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori, m.nama_merek " +
                "FROM produk p " +
                "LEFT JOIN kategori k ON p.kategori_id = k.id " +
                "LEFT JOIN merek m ON p.merek_id = m.id " +
                "ORDER BY p.id DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Produk p = mapResultSetToProduk(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error findAll Produk: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Produk p) {
        String sql = "INSERT INTO produk (kategori_id, merek_id, kode_produk, nama_produk, harga_pokok, harga_jual, thumbnail, created_at, updated_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, p.getKategoriId());
            pstmt.setInt(2, p.getMerekId());
            pstmt.setString(3, p.getKodeProduk());
            pstmt.setString(4, p.getNamaProduk());
            pstmt.setInt(5, p.getHargaPokok());
            pstmt.setInt(6, p.getHargaJual());
            pstmt.setString(7, p.getThumbnail());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Produk: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Produk p) {
        String sql = "UPDATE produk SET kategori_id = ?, merek_id = ?, kode_produk = ?, nama_produk = ?, " +
                "harga_pokok = ?, harga_jual = ?, thumbnail = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, p.getKategoriId());
            pstmt.setInt(2, p.getMerekId());
            pstmt.setString(3, p.getKodeProduk());
            pstmt.setString(4, p.getNamaProduk());
            pstmt.setInt(5, p.getHargaPokok());
            pstmt.setInt(6, p.getHargaJual());
            pstmt.setString(7, p.getThumbnail());
            pstmt.setInt(8, p.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Produk: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM produk WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete Produk: " + e.getMessage());
            return false;
        }
    }

    public String generateNextCode() {
        String sql = "SELECT kode_produk FROM produk ORDER BY id DESC LIMIT 1";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastCode = rs.getString("kode_produk");
                if (lastCode != null && lastCode.startsWith("PRD-")) {
                    try {
                        int lastNum = Integer.parseInt(lastCode.substring(4));
                        return String.format("PRD-%03d", lastNum + 1);
                    } catch (NumberFormatException e) {
                        return "PRD-001";
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating next code: " + e.getMessage());
        }
        return "PRD-001";
    }

    private Produk mapResultSetToProduk(ResultSet rs) throws SQLException {
        Produk p = new Produk();
        p.setId(rs.getInt("id"));
        p.setKategoriId(rs.getInt("kategori_id"));
        p.setMerekId(rs.getInt("merek_id"));
        p.setKodeProduk(rs.getString("kode_produk"));
        p.setNamaProduk(rs.getString("nama_produk"));
        p.setHargaPokok(rs.getInt("harga_pokok"));
        p.setHargaJual(rs.getInt("harga_jual"));
        p.setThumbnail(rs.getString("thumbnail"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Joined fields
        p.setKategoriNama(rs.getString("nama_kategori"));
        p.setMerekNama(rs.getString("nama_merek"));

        return p;
    }
}
