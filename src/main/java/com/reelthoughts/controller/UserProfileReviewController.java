package com.reelthoughts.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@WebServlet("/userprofilereview")
public class UserProfileReviewController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection conn = DbConfig.getDbConnection()) {
            // Query to get user's reviews with movie details
            String query = "SELECT m.id, m.title, m.imageLink, r.review_text, r.created_at as review_date " +
                          "FROM reviews r " +
                          "JOIN movies m ON r.movie_id = m.id " +
                          "WHERE r.user_id = ? " +
                          "ORDER BY r.created_at DESC";

            List<Map<String, Object>> reviews = new ArrayList<>();
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> review = new HashMap<>();
                        review.put("movieId", rs.getInt("id"));
                        review.put("title", rs.getString("title"));
                        review.put("imageLink", rs.getString("imageLink"));
                        review.put("reviewText", rs.getString("review_text"));
                        review.put("reviewDate", rs.getTimestamp("review_date"));
                        reviews.add(review);
                    }
                }
            }

            request.setAttribute("reviews", reviews);
            request.getRequestDispatcher("/WEB-INF/pages/userprofile-review.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading reviews data");
            request.getRequestDispatcher("/WEB-INF/pages/userprofile-review.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.getWriter().write("{\"success\": false, \"message\": \"User not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");
        String movieId = request.getParameter("movieId");

        if (movieId == null || movieId.trim().isEmpty()) {
            response.getWriter().write("{\"success\": false, \"message\": \"Movie ID is required\"}");
            return;
        }

        try (Connection conn = DbConfig.getDbConnection()) {
            if ("update".equals(action)) {
                String reviewText = request.getParameter("reviewText");
                if (reviewText == null || reviewText.trim().isEmpty()) {
                    response.getWriter().write("{\"success\": false, \"message\": \"Review text is required\"}");
                    return;
                }

                String query = "UPDATE reviews SET review_text = ? WHERE user_id = ? AND movie_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, reviewText);
                    pstmt.setInt(2, userId);
                    pstmt.setString(3, movieId);
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        response.getWriter().write("{\"success\": true}");
                    } else {
                        response.getWriter().write("{\"success\": false, \"message\": \"Review not found or no changes made\"}");
                    }
                }
            } else if ("delete".equals(action)) {
                String query = "DELETE FROM reviews WHERE user_id = ? AND movie_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, movieId);
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        response.getWriter().write("{\"success\": true}");
                    } else {
                        response.getWriter().write("{\"success\": false, \"message\": \"Review not found\"}");
                    }
                }
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid action\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"message\": \"Database error occurred\"}");
        }
    }
} 