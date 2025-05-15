package com.reelthoughts.controller;

import com.reelthoughts.config.DbConfig;
import com.reelthoughts.model.UserModel;
import com.reelthoughts.util.ImageUtil;
import com.reelthoughts.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.nio.file.AccessDeniedException;

@WebServlet("/register")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 1024 * 1024 * 5,       // 5MB
    maxRequestSize = 1024 * 1024 * 10    // 10MB
)
public class RegisterController extends HttpServlet {
    private final ImageUtil imageUtil = new ImageUtil();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/registration.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get form data
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String retypePassword = request.getParameter("retypePassword");
        Date dob = Date.valueOf(request.getParameter("dob"));
        String gender = request.getParameter("gender");
        Part imagePart = request.getPart("image");

        // 2. Validate passwords
        if (!password.equals(retypePassword)) {
            setErrorAndForward(request, response, "Passwords do not match!", 
                firstName, lastName, email, dob.toString(), gender);
            return;
        }

        // 3. Process image with enhanced error handling
        String imagePath = null;
        try {
            if (imageUtil.isImageFile(imagePart) && imagePart.getSize() > 0) {
                imagePath = imageUtil.saveImageAndGetPath(imagePart, "users");
            }
        } catch (AccessDeniedException e) {
            setErrorAndForward(request, response, "Server cannot save uploaded files. Contact administrator.", 
                firstName, lastName, email, dob.toString(), gender);
            return;
        } catch (IOException e) {
            setErrorAndForward(request, response, "Failed to process image upload.", 
                firstName, lastName, email, dob.toString(), gender);
            return;
        }

        // 4. Encrypt password
        String encryptedPassword = PasswordUtil.hashPassword(email, password);
        if (encryptedPassword == null) {
            setErrorAndForward(request, response, "Password encryption failed", 
                firstName, lastName, email, dob.toString(), gender);
            return;
        }

        // 5. Save to database
        try (Connection conn = DbConfig.getDbConnection()) {
            String sql = "INSERT INTO users (first_name, last_name, email, password, dob, gender, profile_image_path) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, encryptedPassword);
                stmt.setDate(5, dob);
                stmt.setString(6, gender);
                stmt.setString(7, imagePath);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    HttpSession session = request.getSession();
                    session.setAttribute("success", "Registration successful! Please login.");
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            setErrorAndForward(request, response, "Registration failed: " + e.getMessage(), 
                firstName, lastName, email, dob.toString(), gender);
            return;
        }

        // Fallback error
        setErrorAndForward(request, response, "Unknown registration error", 
            firstName, lastName, email, dob.toString(), gender);
    }

    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response,
                                  String error, String firstName, String lastName, 
                                  String email, String dob, String gender) 
            throws ServletException, IOException {
        request.setAttribute("error", error);
        repopulateForm(request, firstName, lastName, email, dob, gender);
        request.getRequestDispatcher("/WEB-INF/pages/registration.jsp").forward(request, response);
    }

    private void repopulateForm(HttpServletRequest request, String firstName, String lastName, 
                               String email, String dob, String gender) {
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("email", email);
        request.setAttribute("dob", dob);
        request.setAttribute("gender", gender);
    }
}





/*
 * package com.reelthoughts.controller;
 * 
 * import com.reelthoughts.config.DbConfig; import
 * com.reelthoughts.model.UserModel; import com.reelthoughts.util.ImageUtil;
 * import com.reelthoughts.util.PasswordUtil; import
 * jakarta.servlet.ServletException; import
 * jakarta.servlet.annotation.MultipartConfig; import
 * jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import
 * java.io.IOException; import java.sql.*; import
 * java.nio.file.AccessDeniedException;
 * 
 * @WebServlet("/register")
 * 
 * @MultipartConfig( fileSizeThreshold = 1024 * 1024, // 1MB maxFileSize = 1024
 * * 1024 * 5, // 5MB maxRequestSize = 1024 * 1024 * 10 // 10MB ) public class
 * RegisterController extends HttpServlet { private final ImageUtil imageUtil =
 * new ImageUtil();
 * 
 * @Override protected void doGet(HttpServletRequest request,
 * HttpServletResponse response) throws ServletException, IOException {
 * request.getRequestDispatcher("/WEB-INF/pages/registration.jsp").forward(
 * request, response); }
 * 
 * protected void doPost(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException {
 * 
 * // 1. Get form data String firstName = request.getParameter("firstName");
 * String lastName = request.getParameter("lastName"); String email =
 * request.getParameter("email"); String password =
 * request.getParameter("password"); String retypePassword =
 * request.getParameter("retypePassword"); Date dob =
 * Date.valueOf(request.getParameter("dob")); String gender =
 * request.getParameter("gender"); Part imagePart = request.getPart("image");
 * 
 * // 2. Validate passwords if (!password.equals(retypePassword)) {
 * setErrorAndForward(request, response, "Passwords do not match!", firstName,
 * lastName, email, dob.toString(), gender); return; }
 * 
 * // 3. Process image with enhanced error handling String imagePath = null; try
 * { if (imageUtil.isImageFile(imagePart) && imagePart.getSize() > 0) {
 * imagePath = imageUtil.saveImageAndGetPath(imagePart, "users"); } } catch
 * (AccessDeniedException e) { setErrorAndForward(request, response,
 * "Server cannot save uploaded files. Contact administrator.", firstName,
 * lastName, email, dob.toString(), gender); return; } catch (IOException e) {
 * setErrorAndForward(request, response, "Failed to process image upload.",
 * firstName, lastName, email, dob.toString(), gender); return; }
 * 
 * // 4. Encrypt password String encryptedPassword =
 * PasswordUtil.hashPassword(email, password); if (encryptedPassword == null) {
 * setErrorAndForward(request, response, "Password encryption failed",
 * firstName, lastName, email, dob.toString(), gender); return; }
 * 
 * // 5. Save to database try (Connection conn = DbConfig.getDbConnection()) {
 * String sql =
 * "INSERT INTO users (first_name, last_name, email, password, dob, gender, profile_image_path) "
 * + "VALUES (?, ?, ?, ?, ?, ?, ?)";
 * 
 * try (PreparedStatement stmt = conn.prepareStatement(sql,
 * Statement.RETURN_GENERATED_KEYS)) { stmt.setString(1, firstName);
 * stmt.setString(2, lastName); stmt.setString(3, email); stmt.setString(4,
 * encryptedPassword); stmt.setDate(5, dob); stmt.setString(6, gender);
 * stmt.setString(7, imagePath);
 * 
 * int affectedRows = stmt.executeUpdate(); if (affectedRows > 0) {
 * request.setAttribute("success", "Registration successful! Please login.");
 * response.sendRedirect(request.getContextPath() + "/login"); return; } } }
 * catch (SQLException | ClassNotFoundException e) { e.printStackTrace();
 * setErrorAndForward(request, response, "Registration failed: " +
 * e.getMessage(), firstName, lastName, email, dob.toString(), gender); return;
 * }
 * 
 * // Fallback error setErrorAndForward(request, response,
 * "Unknown registration error", firstName, lastName, email, dob.toString(),
 * gender); }
 * 
 * private void setErrorAndForward(HttpServletRequest request,
 * HttpServletResponse response, String error, String firstName, String
 * lastName, String email, String dob, String gender) throws ServletException,
 * IOException { request.setAttribute("error", error); repopulateForm(request,
 * firstName, lastName, email, dob, gender);
 * request.getRequestDispatcher("/WEB-INF/pages/registration.jsp").forward(
 * request, response); }
 * 
 * private void repopulateForm(HttpServletRequest request, String firstName,
 * String lastName, String email, String dob, String gender) {
 * request.setAttribute("firstName", firstName);
 * request.setAttribute("lastName", lastName); request.setAttribute("email",
 * email); request.setAttribute("dob", dob); request.setAttribute("gender",
 * gender); } }
 */




/*
 * package com.reelthoughts.controller;
 * 
 * import com.reelthoughts.config.DbConfig; import
 * com.reelthoughts.model.UserModel; import com.reelthoughts.util.ImageUtil;
 * import com.reelthoughts.util.PasswordUtil; import
 * jakarta.servlet.ServletException; import
 * jakarta.servlet.annotation.MultipartConfig; import
 * jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import
 * java.io.IOException; import java.sql.*; import
 * java.nio.file.AccessDeniedException;
 * 
 * @WebServlet("/register")
 * 
 * @MultipartConfig( fileSizeThreshold = 1024 * 1024, // 1MB maxFileSize = 1024
 * * 1024 * 5, // 5MB maxRequestSize = 1024 * 1024 * 10 // 10MB ) public class
 * RegisterController extends HttpServlet { private final ImageUtil imageUtil =
 * new ImageUtil();
 * 
 * @Override protected void doGet(HttpServletRequest request,
 * HttpServletResponse response) throws ServletException, IOException {
 * request.getRequestDispatcher("/WEB-INF/pages/registration.jsp").forward(
 * request, response); }
 * 
 * protected void doPost(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException {
 * 
 * // 1. Get form data String firstName = request.getParameter("firstName");
 * String lastName = request.getParameter("lastName"); String email =
 * request.getParameter("email"); String password =
 * request.getParameter("password"); String retypePassword =
 * request.getParameter("retypePassword"); Date dob =
 * Date.valueOf(request.getParameter("dob")); String gender =
 * request.getParameter("gender"); Part imagePart = request.getPart("image");
 * 
 * // 2. Validate passwords if (!password.equals(retypePassword)) {
 * setErrorAndForward(request, response, "Passwords do not match!", firstName,
 * lastName, email, dob.toString(), gender); return; }
 * 
 * // 3. Process image with enhanced error handling String imagePath = null; try
 * { if (imageUtil.isImageFile(imagePart) && imagePart.getSize() > 0) {
 * imagePath = imageUtil.saveImageAndGetPath(imagePart, "users"); } } catch
 * (AccessDeniedException e) { setErrorAndForward(request, response,
 * "Server cannot save uploaded files. Contact administrator.", firstName,
 * lastName, email, dob.toString(), gender); return; } catch (IOException e) {
 * setErrorAndForward(request, response, "Failed to process image upload.",
 * firstName, lastName, email, dob.toString(), gender); return; }
 * 
 * // 4. Encrypt password String encryptedPassword =
 * PasswordUtil.hashPassword(email, password); if (encryptedPassword == null) {
 * setErrorAndForward(request, response, "Password encryption failed",
 * firstName, lastName, email, dob.toString(), gender); return; }
 * 
 * // 5. Save to database try (Connection conn = DbConfig.getDbConnection()) {
 * String sql =
 * "INSERT INTO users (first_name, last_name, email, password, dob, gender, profile_image_path) "
 * + "VALUES (?, ?, ?, ?, ?, ?, ?)";
 * 
 * try (PreparedStatement stmt = conn.prepareStatement(sql,
 * Statement.RETURN_GENERATED_KEYS)) { stmt.setString(1, firstName);
 * stmt.setString(2, lastName); stmt.setString(3, email); stmt.setString(4,
 * encryptedPassword); stmt.setDate(5, dob); stmt.setString(6, gender);
 * stmt.setString(7, imagePath);
 * 
 * int affectedRows = stmt.executeUpdate(); if (affectedRows > 0) {
 * request.setAttribute("success", "Registration successful! Please login.");
 * response.sendRedirect(request.getContextPath() + "/login.jsp"); return; } } }
 * catch (SQLException | ClassNotFoundException e) { e.printStackTrace();
 * setErrorAndForward(request, response, "Registration failed: " +
 * e.getMessage(), firstName, lastName, email, dob.toString(), gender); return;
 * }
 * 
 * // Fallback error setErrorAndForward(request, response,
 * "Unknown registration error", firstName, lastName, email, dob.toString(),
 * gender); }
 * 
 * private void setErrorAndForward(HttpServletRequest request,
 * HttpServletResponse response, String error, String firstName, String
 * lastName, String email, String dob, String gender) throws ServletException,
 * IOException { request.setAttribute("error", error); repopulateForm(request,
 * firstName, lastName, email, dob, gender);
 * request.getRequestDispatcher("/WEB-INF/pages/registration.jsp").forward(
 * request, response); }
 * 
 * private void repopulateForm(HttpServletRequest request, String firstName,
 * String lastName, String email, String dob, String gender) {
 * request.setAttribute("firstName", firstName);
 * request.setAttribute("lastName", lastName); request.setAttribute("email",
 * email); request.setAttribute("dob", dob); request.setAttribute("gender",
 * gender); } }
 */