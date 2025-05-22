package com.reelthoughts.controller;

import com.reelthoughts.config.DbConfig;
import com.reelthoughts.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/rating")
public class RatingController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        String userEmail = (String) SessionUtil.getAttribute(request, "user");
        System.out.println("[DEBUG] User email from session: " + userEmail);
        
        if (userEmail == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"User not logged in\"}");
            return;
        }

        String movieIdStr = request.getParameter("movieId");
        if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Movie ID is required\"}");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            System.out.println("[DEBUG] Parsed movie ID: " + movieId);
            
            try (Connection conn = DbConfig.getDbConnection()) {
                // Check if user_rating table exists and create it if it doesn't
                try {
                    String tableExistsQuery = "SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user_rating'";
                    try (PreparedStatement tableStmt = conn.prepareStatement(tableExistsQuery)) {
                        ResultSet tableRs = tableStmt.executeQuery();
                        if (!tableRs.next()) {
                            // Table doesn't exist, create it
                            System.out.println("[DEBUG] Creating user_rating table");
                            try (PreparedStatement createStmt = conn.prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS user_rating (" +
                                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                    "User_ID INT NOT NULL, " +
                                    "Movie_ID INT NOT NULL, " +
                                    "RatingValue INT NOT NULL, " +
                                    "RatingDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                    "FOREIGN KEY (User_ID) REFERENCES users(user_id), " +
                                    "UNIQUE KEY user_movie (User_ID, Movie_ID)" +
                                    ")")) {
                                createStmt.executeUpdate();
                                System.out.println("[DEBUG] Created user_rating table");
                            }
                        } else {
                            System.out.println("[DEBUG] user_rating table exists");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[DEBUG] Error checking/creating table: " + e.getMessage());
                    e.printStackTrace();
                }

                // Get user ID
                String userQuery = "SELECT user_id FROM users WHERE email = ?";
                int userId;
                try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                    userStmt.setString(1, userEmail);
                    try (ResultSet userRs = userStmt.executeQuery()) {
                        if (!userRs.next()) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().write("{\"error\": \"User not found\"}");
                            return;
                        }
                        userId = userRs.getInt("user_id");
                    }
                }

                // Get rating
                String ratingQuery = "SELECT RatingValue FROM user_rating WHERE User_ID = ? AND Movie_ID = ?";
                try (PreparedStatement ratingStmt = conn.prepareStatement(ratingQuery)) {
                    ratingStmt.setInt(1, userId);
                    ratingStmt.setInt(2, movieId);
                    try (ResultSet ratingRs = ratingStmt.executeQuery()) {
                        if (ratingRs.next()) {
                            int rating = ratingRs.getInt("RatingValue");
                            response.getWriter().write("{\"rating\": " + rating + "}");
                        } else {
                            response.getWriter().write("{\"rating\": null}");
                        }
                    }
                }
            } catch (SQLException | ClassNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Database error\"}");
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            System.out.println("[DEBUG] Invalid movie ID format: " + movieIdStr);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid movie ID format\"}");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        String userEmail = (String) SessionUtil.getAttribute(request, "user");
        System.out.println("[DEBUG] User email from session: " + userEmail);
        
        if (userEmail == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"User not logged in\"}");
            return;
        }

        String movieIdStr = request.getParameter("movieId");
        String ratingStr = request.getParameter("rating");
        System.out.println("[DEBUG] Received movieId: " + movieIdStr + ", rating: " + ratingStr);

        if (movieIdStr == null || ratingStr == null || 
            movieIdStr.trim().isEmpty() || ratingStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Movie ID and rating are required\"}");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            int rating = Integer.parseInt(ratingStr);
            System.out.println("[DEBUG] Parsed movieId: " + movieId + ", rating: " + rating);

            if (rating < 1 || rating > 10) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Rating must be between 1 and 10\"}");
                return;
            }

            try (Connection conn = DbConfig.getDbConnection()) {
                System.out.println("[DEBUG] Got database connection");
                
                // Check if user_rating table exists and create it if it doesn't
                try {
                    String tableExistsQuery = "SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user_rating'";
                    try (PreparedStatement tableStmt = conn.prepareStatement(tableExistsQuery)) {
                        ResultSet tableRs = tableStmt.executeQuery();
                        if (!tableRs.next()) {
                            // Table doesn't exist, create it
                            System.out.println("[DEBUG] Creating user_rating table");
                            try (PreparedStatement createStmt = conn.prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS user_rating (" +
                                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                    "User_ID INT NOT NULL, " +
                                    "Movie_ID INT NOT NULL, " +
                                    "RatingValue INT NOT NULL, " +
                                    "RatingDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                    "FOREIGN KEY (User_ID) REFERENCES users(user_id), " +
                                    "UNIQUE KEY user_movie (User_ID, Movie_ID)" +
                                    ")")) {
                                createStmt.executeUpdate();
                                System.out.println("[DEBUG] Created user_rating table");
                            }
                        } else {
                            System.out.println("[DEBUG] user_rating table exists");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[DEBUG] Error checking/creating table: " + e.getMessage());
                    e.printStackTrace();
                }

                // Get user ID
                String userQuery = "SELECT user_id FROM users WHERE email = ?";
                int userId;
                try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                    userStmt.setString(1, userEmail);
                    try (ResultSet userRs = userStmt.executeQuery()) {
                        if (!userRs.next()) {
                            System.out.println("[DEBUG] User not found in database");
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().write("{\"error\": \"User not found\"}");
                            return;
                        }
                        userId = userRs.getInt("user_id");
                        System.out.println("[DEBUG] Found user ID: " + userId);
                    }
                }

                // Check if rating exists
                String checkQuery = "SELECT COUNT(*) as count FROM user_rating WHERE User_ID = ? AND Movie_ID = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, userId);
                    checkStmt.setInt(2, movieId);
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if (rs.next() && rs.getInt("count") > 0) {
                        System.out.println("[DEBUG] Updating existing rating");
                        // Update existing rating
                        String updateQuery = "UPDATE user_rating SET RatingValue = ?, RatingDate = CURRENT_TIMESTAMP WHERE User_ID = ? AND Movie_ID = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, rating);
                            updateStmt.setInt(2, userId);
                            updateStmt.setInt(3, movieId);
                            int result = updateStmt.executeUpdate();
                            System.out.println("[DEBUG] Update result: " + result);
                            
                            if (result > 0) {
                                response.getWriter().write("{\"message\": \"Rating updated\", \"rating\": " + rating + "}");
                            } else {
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                response.getWriter().write("{\"error\": \"Failed to update rating\"}");
                            }
                        }
                    } else {
                        System.out.println("[DEBUG] Inserting new rating");
                        // Insert new rating
                        String insertQuery = "INSERT INTO user_rating (User_ID, Movie_ID, RatingValue, RatingDate) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, movieId);
                            insertStmt.setInt(3, rating);
                            int result = insertStmt.executeUpdate();
                            System.out.println("[DEBUG] Insert result: " + result);
                            
                            if (result > 0) {
                                response.getWriter().write("{\"message\": \"Rating added\", \"rating\": " + rating + "}");
                            } else {
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                response.getWriter().write("{\"error\": \"Failed to add rating\"}");
                            }
                        }
                    }
                }
            } catch (SQLException | ClassNotFoundException e) {
                System.out.println("[DEBUG] Database error: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Database error\"}");
            }
        } catch (NumberFormatException e) {
            System.out.println("[DEBUG] Invalid number format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid number format\"}");
            return;
        }
    }
} 