package com.reelthoughts.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.reelthoughts.config.DbConfig;
import com.reelthoughts.model.UserModel;

public class RegisterService {
    private Connection dbConn;

    public RegisterService() {
        try {
            this.dbConn = DbConfig.getDbConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Database connection error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public Boolean registerUser(UserModel user) {
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return null;
        }

        String query = "INSERT INTO users (first_name, last_name, email, password, dob, gender) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setDate(5, user.getDob());
            stmt.setString(6, user.getGender());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            // Handle duplicate email case specifically
            if (e.getMessage().contains("Duplicate entry")) {
                return false;
            }
            return null;
        }
    }
}