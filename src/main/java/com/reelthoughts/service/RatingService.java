package com.reelthoughts.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RatingService {
    private Connection dbConn;

    public RatingService(Connection dbConn) {
        this.dbConn = dbConn;
    }

    public boolean addOrUpdateRating(int userId, int movieId, int rating) {
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return false;
        }

        // First check if a rating already exists
        String checkQuery = "SELECT Rating_ID FROM user_rating WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement checkStmt = dbConn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, movieId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Rating exists, update it
                String updateQuery = "UPDATE user_rating SET RatingValue = ?, RatingDate = CURRENT_TIMESTAMP WHERE User_ID = ? AND Movie_ID = ?";
                try (PreparedStatement updateStmt = dbConn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, rating);
                    updateStmt.setInt(2, userId);
                    updateStmt.setInt(3, movieId);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                // No rating exists, insert new one
                String insertQuery = "INSERT INTO user_rating (Movie_ID, RatingValue, RatingDate, User_ID) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
                try (PreparedStatement insertStmt = dbConn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, movieId);
                    insertStmt.setInt(2, rating);
                    insertStmt.setInt(3, userId);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error managing rating: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getRating(int userId, int movieId) {
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return null;
        }

        String query = "SELECT RatingValue, RatingDate FROM user_rating WHERE User_ID = ? AND Movie_ID = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> ratingData = new HashMap<>();
                ratingData.put("rating", rs.getInt("RatingValue"));
                ratingData.put("date", rs.getTimestamp("RatingDate"));
                return ratingData;
            }
        } catch (SQLException e) {
            System.err.println("Error getting rating: " + e.getMessage());
        }
        return null;
    }

    public double getAverageRating(int movieId) {
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return 0.0;
        }

        String query = "SELECT AVG(RatingValue) as avgRating FROM user_rating WHERE Movie_ID = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avgRating");
            }
        } catch (SQLException e) {
            System.err.println("Error getting average rating: " + e.getMessage());
        }
        return 0.0;
    }
} 




/*
 * package com.reelthoughts.service;
 * 
 * import java.sql.Connection; import java.sql.PreparedStatement; import
 * java.sql.ResultSet; import java.sql.SQLException; import java.util.HashMap;
 * import java.util.Map;
 * 
 * public class RatingService { private Connection dbConn;
 * 
 * public RatingService(Connection dbConn) { this.dbConn = dbConn; }
 * 
 * public boolean addOrUpdateRating(int userId, int movieId, int rating) { if
 * (dbConn == null) { System.err.println("Database connection not available");
 * return false; }
 * 
 * // First check if a rating already exists String checkQuery =
 * "SELECT Rating_ID FROM rating WHERE User_ID = ? AND Movie_ID = ?"; try
 * (PreparedStatement checkStmt = dbConn.prepareStatement(checkQuery)) {
 * checkStmt.setInt(1, userId); checkStmt.setInt(2, movieId); ResultSet rs =
 * checkStmt.executeQuery();
 * 
 * if (rs.next()) { // Rating exists, update it String updateQuery =
 * "UPDATE rating SET RatingValue = ?, RatingDate = CURRENT_TIMESTAMP WHERE User_ID = ? AND Movie_ID = ?"
 * ; try (PreparedStatement updateStmt = dbConn.prepareStatement(updateQuery)) {
 * updateStmt.setInt(1, rating); updateStmt.setInt(2, userId);
 * updateStmt.setInt(3, movieId); return updateStmt.executeUpdate() > 0; } }
 * else { // No rating exists, insert new one String insertQuery =
 * "INSERT INTO rating (Movie_ID, RatingValue, RatingDate, User_ID) VALUES (?, ?, CURRENT_TIMESTAMP, ?)"
 * ; try (PreparedStatement insertStmt = dbConn.prepareStatement(insertQuery)) {
 * insertStmt.setInt(1, movieId); insertStmt.setInt(2, rating);
 * insertStmt.setInt(3, userId); return insertStmt.executeUpdate() > 0; } } }
 * catch (SQLException e) { System.err.println("Error managing rating: " +
 * e.getMessage()); return false; } }
 * 
 * public Map<String, Object> getRating(int userId, int movieId) { if (dbConn ==
 * null) { System.err.println("Database connection not available"); return null;
 * }
 * 
 * String query =
 * "SELECT RatingValue, RatingDate FROM rating WHERE User_ID = ? AND Movie_ID = ?"
 * ; try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
 * stmt.setInt(1, userId); stmt.setInt(2, movieId); ResultSet rs =
 * stmt.executeQuery();
 * 
 * if (rs.next()) { Map<String, Object> ratingData = new HashMap<>();
 * ratingData.put("rating", rs.getInt("RatingValue")); ratingData.put("date",
 * rs.getTimestamp("RatingDate")); return ratingData; } } catch (SQLException e)
 * { System.err.println("Error getting rating: " + e.getMessage()); } return
 * null; }
 * 
 * public double getAverageRating(int movieId) { if (dbConn == null) {
 * System.err.println("Database connection not available"); return 0.0; }
 * 
 * String query =
 * "SELECT AVG(RatingValue) as avgRating FROM rating WHERE Movie_ID = ?"; try
 * (PreparedStatement stmt = dbConn.prepareStatement(query)) { stmt.setInt(1,
 * movieId); ResultSet rs = stmt.executeQuery(); if (rs.next()) { return
 * rs.getDouble("avgRating"); } } catch (SQLException e) {
 * System.err.println("Error getting average rating: " + e.getMessage()); }
 * return 0.0; } }
 */