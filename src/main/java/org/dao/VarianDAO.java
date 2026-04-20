package org.dao;

import org.model.Varian;
import org.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VarianDAO {

    public List<Varian> findByProdukId(int produkId) {
        List<Varian> list = new ArrayList<>();
        String sql = "SELECT v.*, u.nama_ukuran, w.nama_warna " +
                     "FROM varian v " +
                     "LEFT JOIN ukuran u ON v.ukuran_id = u.id " +
                     "LEFT JOIN warna w ON v.warna_id = w.id " +
                     "WHERE v.produk_id = ? " +
                     "ORDER BY v.id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, produkId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Varian v = mapResultSetToVarian(rs);
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error findByProdukId: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Varian v) {
        String sql = "INSERT INTO varian (produk_id, ukuran_id, warna_id, stok_produk, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, v.getProdukId());
            pstmt.setInt(2, v.getUkuranId());
            pstmt.setInt(3, v.getWarnaId());
            pstmt.setInt(4, v.getStokProduk());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Varian: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Varian v) {
        String sql = "UPDATE varian SET ukuran_id = ?, warna_id = ?, stok_produk = ?, updated_at = NOW() " +
                     "WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, v.getUkuranId());
            pstmt.setInt(2, v.getWarnaId());
            pstmt.setInt(3, v.getStokProduk());
            pstmt.setInt(4, v.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Varian: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM varian WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete Varian: " + e.getMessage());
            return false;
        }
    }

    private Varian mapResultSetToVarian(ResultSet rs) throws SQLException {
        Varian v = new Varian();
        v.setId(rs.getInt("id"));
        v.setProdukId(rs.getInt("produk_id"));
        v.setUkuranId(rs.getInt("ukuran_id"));
        v.setWarnaId(rs.getInt("warna_id"));
        v.setStokProduk(rs.getInt("stok_produk"));
        v.setCreatedAt(rs.getTimestamp("created_at"));
        v.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Joined fields
        v.setUkuranNama(rs.getString("nama_ukuran"));
        v.setWarnaNama(rs.getString("nama_warna"));
        
        return v;
    }
}
