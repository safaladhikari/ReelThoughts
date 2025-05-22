package com.reelthoughts.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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

// Map this servlet to the URL "/profile"
@WebServlet("/profile")
public class UserProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || SessionUtil.getAttribute(request, "user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userEmail = (String) SessionUtil.getAttribute(request, "user");
        
        try (Connection conn = DbConfig.getDbConnection()) {
            // Get user data
            String userQuery = "SELECT user_id, first_name, last_name, email, profile_image_path FROM users WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(userQuery)) {
                pstmt.setString(1, userEmail);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", rs.getInt("user_id"));
                        user.put("firstName", rs.getString("first_name"));
                        user.put("lastName", rs.getString("last_name"));
                        user.put("email", rs.getString("email"));
                        String profileImagePath = rs.getString("profile_image_path");
                        System.out.println("Profile Image Path: " + profileImagePath); // Debug line

                        // Debug: Check if the file exists from the server's perspective
                        String realPath = getServletContext().getRealPath(profileImagePath);
                        System.out.println("[DEBUG Controller] Real file system path for image: " + realPath);
                        if (realPath != null) {
                            java.io.File imageFile = new java.io.File(realPath);
                            System.out.println("[DEBUG Controller] File exists at real path: " + imageFile.exists());
                            System.out.println("[DEBUG Controller] Can read file at real path: " + imageFile.canRead());
                        } else {
                             System.out.println("[DEBUG Controller] Could not get real path for: " + profileImagePath);
                        }

                        user.put("profilePicture", profileImagePath);
                        request.setAttribute("user", user);

                        // Get latest 4 favorite movies
                        String favoritesQuery = "SELECT m.* FROM movies m " +
                            "JOIN user_favorite f ON m.id = f.Movie_ID " +
                            "WHERE f.User_ID = ? " +
                            "ORDER BY f.created_at DESC LIMIT 4";
                        List<Map<String, Object>> favoriteMovies = getMovies(conn, favoritesQuery, rs.getInt("user_id"));
                        request.setAttribute("favoriteMovies", favoriteMovies);

                        // Get latest 4 reviewed movies
                        String reviewsQuery = "SELECT m.*, ur.RatingValue as rating, ur.RatingDate as ratedAt FROM movies m " +
                            "JOIN user_rating ur ON m.id = ur.Movie_ID " +
                            "WHERE ur.User_ID = ? " +
                            "ORDER BY ur.RatingDate DESC LIMIT 4";
                        List<Map<String, Object>> reviewedMovies = getMovies(conn, reviewsQuery, rs.getInt("user_id"));
                        request.setAttribute("reviewedMovies", reviewedMovies);

                        // Get latest 4 watchlist movies
                        String watchlistQuery = "SELECT m.* FROM movies m " +
                            "JOIN user_watchlist wl ON m.id = wl.Movie_ID " +
                            "WHERE wl.User_ID = ? " +
                            "ORDER BY wl.created_at DESC LIMIT 4";
                        List<Map<String, Object>> watchlistMovies = getMovies(conn, watchlistQuery, rs.getInt("user_id"));
                        request.setAttribute("watchlistMovies", watchlistMovies);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading profile data");
        }

        request.getRequestDispatcher("/WEB-INF/pages/userprofile.jsp").forward(request, response);
    }

    private List<Map<String, Object>> getMovies(Connection conn, String query, int userId) throws SQLException {
        List<Map<String, Object>> movies = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getInt("id"));
                    movie.put("title", rs.getString("title"));
                    movie.put("director", rs.getString("director"));
                    movie.put("year", rs.getString("year"));
                    movie.put("genre", rs.getString("genre"));
                    movie.put("imageLink", rs.getString("imageLink"));
                    
                    // Add rating and ratedAt if they exist in the result set
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        if (columnName.equalsIgnoreCase("rating")) {
                            movie.put("rating", rs.getInt("rating"));
                        } else if (columnName.equalsIgnoreCase("ratedAt")) {
                            movie.put("ratedAt", rs.getTimestamp("ratedAt"));
                        }
                    }
                    
                    movies.add(movie);
                }
            }
        }
        return movies;
    }
}



/*
 * package com.reelthoughts.controller;
 * 
 * import java.io.IOException; import java.sql.Connection; import
 * java.sql.PreparedStatement; import java.sql.ResultSet; import
 * java.sql.ResultSetMetaData; import java.sql.SQLException; import
 * java.sql.Timestamp; import java.util.ArrayList; import java.util.HashMap;
 * import java.util.List; import java.util.Map;
 * 
 * import jakarta.servlet.ServletException; import
 * jakarta.servlet.annotation.WebServlet; import
 * jakarta.servlet.http.HttpServlet; import
 * jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse; import
 * jakarta.servlet.http.HttpSession;
 * 
 * import com.reelthoughts.config.DbConfig; import
 * com.reelthoughts.util.SessionUtil;
 * 
 * // Map this servlet to the URL "/profile"
 * 
 * @WebServlet("/profile") public class UserProfileController extends
 * HttpServlet { private static final long serialVersionUID = 1L;
 * 
 * @Override protected void doGet(HttpServletRequest request,
 * HttpServletResponse response) throws ServletException, IOException {
 * HttpSession session = request.getSession(false); if (session == null ||
 * SessionUtil.getAttribute(request, "user") == null) {
 * response.sendRedirect(request.getContextPath() + "/login"); return; }
 * 
 * String userEmail = (String) SessionUtil.getAttribute(request, "user");
 * System.out.
 * println("[DEBUG] UserProfileController: Processing request for user email: "
 * + userEmail);
 * 
 * Connection conn = null; try { conn = DbConfig.getDbConnection(); if (conn ==
 * null) { System.out.
 * println("[DEBUG] UserProfileController: Database connection failed");
 * request.setAttribute("error", "Database connection error");
 * request.getRequestDispatcher("/WEB-INF/pages/userprofile.jsp").forward(
 * request, response); return; }
 * 
 * // Verify tables exist and have correct structure verifyTables(conn);
 * 
 * // Get user data String userQuery =
 * "SELECT user_id, first_name, last_name, email, profile_image_path, created_at FROM users WHERE email = ?"
 * ; System.out.println("[DEBUG] UserProfileController: Executing user query: "
 * + userQuery);
 * 
 * try (PreparedStatement pstmt = conn.prepareStatement(userQuery)) {
 * pstmt.setString(1, userEmail); try (ResultSet rs = pstmt.executeQuery()) { if
 * (rs.next()) { int userId = rs.getInt("user_id");
 * System.out.println("[DEBUG] UserProfileController: Found user with ID: " +
 * userId);
 * 
 * Map<String, Object> user = new HashMap<>(); user.put("id", userId);
 * user.put("firstName", rs.getString("first_name")); user.put("lastName",
 * rs.getString("last_name")); user.put("email", rs.getString("email"));
 * user.put("profilePicture", rs.getString("profile_image_path"));
 * user.put("createdAt", rs.getTimestamp("created_at"));
 * request.setAttribute("user", user);
 * 
 * // Get latest 4 favorite movies String favoritesQuery =
 * "SELECT m.* FROM movies m " + "JOIN user_favorite f ON m.id = f.Movie_ID " +
 * "WHERE f.User_ID = ? " + "ORDER BY f.created_at DESC LIMIT 4"; System.out.
 * println("[DEBUG] UserProfileController: Executing favorites query: " +
 * favoritesQuery); List<Map<String, Object>> favoriteMovies = getMovies(conn,
 * favoritesQuery, userId);
 * System.out.println("[DEBUG] UserProfileController: Found " +
 * favoriteMovies.size() + " favorite movies");
 * request.setAttribute("favoriteMovies", favoriteMovies);
 * 
 * // Get latest 4 reviewed movies String reviewsQuery =
 * "SELECT m.*, r.review_text FROM movies m " +
 * "JOIN reviews r ON m.id = r.movie_id " + "WHERE r.user_id = ? " +
 * "ORDER BY r.created_at DESC LIMIT 4";
 * System.out.println("[DEBUG] UserProfileController: Executing reviews query: "
 * + reviewsQuery); List<Map<String, Object>> reviewedMovies = getMovies(conn,
 * reviewsQuery, userId);
 * System.out.println("[DEBUG] UserProfileController: Found " +
 * reviewedMovies.size() + " reviewed movies");
 * request.setAttribute("reviewedMovies", reviewedMovies);
 * 
 * // Get latest 4 watchlist movies String watchlistQuery =
 * "SELECT m.* FROM movies m " + "JOIN user_watchlist wl ON m.id = wl.Movie_ID "
 * + "WHERE wl.User_ID = ? " + "ORDER BY wl.created_at DESC LIMIT 4";
 * System.out.
 * println("[DEBUG] UserProfileController: Executing watchlist query: " +
 * watchlistQuery); List<Map<String, Object>> watchlistMovies = getMovies(conn,
 * watchlistQuery, userId);
 * System.out.println("[DEBUG] UserProfileController: Found " +
 * watchlistMovies.size() + " watchlist movies");
 * request.setAttribute("watchlistMovies", watchlistMovies);
 * 
 * // Debug: Print table structures printTableStructure(conn, "user_rating");
 * printTableStructure(conn, "user_watchlist"); } else { System.out.
 * println("[DEBUG] UserProfileController: No user found with email: " +
 * userEmail); } } } } catch (SQLException | ClassNotFoundException e) {
 * System.out.println("[DEBUG] UserProfileController: Database error: " +
 * e.getMessage()); e.printStackTrace(); request.setAttribute("error",
 * "Error loading profile data"); } finally { if (conn != null) { try {
 * conn.close(); } catch (SQLException e) { System.out.
 * println("[DEBUG] UserProfileController: Error closing connection: " +
 * e.getMessage()); } } }
 * 
 * request.getRequestDispatcher("/WEB-INF/pages/userprofile.jsp").forward(
 * request, response); }
 * 
 * private void printTableStructure(Connection conn, String tableName) throws
 * SQLException {
 * System.out.println("[DEBUG] UserProfileController: Structure of table " +
 * tableName + ":"); try (PreparedStatement pstmt =
 * conn.prepareStatement("SHOW COLUMNS FROM " + tableName)) { ResultSet rs =
 * pstmt.executeQuery(); while (rs.next()) {
 * System.out.println("[DEBUG] Column: " + rs.getString("Field") + ", Type: " +
 * rs.getString("Type") + ", Null: " + rs.getString("Null") + ", Key: " +
 * rs.getString("Key")); } } }
 * 
 * private void verifyTables(Connection conn) throws SQLException { String[]
 * tables = {"user_favorite", "user_rating", "user_watchlist", "movies"}; for
 * (String table : tables) { try { String query = "SELECT COUNT(*) FROM " +
 * table; try (PreparedStatement pstmt = conn.prepareStatement(query)) {
 * ResultSet rs = pstmt.executeQuery(); if (rs.next()) { int count =
 * rs.getInt(1); System.out.println("[DEBUG] UserProfileController: Table " +
 * table + " exists with " + count + " rows"); } } } catch (SQLException e) {
 * System.out.println("[DEBUG] UserProfileController: Error checking table " +
 * table + ": " + e.getMessage()); throw e; } } }
 * 
 * private List<Map<String, Object>> getMovies(Connection conn, String query,
 * int userId) throws SQLException { List<Map<String, Object>> movies = new
 * ArrayList<>(); try (PreparedStatement pstmt = conn.prepareStatement(query)) {
 * pstmt.setInt(1, userId); System.out.
 * println("[DEBUG] UserProfileController: Executing query with userId: " +
 * userId); try (ResultSet rs = pstmt.executeQuery()) { ResultSetMetaData
 * metaData = rs.getMetaData(); int columnCount = metaData.getColumnCount();
 * 
 * while (rs.next()) { Map<String, Object> movie = new HashMap<>();
 * movie.put("id", rs.getInt("id")); movie.put("title", rs.getString("title"));
 * movie.put("director", rs.getString("director")); movie.put("year",
 * rs.getString("year")); movie.put("genre", rs.getString("genre"));
 * movie.put("imageLink", rs.getString("imageLink"));
 * 
 * // Only add rating if the column exists in the result set for (int i = 1; i
 * <= columnCount; i++) { if
 * (metaData.getColumnName(i).equalsIgnoreCase("rating")) { movie.put("rating",
 * rs.getInt("rating")); break; } }
 * 
 * movies.add(movie);
 * System.out.println("[DEBUG] UserProfileController: Added movie: " +
 * movie.get("title")); } } } catch (SQLException e) {
 * System.out.println("[DEBUG] UserProfileController: Error executing query: " +
 * query); System.out.println("[DEBUG] UserProfileController: Error message: " +
 * e.getMessage()); e.printStackTrace(); throw e; } return movies; } }
 */