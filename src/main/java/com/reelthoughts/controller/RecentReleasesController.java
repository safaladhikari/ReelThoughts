package com.reelthoughts.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.reelthoughts.config.DbConfig;
import com.reelthoughts.util.SessionUtil;

@WebServlet("/recentreleases")
public class RecentReleasesController extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public RecentReleasesController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Fetch recent movies from database
        List<Map<String, Object>> movies = new ArrayList<>();
        
        try {
            Connection conn = DbConfig.getDbConnection();
            
            try (Statement stmt = conn.createStatement()) {
                String query = "SELECT id, title, genre, year, director, imageLink FROM movies ORDER BY id DESC LIMIT 8";
                
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        Map<String, Object> movie = new HashMap<>();
                        int movieId = rs.getInt("id");
                        movie.put("id", movieId);
                        movie.put("title", rs.getString("title"));
                        movie.put("genre", rs.getString("genre"));
                        movie.put("year", rs.getInt("year"));
                        movie.put("director", rs.getString("director"));
                        movie.put("imageLink", rs.getString("imageLink"));
                        
                        // Get user's session
                        HttpSession session = request.getSession(false);
                        if (session != null) {
                            Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
                            
                            // If userId is null, try to get it from the email
                            if (userId == null) {
                                String email = (String) SessionUtil.getAttribute(request, "user");
                                if (email != null) {
                                    String userQuery = "SELECT user_id FROM users WHERE email = ?";
                                    try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                                        userStmt.setString(1, email);
                                        ResultSet userRs = userStmt.executeQuery();
                                        if (userRs.next()) {
                                            userId = userRs.getInt("user_id");
                                            SessionUtil.setAttribute(request, "userId", userId);
                                        }
                                    }
                                }
                            }

                            if (userId != null) {
                                // Check if movie is in user's favorites
                                boolean isFavorite = checkIfFavorite(conn, userId, movieId);
                                movie.put("isFavorite", isFavorite);
                                
                                // Check if movie is in user's watchlist
                                boolean isInWatchlist = checkIfInWatchlist(conn, userId, movieId);
                                movie.put("isInWatchlist", isInWatchlist);
                                
                                // Get user's rating for the movie
                                Integer userRating = getUserRating(conn, userId, movieId);
                                movie.put("userRating", userRating);
                            }
                        }
                        
                        movies.add(movie);
                        System.out.println("[DEBUG] Loaded recent movie with ID: " + movieId + ", Title: " + rs.getString("title"));
                    }
                }
            }
            
            // Close the connection
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error fetching recent movies: " + e.getMessage());
        }
        
        // Set the movies as a request attribute
        request.setAttribute("movies", movies);
        
        // Forward to the JSP page
        request.getRequestDispatcher("/WEB-INF/pages/recentreleases.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    private boolean checkIfFavorite(Connection conn, Integer userId, int movieId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_favorite WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean checkIfInWatchlist(Connection conn, Integer userId, int movieId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_watchlist WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Integer getUserRating(Connection conn, Integer userId, int movieId) throws SQLException {
        String sql = "SELECT RatingValue FROM user_rating WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RatingValue");
                }
            }
        }
        return null;
    }
}
