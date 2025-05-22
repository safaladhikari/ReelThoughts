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
import com.reelthoughts.util.SessionUtil;

@WebServlet(asyncSupported = true, urlPatterns = { "/movie-details" })
public class MovieDetailsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("MovieDetailsController: Processing request");
        
        String movieId = request.getParameter("id");
        if (movieId == null || movieId.trim().isEmpty()) {
            System.out.println("MovieDetailsController: No movie ID provided");
            request.setAttribute("error", "Movie ID is required");
            request.getRequestDispatcher("/WEB-INF/pages/movie-details.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DbConfig.getDbConnection()) {
            System.out.println("MovieDetailsController: Database connection established");
            
            // Get movie details
            Map<String, Object> movie = getMovieDetails(conn, movieId);
            if (movie == null) {
                System.out.println("MovieDetailsController: Movie not found");
                request.setAttribute("error", "Movie not found");
                request.getRequestDispatcher("/WEB-INF/pages/movie-details.jsp").forward(request, response);
                return;
            }

            // Get user session
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer userId = (Integer) SessionUtil.getAttribute(request, "userId");
                
                // If userId is null, try to get it from the email
                if (userId == null) {
                    String email = (String) SessionUtil.getAttribute(request, "user");
                    if (email != null) {
                        String query = "SELECT user_id FROM users WHERE email = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(query)) {
                            stmt.setString(1, email);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                userId = rs.getInt("user_id");
                                SessionUtil.setAttribute(request, "userId", userId);
                            }
                        }
                    }
                }

                if (userId != null) {
                    // Check if movie is in user's favorites
                    boolean isFavorite = checkIfFavorite(conn, userId, movieId);
                    request.setAttribute("isFavorite", isFavorite);
                    
                    // Check if movie is in user's watchlist
                    boolean isInWatchlist = checkIfInWatchlist(conn, userId, movieId);
                    request.setAttribute("isInWatchlist", isInWatchlist);
                    
                    // Get user's rating for the movie
                    Integer userRating = getUserRating(conn, userId, movieId);
                    request.setAttribute("userRating", userRating);
                }
            }

            // Get reviews for the movie
            List<Map<String, Object>> reviews = getMovieReviews(conn, movieId);
            request.setAttribute("reviews", reviews);
            request.setAttribute("movie", movie);
            
            System.out.println("MovieDetailsController: Forwarding to movie-details.jsp");
            request.getRequestDispatcher("/WEB-INF/pages/movie-details.jsp").forward(request, response);
            
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("MovieDetailsController: Error - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Database error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/pages/movie-details.jsp").forward(request, response);
        }
    }

    private Map<String, Object> getMovieDetails(Connection conn, String movieId) throws SQLException {
        String sql = "SELECT *, CONCAT_WS(', ', cast1, cast2, cast3) as cast FROM movies WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getString("id"));
                    movie.put("title", rs.getString("title"));
                    movie.put("director", rs.getString("director"));
                    movie.put("year", rs.getString("year"));
                    movie.put("genre", rs.getString("genre"));
                    movie.put("description", rs.getString("description"));
                    movie.put("imageLink", rs.getString("imageLink"));
                    movie.put("cast", rs.getString("cast"));
                    return movie;
                }
            }
        }
        return null;
    }

    private List<Map<String, Object>> getMovieReviews(Connection conn, String movieId) throws SQLException {
        List<Map<String, Object>> reviews = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(u.first_name, ' ', u.last_name) as username, u.profile_image_path as userProfilePicture " +
                    "FROM reviews r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "WHERE r.movie_id = ? " +
                    "ORDER BY r.created_at DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> review = new HashMap<>();
                    review.put("id", rs.getString("id"));
                    review.put("userId", rs.getString("user_id"));
                    review.put("username", rs.getString("username"));
                    review.put("reviewText", rs.getString("review_text"));
                    review.put("createdAt", rs.getTimestamp("created_at"));
                    review.put("userProfilePicture", rs.getString("userProfilePicture"));
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    private boolean checkIfFavorite(Connection conn, Integer userId, String movieId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_favorite WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean checkIfInWatchlist(Connection conn, Integer userId, String movieId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_watchlist WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Integer getUserRating(Connection conn, Integer userId, String movieId) throws SQLException {
        String sql = "SELECT RatingValue FROM user_rating WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RatingValue");
                }
            }
        }
        return null;
    }
} 