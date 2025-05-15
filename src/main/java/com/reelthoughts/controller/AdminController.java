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
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DEBUG] MySQL JDBC Driver loaded successfully");
            
            // Database connection parameters
            String url = "jdbc:mysql://localhost:3306/reelthoughts";
            String username = "root";
            String password = "";
            
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                System.out.println("[DEBUG] Database connection established");
                
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
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM `user-favorite`")) {
                    if (rs.next()) {
                        request.setAttribute("totalFavorites", rs.getInt("count"));
                    }
                }
                
                // Get total watchlists count
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM watchlists")) {
                    if (rs.next()) {
                        request.setAttribute("totalWatchlists", rs.getInt("count"));
                    }
                }
                
                // Get all users
                List<Map<String, Object>> users = new ArrayList<>();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT id, first_name, last_name, email, role, created_at as joinDate FROM users")) {
                    while (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", rs.getInt("id"));
                        user.put("firstName", rs.getString("first_name"));
                        user.put("lastName", rs.getString("last_name"));
                        user.put("email", rs.getString("email"));
                        user.put("role", rs.getString("role"));
                        user.put("joinDate", rs.getDate("joinDate"));
                        users.add(user);
                    }
                }
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
                         "LEFT JOIN `user-favorite` f ON m.id = f.Movie_ID " +
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
                         "SELECT m.title, COUNT(w.id) as watchlistCount " +
                         "FROM movies m " +
                         "LEFT JOIN watchlists w ON m.id = w.movie_id " +
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
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("[ERROR] Exception in AdminController: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard data: " + e.getMessage());
        }
        
        System.out.println("[DEBUG] Forwarding to admin dashboard JSP");
        // Forward to admin dashboard
        request.getRequestDispatcher("/WEB-INF/pages/admindashboard.jsp").forward(request, response);
    }
}