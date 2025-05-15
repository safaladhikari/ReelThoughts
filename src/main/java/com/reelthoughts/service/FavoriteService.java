package com.reelthoughts.service;

import com.reelthoughts.config.DbConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FavoriteService {
    private Connection dbConn;

    public FavoriteService() {
        try {
            this.dbConn = DbConfig.getDbConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Database connection error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean addFavorite(int userId, int movieId) {
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return false;
        }

        String query = "INSERT INTO `user-favorite` (User_ID, Movie_ID) VALUES (?, ?)";

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding favorite: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFavorite(int userId, int movieId) {
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return false;
        }

        String query = "DELETE FROM `user-favorite` WHERE User_ID = ? AND Movie_ID = ?";

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing favorite: " + e.getMessage());
            return false;
        }
    }

    public boolean isFavorite(int userId, int movieId) {
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return false;
        }

        String query = "SELECT COUNT(*) as count FROM `user-favorite` WHERE User_ID = ? AND Movie_ID = ?";

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking favorite status: " + e.getMessage());
        }
        return false;
    }

    public List<Map<String, Object>> getUserFavorites(int userId) {
        List<Map<String, Object>> favorites = new ArrayList<>();
        if (dbConn == null) {
            System.err.println("Database connection not available");
            return favorites;
        }

        String query = "SELECT m.* FROM movies m " +
                      "INNER JOIN `user-favorite` f ON m.id = f.Movie_ID " +
                      "WHERE f.User_ID = ?";

        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
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
                favorites.add(movie);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user favorites: " + e.getMessage());
        }
        return favorites;
    }
} 