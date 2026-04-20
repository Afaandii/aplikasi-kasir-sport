package org.dao;

import org.model.User;
import org.utils.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    public java.util.List<User> findAllCashiers() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM user WHERE role_id = 2 ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setRoleId(rs.getInt("role_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error findAllCashiers: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(User user) {
        String sql = "INSERT INTO user (role_id, username, email, password, created_at, updated_at) VALUES (2, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert User: " + e.getMessage());
            return false;
        }
    }

    public boolean update(User user, boolean updatePassword) {
        String sql;
        if (updatePassword) {
            sql = "UPDATE user SET username = ?, email = ?, password = ?, updated_at = NOW() WHERE id = ?";
        } else {
            sql = "UPDATE user SET username = ?, email = ?, updated_at = NOW() WHERE id = ?";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            
            if (updatePassword) {
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                pstmt.setString(3, hashedPassword);
                pstmt.setInt(4, user.getId());
            } else {
                pstmt.setInt(3, user.getId());
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update User: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete User: " + e.getMessage());
            return false;
        }
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ?";
        
        try {
            Connection conn = Database.getConnection();
            if (conn == null) {
                System.err.println("Koneksi database tidak tersedia.");
                return null;
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        
                        boolean isAuthenticated = false;
                        try {
                            isAuthenticated = BCrypt.checkpw(password, hashedPassword);
                        } catch (IllegalArgumentException e) {
                            isAuthenticated = password.equals(hashedPassword);
                        }

                        if (isAuthenticated) {
                            User user = new User();
                            user.setId(rs.getInt("id"));
                            user.setUsername(rs.getString("username"));
                            user.setEmail(rs.getString("email"));
                            user.setRoleId(rs.getInt("role_id"));
                            return user;
                        }
                    }
                } // close try-with-resources (ResultSet)
            } // close try-with-resources (PreparedStatement)
        } catch (SQLException e) {
            System.err.println("Gagal autentikasi: " + e.getMessage());
        }
        return null;
    }
}
