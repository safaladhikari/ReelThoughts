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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "UserReviewController", urlPatterns = {"/userreview"})
public class UserReviewController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get user ID from session
        Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
        String email = (String) SessionUtil.getAttribute(request, "user");
        
        // If userId is null, try to get it from the email
        if (userId == null && email != null) {
            try (Connection conn = DbConfig.getDbConnection()) {
                String query = "SELECT user_id FROM users WHERE email = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                        SessionUtil.setAttribute(request, "userId", userId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }

        if (userId == null) {
            // If no user is logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DbConfig.getDbConnection()) {
            // Get user information
            String userQuery = "SELECT first_name, last_name, profile_image_path FROM users WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", rs.getString("first_name"));
                    user.put("lastName", rs.getString("last_name"));
                    user.put("profilePicture", rs.getString("profile_image_path"));
                    request.setAttribute("user", user);
                }
            }

            // Get user's reviewed movies with movie details
            String reviewsQuery = "SELECT m.*, r.review_text, r.created_at " +
                                "FROM reviews r " +
                                "INNER JOIN movies m ON r.movie_id = m.id " +
                                "WHERE r.user_id = ? " +
                                "ORDER BY r.created_at DESC";
            
            List<Map<String, Object>> reviewedMovies = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(reviewsQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getInt("id"));
                    movie.put("title", rs.getString("title"));
                    movie.put("genre", rs.getString("genre"));
                    movie.put("year", rs.getInt("year"));
                    movie.put("director", rs.getString("director"));
                    movie.put("imageLink", rs.getString("imageLink"));
                    movie.put("review", rs.getString("review_text"));
                    movie.put("reviewedAt", rs.getTimestamp("created_at"));
                    reviewedMovies.add(movie);
                }
            }
            request.setAttribute("reviewedMovies", reviewedMovies);

            // Forward to the reviews JSP page
            request.getRequestDispatcher("/WEB-INF/pages/userprofile-reviews.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
} 