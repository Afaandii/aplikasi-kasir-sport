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
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    
                    // Verify password using BCrypt
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setRoleId(rs.getInt("role_id"));
                        // Do not return password for security
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal autentikasi: " + e.getMessage());
        }
        return null;
    }
}
