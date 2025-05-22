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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@WebServlet("/favorites")
public class FavoritesController extends HttpServlet {
    private FavoriteService favoriteService;

    @Override
    public void init() throws ServletException {
        favoriteService = new FavoriteService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get user ID from session
        Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
        
        // If userId is null, try to get it from the email
        if (userId == null) {
            String email = (String) SessionUtil.getAttribute(request, "user");
            if (email != null) {
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
            } else {
                // If no user is logged in, redirect to login page
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }

        // Get user's favorite movies
        List<Map<String, Object>> favoriteMovies = favoriteService.getUserFavorites(userId);
        request.setAttribute("favoriteMovies", favoriteMovies);

        // Forward to the favorites JSP page
        request.getRequestDispatcher("/WEB-INF/pages/userprofile-favorites.jsp").forward(request, response);
    }
} 