package org.dao;

import org.model.Kategori;
import org.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    public List<Kategori> findAll() {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Kategori k = new Kategori();
                k.setIdKategori(rs.getInt("id"));
                k.setNamaKategori(rs.getString("nama_kategori"));
                k.setCreatedAt(rs.getTimestamp("created_at"));
                k.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(k);
            }
        } catch (SQLException e) {
            System.err.println("Error findAll Kategori: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Kategori kategori) {
        String sql = "INSERT INTO kategori (nama_kategori, created_at, updated_at) VALUES (?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kategori.getNamaKategori());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Kategori: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Kategori kategori) {
        String sql = "UPDATE kategori SET nama_kategori = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kategori.getNamaKategori());
            pstmt.setInt(2, kategori.getIdKategori());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Kategori: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM kategori WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete Kategori: " + e.getMessage());
            return false;
        }
    }
}
