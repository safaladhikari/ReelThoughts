package com.reelthoughts.controller;

import com.reelthoughts.config.DbConfig;
import com.reelthoughts.service.FavoriteService;
import com.reelthoughts.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/favorite")
public class FavoriteController extends HttpServlet {
    private FavoriteService favoriteService;

    @Override
    public void init() throws ServletException {
        favoriteService = new FavoriteService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("[DEBUG] FavoriteController: Processing POST request");
        
        // Debug all request parameters
        System.out.println("[DEBUG] All request parameters:");
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println("[DEBUG] Parameter: " + paramName + " = " + paramValue);
        }
        
        // Debug raw request body
        try {
            StringBuilder requestBodyBuilder = new StringBuilder();
            java.io.BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }
            System.out.println("[DEBUG] Raw request body: " + requestBodyBuilder.toString());
        } catch (Exception e) {
            System.out.println("[DEBUG] Could not read raw request body: " + e.getMessage());
        }
        
        // Debug request headers for more context
        System.out.println("[DEBUG] Request headers:");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println("[DEBUG] Header: " + headerName + " = " + headerValue);
        }
        
        // Debug: Show user_favorite table structure
        try {
            Connection conn = DbConfig.getDbConnection();
            try (PreparedStatement stmt = conn.prepareStatement("SHOW COLUMNS FROM user_favorite")) {
                ResultSet rs = stmt.executeQuery();
                System.out.println("[DEBUG] user_favorite table structure:");
                while (rs.next()) {
                    String field = rs.getString("Field");
                    String type = rs.getString("Type");
                    String key = rs.getString("Key");
                    System.out.println("[DEBUG] Column: " + field + ", Type: " + type + ", Key: " + key);
                }
            }
            
            // Get movie ID from request
            String movieIdStr = request.getParameter("movieId");
            System.out.println("[DEBUG] Movie ID from request: " + movieIdStr);
            
            // Debug: Check if movie exists in the database
            try {
                if (movieIdStr != null && !movieIdStr.trim().isEmpty()) {
                    String query = "SELECT id FROM movies WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, Integer.parseInt(movieIdStr));
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            System.out.println("[DEBUG] Movie found in database with ID: " + rs.getInt("id"));
                        } else {
                            System.out.println("[DEBUG] No movie found with ID: " + movieIdStr);
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().write("Movie not found");
                            return;
                        }
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Movie ID is required");
                    return;
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
                    response.getWriter().write("Please log in to add favorites");
                    return;
                }
            }
            
            // Handle the favorite action directly
            String action = request.getParameter("action");
            System.out.println("[DEBUG] Action: " + action);
            
            if ("add".equals(action)) {
                int movieId = Integer.parseInt(movieIdStr);
                
                // First check if entry already exists
                String checkQuery = "SELECT COUNT(*) AS count FROM user_favorite WHERE User_ID = ? AND Movie_ID = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, userId);
                    checkStmt.setInt(2, movieId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt("count") > 0) {
                        System.out.println("[DEBUG] Favorite already exists");
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("Success");
                    } else {
                        // Insert new favorite
                        String insertQuery = "INSERT INTO user_favorite (User_ID, Movie_ID) VALUES (?, ?)";
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
                                response.getWriter().write("Failed to add favorite");
                            }
                        }
                    }
                }
            } else if ("remove".equals(action)) {
                int movieId = Integer.parseInt(movieIdStr);
                
                String deleteQuery = "DELETE FROM user_favorite WHERE User_ID = ? AND Movie_ID = ?";
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
                        response.getWriter().write("Failed to remove favorite");
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
        
        System.out.println("[DEBUG] FavoriteController: Processing GET request for favorite status");

        // Get movie ID from request
        String movieIdStr = request.getParameter("movieId");
        System.out.println("[DEBUG] Checking favorite status for movie ID: " + movieIdStr);
        
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
                            response.setContentType("application/json");
                            response.getWriter().write("{\"isFavorite\": false}");
                            return;
                        }
                    }
                    conn.close();
                } else {
                    System.out.println("[DEBUG] No user email in session, returning not favorite");
                    response.setContentType("application/json");
                    response.getWriter().write("{\"isFavorite\": false}");
                    return;
                }
            }
            
            // Verify the user_favorite table exists, create if not
            Connection conn = DbConfig.getDbConnection();
            try {
                String tableExistsQuery = "SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user_favorite'";
                try (PreparedStatement tableStmt = conn.prepareStatement(tableExistsQuery)) {
                    ResultSet tableRs = tableStmt.executeQuery();
                    if (!tableRs.next()) {
                        // Table doesn't exist, create it
                        System.out.println("[DEBUG] Creating user_favorite table");
                        try (PreparedStatement createStmt = conn.prepareStatement(
                                "CREATE TABLE IF NOT EXISTS user_favorite (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "User_ID INT NOT NULL, " +
                                "Movie_ID INT NOT NULL, " +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                "FOREIGN KEY (User_ID) REFERENCES users(user_id), " +
                                "UNIQUE KEY user_movie (User_ID, Movie_ID)" +
                                ")")) {
                            createStmt.executeUpdate();
                            System.out.println("[DEBUG] Created user_favorite table");
                        }
                    } else {
                        System.out.println("[DEBUG] user_favorite table exists");
                    }
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error checking/creating table: " + e.getMessage());
            }
            
            // Display table columns for debugging
            try (PreparedStatement colStmt = conn.prepareStatement("SHOW COLUMNS FROM user_favorite")) {
                ResultSet colRs = colStmt.executeQuery();
                System.out.println("[DEBUG] user_favorite table columns:");
                while (colRs.next()) {
                    System.out.println("[DEBUG] Column: " + colRs.getString("Field") + ", Type: " + colRs.getString("Type"));
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error fetching columns: " + e.getMessage());
            }
            
            // Check favorite status
            boolean isFavorite = false;
            String query = "SELECT COUNT(*) AS count FROM user_favorite WHERE User_ID = ? AND Movie_ID = ?";
            
            System.out.println("[DEBUG] Running favorite check query: " + query);
            System.out.println("[DEBUG] Parameters: User_ID=" + userId + ", Movie_ID=" + movieId);
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, movieId);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    isFavorite = count > 0;
                    System.out.println("[DEBUG] Found " + count + " favorite entries for User_ID=" + userId + ", Movie_ID=" + movieId);
                } else {
                    System.out.println("[DEBUG] No results from favorite count query");
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error checking favorite status: " + e.getMessage());
                e.printStackTrace();
            }
            
            conn.close();
            
            System.out.println("[DEBUG] isFavorite result for movie " + movieId + ": " + isFavorite);
            response.setContentType("application/json");
            response.getWriter().write("{\"isFavorite\": " + isFavorite + "}");
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid movie ID format: " + movieIdStr);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid movie ID format");
        } catch (Exception e) {
            System.err.println("[ERROR] Database error in favorite check: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }
} 