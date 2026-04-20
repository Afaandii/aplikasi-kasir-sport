package org.dao;

import org.model.Ukuran;
import org.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UkuranDAO {

    public List<Ukuran> findAll() {
        List<Ukuran> list = new ArrayList<>();
        String sql = "SELECT * FROM ukuran ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Ukuran u = new Ukuran();
                u.setId(rs.getInt("id"));
                u.setNamaUkuran(rs.getString("nama_ukuran"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                u.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error findAll Ukuran: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Ukuran ukuran) {
        String sql = "INSERT INTO ukuran (nama_ukuran, created_at, updated_at) VALUES (?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ukuran.getNamaUkuran());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Ukuran: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Ukuran ukuran) {
        String sql = "UPDATE ukuran SET nama_ukuran = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ukuran.getNamaUkuran());
            pstmt.setInt(2, ukuran.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Ukuran: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM ukuran WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete Ukuran: " + e.getMessage());
            return false;
        }
    }
}
