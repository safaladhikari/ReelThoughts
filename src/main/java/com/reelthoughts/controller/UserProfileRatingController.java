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

@WebServlet("/userprofilerating")
public class UserProfileRatingController extends HttpServlet {
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
            // Simple query to get user's ratings with movie details
            String query = "SELECT m.id, m.title, m.imageLink, ur.RatingValue, ur.RatingDate " +
                          "FROM user_rating ur " +
                          "JOIN movies m ON ur.Movie_ID = m.id " +
                          "WHERE ur.User_ID = ? " +
                          "ORDER BY ur.RatingDate DESC";

            List<Map<String, Object>> ratings = new ArrayList<>();
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> rating = new HashMap<>();
                        rating.put("movieId", rs.getInt("id"));
                        rating.put("title", rs.getString("title"));
                        rating.put("imageLink", rs.getString("imageLink"));
                        rating.put("rating", rs.getInt("RatingValue"));
                        rating.put("date", rs.getTimestamp("RatingDate"));
                        ratings.add(rating);
                    }
                }
            }

            request.setAttribute("ratings", ratings);
            request.getRequestDispatcher("/WEB-INF/pages/userprofile-ratings.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading ratings data");
            request.getRequestDispatcher("/WEB-INF/pages/userprofile-ratings.jsp").forward(request, response);
        }
    }
} 