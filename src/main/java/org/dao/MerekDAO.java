package org.dao;

import org.model.Merek;
import org.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MerekDAO {

    public List<Merek> findAll() {
        List<Merek> list = new ArrayList<>();
        String sql = "SELECT * FROM merek ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Merek m = new Merek();
                m.setId(rs.getInt("id"));
                m.setNamaMerek(rs.getString("nama_merek"));
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error findAll Merek: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Merek merek) {
        String sql = "INSERT INTO merek (nama_merek, created_at, updated_at) VALUES (?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, merek.getNamaMerek());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Merek: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Merek merek) {
        String sql = "UPDATE merek SET nama_merek = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, merek.getNamaMerek());
            pstmt.setInt(2, merek.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Merek: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM merek WHERE id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete Merek: " + e.getMessage());
            return false;
        }
    }
}
