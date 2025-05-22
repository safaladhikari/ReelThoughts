package com.reelthoughts.controller;

import com.reelthoughts.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/popular")
public class PopularController extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public PopularController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        try {
            System.out.println("[DEBUG] Getting database connection");
            conn = DbConfig.getDbConnection();
            System.out.println("[DEBUG] Database connection established");
            
            // Check if user_favorite table exists and create it if it doesn't
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
                    }
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Error checking/creating table: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Query to get top 8 movies based on number of favorites
            String query = "SELECT m.*, COUNT(uf.Movie_ID) as favorite_count " +
                          "FROM movies m " +
                          "LEFT JOIN user_favorite uf ON m.id = uf.Movie_ID " +
                          "GROUP BY m.id, m.title, m.director, m.year, m.genre, m.imageLink " +
                          "ORDER BY favorite_count DESC " +
                          "LIMIT 8";
            
            System.out.println("[DEBUG] Executing query: " + query);
            List<Map<String, Object>> movies = new ArrayList<>();
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                System.out.println("[DEBUG] Query executed successfully");
                
                while (rs.next()) {
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getInt("id"));
                    movie.put("title", rs.getString("title"));
                    movie.put("director", rs.getString("director"));
                    movie.put("year", rs.getString("year"));
                    movie.put("genre", rs.getString("genre"));
                    movie.put("imageLink", rs.getString("imageLink"));
                    movie.put("favoriteCount", rs.getInt("favorite_count"));
                    movies.add(movie);
                    System.out.println("[DEBUG] Added movie: " + movie.get("title") + " with " + movie.get("favoriteCount") + " favorites");
                }
            }
            
            System.out.println("[DEBUG] Found " + movies.size() + " movies");
            request.setAttribute("movies", movies);
            request.getRequestDispatcher("/WEB-INF/pages/popular.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("[ERROR] Error in PopularController: " + e.getMessage());
            e.printStackTrace();
            if (e instanceof java.sql.SQLException) {
                java.sql.SQLException sqlEx = (java.sql.SQLException) e;
                System.err.println("[ERROR] SQL State: " + sqlEx.getSQLState());
                System.err.println("[ERROR] Error Code: " + sqlEx.getErrorCode());
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading popular movies: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("[DEBUG] Database connection closed");
                } catch (Exception e) {
                    System.err.println("[ERROR] Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
