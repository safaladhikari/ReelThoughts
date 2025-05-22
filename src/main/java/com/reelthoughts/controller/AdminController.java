package com.reelthoughts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.reelthoughts.util.SessionUtil;
import com.reelthoughts.config.DbConfig;

@WebServlet("/admin/dashboard")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("[DEBUG] AdminController: doGet method called");
        
        // Check if the user is logged in and is an admin
        String userRole = (String) SessionUtil.getAttribute(request, "userRole");
        System.out.println("[DEBUG] User Role: " + userRole);
        
        if (userRole == null) {
            // Redirect to login if not logged in
            System.out.println("[DEBUG] User not logged in, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } 
        
        if (!"admin".equals(userRole)) {
            // Redirect non-admin users to home
            System.out.println("[DEBUG] User is not admin, redirecting to home");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        try {
            // Get database connection using DbConfig
            System.out.println("[DEBUG] Getting database connection from DbConfig");
            Connection conn = DbConfig.getDbConnection();
            System.out.println("[DEBUG] Database connection established successfully");
            
            try {
                // Get total users count
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users")) {
                    if (rs.next()) {
                        request.setAttribute("totalUsers", rs.getInt("count"));
                    }
                }
                
                // Get total movies count
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM movies")) {
                    if (rs.next()) {
                        int movieCount = rs.getInt("count");
                        System.out.println("[DEBUG] Total movies in database: " + movieCount);
                        request.setAttribute("totalMovies", movieCount);
                    }
                }
                
                // Get total favorites count
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM user_favorite")) {
                    if (rs.next()) {
                        request.setAttribute("totalFavorites", rs.getInt("count"));
                    }
                }
                
                // Get total watchlists count
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM user_watchlist")) {
                    if (rs.next()) {
                        request.setAttribute("totalWatchlists", rs.getInt("count"));
                    }
                }
                
                // Get all users
                List<Map<String, Object>> users = new ArrayList<>();
                try (Statement stmt = conn.createStatement()) {
                    String userQuery = "SELECT user_id, first_name, last_name, email, created_at FROM users ORDER BY user_id DESC";
                    System.out.println("[DEBUG] Executing users query: " + userQuery);
                    try (ResultSet rs = stmt.executeQuery(userQuery)) {
                        System.out.println("[DEBUG] Query executed successfully");
                        int userCount = 0;
                        while (rs.next()) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("id", rs.getInt("user_id"));
                            user.put("firstName", rs.getString("first_name"));
                            user.put("lastName", rs.getString("last_name"));
                            user.put("email", rs.getString("email"));
                            user.put("joinDate", rs.getTimestamp("created_at"));
                            users.add(user);
                            userCount++;
                            System.out.println("[DEBUG] Found user: " + user.get("email") + " (ID: " + user.get("id") + ")");
                        }
                        System.out.println("[DEBUG] Total users fetched: " + userCount);
                    } catch (SQLException e) {
                        System.err.println("[ERROR] Error executing users query: " + e.getMessage());
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    System.err.println("[ERROR] Error creating statement: " + e.getMessage());
                    e.printStackTrace();
                }
                System.out.println("[DEBUG] Setting users attribute with " + users.size() + " users");
                request.setAttribute("users", users);
                
                // Get all movies
                List<Map<String, Object>> movies = new ArrayList<>();
                try (Statement stmt = conn.createStatement()) {
                    System.out.println("[DEBUG] Database connection successful");
                    String query = "SELECT id, title, genre, year, director, imageLink FROM movies";
                    System.out.println("[DEBUG] Executing query: " + query);
                    try (ResultSet rs = stmt.executeQuery(query)) {
                        System.out.println("[DEBUG] Query executed successfully");
                        while (rs.next()) {
                            Map<String, Object> movie = new HashMap<>();
                            movie.put("id", rs.getInt("id"));
                            movie.put("title", rs.getString("title"));
                            movie.put("genre", rs.getString("genre"));
                            movie.put("year", rs.getInt("year"));
                            movie.put("director", rs.getString("director"));
                            movie.put("imageLink", rs.getString("imageLink"));
                            movies.add(movie);
                            System.out.println("[DEBUG] Found movie: " + movie.get("title") + " (ID: " + movie.get("id") + ")");
                        }
                        System.out.println("[DEBUG] Total movies fetched: " + movies.size());
                    }
                }
                System.out.println("[DEBUG] Setting movies attribute with " + movies.size() + " movies");
                request.setAttribute("movies", movies);
                
                // Get top favorited movies
                List<Map<String, Object>> topFavoritedMovies = new ArrayList<>();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(
                         "SELECT m.title, COUNT(f.Movie_ID) as favoriteCount " +
                         "FROM movies m " +
                         "LEFT JOIN user_favorite f ON m.id = f.Movie_ID " +
                         "GROUP BY m.id, m.title " +
                         "ORDER BY favoriteCount DESC " +
                         "LIMIT 5")) {
                    while (rs.next()) {
                        Map<String, Object> movie = new HashMap<>();
                        movie.put("title", rs.getString("title"));
                        movie.put("favoriteCount", rs.getInt("favoriteCount"));
                        topFavoritedMovies.add(movie);
                    }
                }
                request.setAttribute("topFavoritedMovies", topFavoritedMovies);
                
                // Get top watchlisted movies
                List<Map<String, Object>> topWatchlistedMovies = new ArrayList<>();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(
                         "SELECT m.title, COUNT(w.Movie_ID) as watchlistCount " +
                         "FROM movies m " +
                         "LEFT JOIN user_watchlist w ON m.id = w.Movie_ID " +
                         "GROUP BY m.id, m.title " +
                         "ORDER BY watchlistCount DESC " +
                         "LIMIT 5")) {
                    while (rs.next()) {
                        Map<String, Object> movie = new HashMap<>();
                        movie.put("title", rs.getString("title"));
                        movie.put("watchlistCount", rs.getInt("watchlistCount"));
                        topWatchlistedMovies.add(movie);
                    }
                }
                request.setAttribute("topWatchlistedMovies", topWatchlistedMovies);
            } finally {
                // Close the connection
                if (conn != null) {
                    try {
                        conn.close();
                        System.out.println("[DEBUG] Database connection closed");
                    } catch (SQLException e) {
                        System.err.println("[ERROR] Error closing connection: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("[ERROR] Database error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Unexpected error: " + e.getMessage());
        }
        
        System.out.println("[DEBUG] Forwarding to admin dashboard JSP");
        // Forward to admin dashboard
        request.getRequestDispatcher("/WEB-INF/pages/admindashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if the user is logged in and is an admin
        String userRole = (String) SessionUtil.getAttribute(request, "userRole");
        if (userRole == null || !"admin".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Connection conn = DbConfig.getDbConnection();
            try {
                // Handle user update
                String updateUserId = request.getParameter("updateUserId");
                if (updateUserId != null) {
                    String firstName = request.getParameter("firstName");
                    String lastName = request.getParameter("lastName");
                    String email = request.getParameter("email");

                    String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE user_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, firstName);
                        pstmt.setString(2, lastName);
                        pstmt.setString(3, email);
                        pstmt.setInt(4, Integer.parseInt(updateUserId));
                        pstmt.executeUpdate();
                    }
                }

                // Handle movie update
                String updateMovieId = request.getParameter("updateMovieId");
                if (updateMovieId != null) {
                    String title = request.getParameter("title");
                    String genre = request.getParameter("genre");
                    int year = Integer.parseInt(request.getParameter("year"));
                    String director = request.getParameter("director");
                    String imageLink = request.getParameter("imageLink");

                    String sql = "UPDATE movies SET title = ?, genre = ?, year = ?, director = ?, imageLink = ? WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, title);
                        pstmt.setString(2, genre);
                        pstmt.setInt(3, year);
                        pstmt.setString(4, director);
                        pstmt.setString(5, imageLink);
                        pstmt.setInt(6, Integer.parseInt(updateMovieId));
                        pstmt.executeUpdate();
                    }
                }
            } finally {
                if (conn != null) conn.close();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error in doPost: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
        }

        // Redirect back to dashboard
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if the user is logged in and is an admin
        String userRole = (String) SessionUtil.getAttribute(request, "userRole");
        if (userRole == null || !"admin".equals(userRole)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }

        try {
            Connection conn = DbConfig.getDbConnection();
            try {
                // Handle user deletion
                String deleteUserId = request.getParameter("deleteUserId");
                if (deleteUserId != null) {
                    // First delete related records
                    String deleteFavorites = "DELETE FROM user_favorite WHERE User_ID = ?";
                    String deleteWatchlists = "DELETE FROM user_watchlist WHERE User_ID = ?";
                    String deleteReviews = "DELETE FROM reviews WHERE User_ID = ?";
                    String deleteUser = "DELETE FROM users WHERE user_id = ?";

                    try (PreparedStatement pstmt = conn.prepareStatement(deleteFavorites)) {
                        pstmt.setInt(1, Integer.parseInt(deleteUserId));
                        pstmt.executeUpdate();
                    }
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteWatchlists)) {
                        pstmt.setInt(1, Integer.parseInt(deleteUserId));
                        pstmt.executeUpdate();
                    }
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteReviews)) {
                        pstmt.setInt(1, Integer.parseInt(deleteUserId));
                        pstmt.executeUpdate();
                    }
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteUser)) {
                        pstmt.setInt(1, Integer.parseInt(deleteUserId));
                        pstmt.executeUpdate();
                    }
                }

                // Handle movie deletion
                String deleteMovieId = request.getParameter("deleteMovieId");
                if (deleteMovieId != null) {
                    // First delete related records
                    String deleteFavorites = "DELETE FROM user_favorite WHERE Movie_ID = ?";
                    String deleteWatchlists = "DELETE FROM user_watchlist WHERE Movie_ID = ?";
                    String deleteReviews = "DELETE FROM reviews WHERE Movie_ID = ?";
                    String deleteMovie = "DELETE FROM movies WHERE id = ?";

                    try (PreparedStatement pstmt = conn.prepareStatement(deleteFavorites)) {
                        pstmt.setInt(1, Integer.parseInt(deleteMovieId));
                        pstmt.executeUpdate();
                    }
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteWatchlists)) {
                        pstmt.setInt(1, Integer.parseInt(deleteMovieId));
                        pstmt.executeUpdate();
                    }
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteReviews)) {
                        pstmt.setInt(1, Integer.parseInt(deleteMovieId));
                        pstmt.executeUpdate();
                    }
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteMovie)) {
                        pstmt.setInt(1, Integer.parseInt(deleteMovieId));
                        pstmt.executeUpdate();
                    }
                }
            } finally {
                if (conn != null) conn.close();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error in doDelete: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing request: " + e.getMessage());
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Success");
    }
}