package com.reelthoughts.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.reelthoughts.config.DbConfig;
import com.reelthoughts.model.UserModel;
import com.reelthoughts.util.PasswordUtil;

/**
 * Service class for handling login operations and role verification.
 */
public class LoginService {
    private static final String ADMIN_EMAIL_SUFFIX = "@adminreelthoughts.com";
    
    private Connection dbConn;
    private boolean isConnectionError = false;

    public LoginService() {
        try {
            dbConn = DbConfig.getDbConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            isConnectionError = true;
        }
    }

    /**
     * Authenticates user and determines if they're an admin
     * @param userModel User credentials
     * @return true if valid user, false if invalid, null if server error
     */
    public Boolean authenticateUser(UserModel userModel) {
        if (isConnectionError) {
            System.out.println("Database connection error!");
            return null;
        }

        String query = "SELECT email, password FROM users WHERE email = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, userModel.getEmail());
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                boolean isValid = validatePassword(result, userModel);
                if (isValid) {
                    // Additional verification that admin email matches database record
                    String dbEmail = result.getString("email");
                    if (dbEmail.endsWith(ADMIN_EMAIL_SUFFIX) && 
                        !userModel.getEmail().endsWith(ADMIN_EMAIL_SUFFIX)) {
                        return false; // Potential security issue
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return false;
    }

    /**
     * Validates password using secure comparison
     */
    private boolean validatePassword(ResultSet result, UserModel userModel) 
            throws SQLException {
        String dbEmail = result.getString("email");
        String dbPassword = result.getString("password");

        // Verify email matches exactly (case-sensitive depending on your requirements)
        if (!dbEmail.equals(userModel.getEmail())) {
            return false;
        }

        // Use secure password verification
        return PasswordUtil.checkPassword(
            userModel.getEmail(), 
            userModel.getPassword(), 
            dbPassword
        );
    }

    /**
     * Checks if the given email belongs to an admin
     */
    public boolean isAdminUser(String email) {
        return email != null && email.toLowerCase().endsWith(ADMIN_EMAIL_SUFFIX);
    }
}