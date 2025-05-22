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

@WebServlet("/watchlist")
public class WatchlistController extends HttpServlet {

    @Override
    public void init() throws ServletException {
        // Initialize if needed
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("[DEBUG] WatchlistController: Processing POST request");
        
        // Debug all request parameters
        System.out.println("[DEBUG] All request parameters:");
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println("[DEBUG] Parameter: " + paramName + " = " + paramValue);
        }
        
        // Debug request headers for context
        System.out.println("[DEBUG] Request headers:");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println("[DEBUG] Header: " + headerName + " = " + headerValue);
        }
        
        try {
            Connection conn = DbConfig.getDbConnection();
            
            // Debug: Show user_watchlist table structure
            try {
                try (PreparedStatement stmt = conn.prepareStatement("SHOW COLUMNS FROM user_watchlist")) {
                    ResultSet rs = stmt.executeQuery();
                    System.out.println("[DEBUG] user_watchlist table structure:");
                    while (rs.next()) {
                        String field = rs.getString("Field");
                        String type = rs.getString("Type");
                        String key = rs.getString("Key");
                        System.out.println("[DEBUG] Column: " + field + ", Type: " + type + ", Key: " + key);
                    }
                } catch (Exception e) {
                    System.out.println("[DEBUG] Error showing table structure (table might not exist yet): " + e.getMessage());
                    
                    // Create the table if it doesn't exist
                    try (PreparedStatement createStmt = conn.prepareStatement(
                            "CREATE TABLE IF NOT EXISTS user_watchlist (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "User_ID INT NOT NULL, " +
                            "Movie_ID INT NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (User_ID) REFERENCES users(user_id), " +
                            "UNIQUE KEY user_movie (User_ID, Movie_ID)" +
                            ")")) {
                        createStmt.executeUpdate();
                        System.out.println("[DEBUG] Created user_watchlist table");
                    }
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error with table operations: " + e.getMessage());
            }

            // Get movie ID from request
            String movieIdStr = request.getParameter("movieId");
            System.out.println("[DEBUG] Movie ID from request: " + movieIdStr);
            
            if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Movie ID is required");
                return;
            }
            
            // Check if movie exists in the database
            try {
                String query = "SELECT id FROM movies WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, Integer.parseInt(movieIdStr));
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        System.out.println("[DEBUG] No movie found with ID: " + movieIdStr);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("Movie not found");
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Error checking movie existence: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error checking movie: " + e.getMessage());
                return;
            }
            
            // Get user ID from session
            Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
            System.out.println("[DEBUG] User ID from session: " + userId);
            
            // If userId is null, try to get it from the email
            if (userId == null) {
                String email = (String) SessionUtil.getAttribute(request, "user");
                System.out.println("[DEBUG] User email from session: " + email);
                
                if (email != null) {
                    String query = "SELECT user_id FROM users WHERE email = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, email);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            userId = rs.getInt("user_id");
                            SessionUtil.setAttribute(request, "userId", userId);
                            System.out.println("[DEBUG] Retrieved user_id: " + userId);
                        } else {
                            System.out.println("[DEBUG] No user found with email: " + email);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("User not found");
                            return;
                        }
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Please log in to add to watchlist");
                    return;
                }
            }
            
            // Handle the watchlist action
            String action = request.getParameter("action");
            System.out.println("[DEBUG] Action: " + action);

            if ("add".equals(action)) {
                int movieId = Integer.parseInt(movieIdStr);
                
                // First check if entry already exists
                String checkQuery = "SELECT COUNT(*) AS count FROM user_watchlist WHERE User_ID = ? AND Movie_ID = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, userId);
                    checkStmt.setInt(2, movieId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt("count") > 0) {
                        System.out.println("[DEBUG] Watchlist entry already exists");
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("Success");
                    } else {
                        // Insert new watchlist entry
                        String insertQuery = "INSERT INTO user_watchlist (User_ID, Movie_ID) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, movieId);
                            int result = insertStmt.executeUpdate();
                            System.out.println("[DEBUG] Insert result: " + result);
                            
                            if (result > 0) {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.getWriter().write("Success");
                            } else {
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                response.getWriter().write("Failed to add to watchlist");
                            }
                        }
                    }
                }
            } else if ("remove".equals(action)) {
                int movieId = Integer.parseInt(movieIdStr);
                
                String deleteQuery = "DELETE FROM user_watchlist WHERE User_ID = ? AND Movie_ID = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.setInt(2, movieId);
                    int result = deleteStmt.executeUpdate();
                    System.out.println("[DEBUG] Delete result: " + result);
                    
                    if (result > 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("Success");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("Failed to remove from watchlist");
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid action");
            }
            
            conn.close();
        } catch (Exception e) {
            System.err.println("[ERROR] Database error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("[DEBUG] WatchlistController: Processing GET request");

        // Get movie ID from request
        String movieIdStr = request.getParameter("movieId");
        if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Movie ID is required");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            
            // Get user ID from session
            Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
            System.out.println("[DEBUG] User ID from session: " + userId);
            
            // If userId is null, try to get it from the email
            if (userId == null) {
                String email = (String) SessionUtil.getAttribute(request, "user");
                System.out.println("[DEBUG] User email from session: " + email);
                
                if (email != null) {
                    Connection conn = DbConfig.getDbConnection();
                    String query = "SELECT user_id FROM users WHERE email = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, email);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            userId = rs.getInt("user_id");
                            SessionUtil.setAttribute(request, "userId", userId);
                            System.out.println("[DEBUG] Retrieved user_id: " + userId);
                        } else {
                            System.out.println("[DEBUG] No user found with email: " + email);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"isWatchlisted\": false}");
                            return;
                        }
                    }
                    conn.close();
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"isWatchlisted\": false}");
                    return;
                }
            }
            
            // Check watchlist status directly from the database
            boolean isWatchlisted = false;
            Connection conn = DbConfig.getDbConnection();
            
            // Check if the table exists
            try {
                String tableExistsQuery = "SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user_watchlist'";
                try (PreparedStatement tableStmt = conn.prepareStatement(tableExistsQuery)) {
                    ResultSet tableRs = tableStmt.executeQuery();
                    if (!tableRs.next()) {
                        // Table doesn't exist, create it
                        System.out.println("[DEBUG] Creating user_watchlist table");
                        try (PreparedStatement createStmt = conn.prepareStatement(
                                "CREATE TABLE IF NOT EXISTS user_watchlist (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "User_ID INT NOT NULL, " +
                                "Movie_ID INT NOT NULL, " +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                "FOREIGN KEY (User_ID) REFERENCES users(user_id), " +
                                "UNIQUE KEY user_movie (User_ID, Movie_ID)" +
                                ")")) {
                            createStmt.executeUpdate();
                            System.out.println("[DEBUG] Created user_watchlist table");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error checking/creating table: " + e.getMessage());
            }
            
            // Now query the watchlist status
            String query = "SELECT COUNT(*) AS count FROM user_watchlist WHERE User_ID = ? AND Movie_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, movieId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    isWatchlisted = rs.getInt("count") > 0;
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error checking watchlist status: " + e.getMessage());
                // If table doesn't exist yet, just return false
            }
            
            conn.close();
            
            response.setContentType("application/json");
            response.getWriter().write("{\"isWatchlisted\": " + isWatchlisted + "}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid movie ID format");
        } catch (Exception e) {
            System.err.println("[ERROR] Database error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }
} 