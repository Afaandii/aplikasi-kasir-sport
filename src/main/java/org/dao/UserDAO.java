package org.dao;

import org.model.User;
import org.utils.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
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
