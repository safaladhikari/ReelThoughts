package com.reelthoughts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import com.reelthoughts.util.SessionUtil;

@WebServlet("/AddMovieServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 1024 * 1024 * 5,       // 5MB
    maxRequestSize = 1024 * 1024 * 10    // 10MB
)
public class AddMovieController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Admin authorization check
        String userRole = (String) SessionUtil.getAttribute(request, "userRole");
        String userEmail = (String) SessionUtil.getAttribute(request, "user");
        
        if (userRole == null || userEmail == null || !"admin".equals(userRole)) {
            System.out.println("[DEBUG] User is not an admin or not logged in");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Get all form parameters
        String title = request.getParameter("title");
        String genre = request.getParameter("genre");
        int year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String cast1 = request.getParameter("cast1");
        String cast2 = request.getParameter("cast2");
        String cast3 = request.getParameter("cast3");
        String imageLink = request.getParameter("imageLink");
        String description = request.getParameter("description");

        // 3. Validate required fields
        if (title == null || title.trim().isEmpty() ||
            genre == null || genre.trim().isEmpty() ||
            director == null || director.trim().isEmpty() ||
            cast1 == null || cast1.trim().isEmpty() ||
            imageLink == null || imageLink.trim().isEmpty() ||
            description == null || description.trim().isEmpty()) {
            
            SessionUtil.setAttribute(request, "error", "All required fields must be filled out!");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 4. Validate year
        if (year < 1900 || year > 2030) {
            SessionUtil.setAttribute(request, "error", "Invalid year. Must be between 1900 and 2030.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 5. Save to database
        try {
            // Database connection parameters
            String url = "jdbc:mysql://localhost:3306/reelthoughts";
            String username = "root";
            String password = "";
            
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String sql = "INSERT INTO movies (title, genre, year, director, cast1, cast2, cast3, imageLink, description) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, title);
                    pstmt.setString(2, genre);
                    pstmt.setInt(3, year);
                    pstmt.setString(4, director);
                    pstmt.setString(5, cast1);
                    pstmt.setString(6, cast2);
                    pstmt.setString(7, cast3);
                    pstmt.setString(8, imageLink);
                    pstmt.setString(9, description);

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        handleSuccessfulAddition(request, response);
                    } else {
                        SessionUtil.setAttribute(request, "error", "Failed to add movie.");
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
            SessionUtil.setAttribute(request, "error", "Database driver error. Please contact the administrator.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            System.err.println("[ERROR] Database error: " + e.getMessage());
            e.printStackTrace();
            SessionUtil.setAttribute(request, "error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    private void handleSuccessfulAddition(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Set success message
        req.getSession().setAttribute("success", "Movie added successfully!");
        
        // Redirect to admin dashboard
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
