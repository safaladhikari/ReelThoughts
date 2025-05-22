package com.reelthoughts.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.reelthoughts.config.DbConfig;
//import com.reelthoughts.util.DatabaseUtil;
import com.reelthoughts.util.SessionUtil;

@WebServlet("/review")
public class ReviewController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("[DEBUG] ReviewController: Processing POST request");
        
        // Get user ID from session
        Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
        System.out.println("[DEBUG] User ID from session: " + userId);
        
        if (userId == null) {
            System.out.println("[DEBUG] No user ID found in session");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain");
            response.getWriter().write("Please log in to submit a review");
            return;
        }

        String movieId = request.getParameter("movieId");
        String reviewText = request.getParameter("reviewText");
        
        System.out.println("[DEBUG] Received parameters - movieId: " + movieId + ", reviewText length: " + (reviewText != null ? reviewText.length() : 0));

        if (movieId == null || reviewText == null || reviewText.trim().isEmpty()) {
            System.out.println("[DEBUG] Missing required parameters");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().write("Movie ID and review text are required");
            return;
        }

        try (Connection conn = DbConfig.getDbConnection()) {
            System.out.println("[DEBUG] Got database connection");
            
            // First check if the movie exists
            String checkMovieSql = "SELECT id FROM movies WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkMovieSql)) {
                checkStmt.setString(1, movieId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("[DEBUG] Movie not found with ID: " + movieId);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.setContentType("text/plain");
                        response.getWriter().write("Movie not found");
                        return;
                    }
                }
            }
            
            String sql = "INSERT INTO reviews (user_id, movie_id, review_text, created_at) VALUES (?, ?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE review_text = ?, created_at = NOW()";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, movieId);
                stmt.setString(3, reviewText);
                stmt.setString(4, reviewText);
                
                System.out.println("[DEBUG] Executing SQL: " + sql);
                System.out.println("[DEBUG] Parameters - userId: " + userId + ", movieId: " + movieId);
                
                int result = stmt.executeUpdate();
                System.out.println("[DEBUG] SQL execution result: " + result);
                
                if (result > 0) {
                    System.out.println("[DEBUG] Review added/updated successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("text/plain");
                    response.getWriter().write("Success");
                } else {
                    System.out.println("[DEBUG] Failed to add/update review");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("text/plain");
                    response.getWriter().write("Failed to submit review");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in ReviewController: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().write("Error submitting review: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("[DEBUG] ReviewController: Processing DELETE request");
        
        // Get user ID from session
        Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
        if (userId == null) {
            System.out.println("[DEBUG] No user ID found in session for delete");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain");
            response.getWriter().write("Please log in to delete reviews");
            return;
        }

        String reviewId = request.getParameter("reviewId");
        String movieId = request.getParameter("movieId");

        System.out.println("[DEBUG] Delete request parameters - reviewId: " + reviewId + ", movieId: " + movieId);

        if (reviewId == null || movieId == null) {
            System.out.println("[DEBUG] Missing required fields for delete");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().write("Missing required fields");
            return;
        }

        try (Connection conn = DbConfig.getDbConnection()) {
            System.out.println("[DEBUG] Got database connection for delete");
            
            // Verify that the review belongs to the user
            String verifyQuery = "SELECT id FROM reviews WHERE id = ? AND user_id = ?";
            try (PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery)) {
                verifyStmt.setString(1, reviewId);
                verifyStmt.setInt(2, userId);
                try (ResultSet rs = verifyStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("[DEBUG] Review not found or doesn't belong to user");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("text/plain");
                        response.getWriter().write("You can only delete your own reviews");
                        return;
                    }
                }
            }

            // Delete the review
            String deleteQuery = "DELETE FROM reviews WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, reviewId);
                int result = deleteStmt.executeUpdate();
                System.out.println("[DEBUG] Delete result: " + result);
                
                if (result > 0) {
                    System.out.println("[DEBUG] Review deleted successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("text/plain");
                    response.getWriter().write("Review deleted successfully");
                } else {
                    System.out.println("[DEBUG] Failed to delete review");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("text/plain");
                    response.getWriter().write("Failed to delete review");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in ReviewController delete: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().write("Error deleting review: " + e.getMessage());
        }
    }
} 