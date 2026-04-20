package org.dao;

import org.model.Warna;
import org.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarnaDAO {

    public List<Warna> findAll() {
        List<Warna> list = new ArrayList<>();
        String sql = "SELECT * FROM warna ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Warna w = new Warna();
                w.setId(rs.getInt("id"));
                w.setNamaWarna(rs.getString("nama_warna"));
                w.setCreatedAt(rs.getTimestamp("created_at"));
                w.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(w);
            }
        } catch (SQLException e) {
            System.err.println("Error findAll Warna: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Warna warna) {
        String sql = "INSERT INTO warna (nama_warna, created_at, updated_at) VALUES (?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, warna.getNamaWarna());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Warna: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Warna warna) {
        String sql = "UPDATE warna SET nama_warna = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, warna.getNamaWarna());
            pstmt.setInt(2, warna.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Warna: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM warna WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete Warna: " + e.getMessage());
            return false;
        }
    }
}
