package com.reelthoughts.controller;

import com.reelthoughts.service.FavoriteService;
import com.reelthoughts.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
        
        // Check if user is logged in
        Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Please log in to add favorites");
            return;
        }

        // Get movie ID from request
        String movieIdStr = request.getParameter("movieId");
        if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Movie ID is required");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            String action = request.getParameter("action");

            boolean success;
            if ("add".equals(action)) {
                success = favoriteService.addFavorite(userId, movieId);
            } else if ("remove".equals(action)) {
                success = favoriteService.removeFavorite(userId, movieId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid action");
                return;
            }

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Success");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to update favorite status");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid movie ID format");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Please log in to view favorites");
            return;
        }

        // Get movie ID from request
        String movieIdStr = request.getParameter("movieId");
        if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Movie ID is required");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            boolean isFavorite = favoriteService.isFavorite(userId, movieId);
            
            response.setContentType("application/json");
            response.getWriter().write("{\"isFavorite\": " + isFavorite + "}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid movie ID format");
        }
    }
} 