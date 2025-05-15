package com.reelthoughts.service;

import com.reelthoughts.config.DbConfig;
import com.reelthoughts.model.UserModel;
import com.reelthoughts.util.ImageUtil;
import com.reelthoughts.util.PasswordUtil;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final ImageUtil imageUtil = new ImageUtil();

    public UserModel getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            handleException(e);
        }
        return null;
    }

    public boolean updateUserProfile(UserModel user, Part imagePart) {
        try {
            String imagePath = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                imagePath = imageUtil.saveImageAndGetPath(imagePart, "users");
                user.setProfileImagePath(imagePath);
            }
            return updateUserProfile(user);
        } catch (IOException e) {
            handleException(e);
            return false;
        }
    }

    private boolean updateUserProfile(UserModel user) {
        String sql = "UPDATE users SET first_name=?, last_name=?, dob=?, gender=?, profile_image_path=? WHERE email=?";
        
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setDate(3, new java.sql.Date(user.getDob().getTime()));
            stmt.setString(4, user.getGender());
            stmt.setString(5, user.getProfileImagePath());
            stmt.setString(6, user.getEmail());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            handleException(e);
            return false;
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        String encryptedPassword = PasswordUtil.hashPassword(email, newPassword);
        if (encryptedPassword == null) return false;
        
        String sql = "UPDATE users SET password=? WHERE email=?";
        
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, encryptedPassword);
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            handleException(e);
            return false;
        }
    }

    public boolean updateUserName(String email, String newName) {
        String sql = "UPDATE users SET first_name = ? WHERE email = ?";
        
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newName);
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            handleException(e);
            return false;
        }
    }

    private UserModel extractUserFromResultSet(ResultSet rs) throws SQLException {
        UserModel user = new UserModel();
        user.setUserId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setDob(rs.getDate("dob"));
        user.setGender(rs.getString("gender"));
        user.setProfileImagePath(rs.getString("profile_image_path"));
        return user;
    }

    private void handleException(Exception e) {
        System.err.println("Error in UserService: " + e.getMessage());
        if (e instanceof SQLException) {
            SQLException sqlEx = (SQLException) e;
            System.err.println("SQL State: " + sqlEx.getSQLState());
            System.err.println("Error Code: " + sqlEx.getErrorCode());
        }
        e.printStackTrace();
    }
}