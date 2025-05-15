package com.reelthoughts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import com.reelthoughts.config.DbConfig;
import com.reelthoughts.util.PasswordUtil;
import com.reelthoughts.util.SessionUtil;
import com.reelthoughts.util.ImageUtil;
import com.reelthoughts.model.UserModel;
import com.reelthoughts.service.RegisterService;
import java.nio.file.AccessDeniedException;

@WebServlet("/admin/add-user")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 1024 * 1024 * 5,       // 5MB
    maxRequestSize = 1024 * 1024 * 10    // 10MB
)
public class AddUserController extends HttpServlet {
    private static final String ADMIN_EMAIL_SUFFIX = "@adminreelthoughts.com";
    private final ImageUtil imageUtil = new ImageUtil();
    private final RegisterService registerService = new RegisterService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Admin authorization check
        String userRole = (String) SessionUtil.getAttribute(request, "userRole");
        if (!"admin".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Get all form parameters
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String retypePassword = request.getParameter("retypePassword");
        String gender = request.getParameter("gender");
        Part imagePart = request.getPart("image");

        // 3. Validate passwords match
        if (!password.equals(retypePassword)) {
            SessionUtil.setAttribute(request, "error", "Passwords do not match!");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 4. Parse and validate date
        java.sql.Date dob;
        try {
            dob = java.sql.Date.valueOf(request.getParameter("dob"));
        } catch (IllegalArgumentException e) {
            SessionUtil.setAttribute(request, "error", "Invalid date format. Please use YYYY-MM-DD");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 5. Validate admin email
        if (!email.endsWith(ADMIN_EMAIL_SUFFIX)) {
            SessionUtil.setAttribute(request, "error", "Admin email must end with " + ADMIN_EMAIL_SUFFIX);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 6. Process image upload
        String imagePath = null;
        try {
            if (imagePart != null && imagePart.getSize() > 0) {
                if (imageUtil.isImageFile(imagePart)) {
                    imagePath = imageUtil.saveImageAndGetPath(imagePart, "users");
                } else {
                    SessionUtil.setAttribute(request, "error", "Invalid image file type");
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                    return;
                }
            }
        } catch (AccessDeniedException e) {
            SessionUtil.setAttribute(request, "error", "Server cannot save uploaded files. Contact administrator.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        } catch (IOException e) {
            SessionUtil.setAttribute(request, "error", "Failed to process image upload.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 7. Hash password
        String hashedPassword = PasswordUtil.hashPassword(email, password);
        if (hashedPassword == null) {
            SessionUtil.setAttribute(request, "error", "Password encryption failed");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        // 8. Create UserModel and set properties
        UserModel user = new UserModel();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setDob(dob);
        user.setGender(gender);
        user.setProfileImagePath(imagePath);

        // 9. Save to database using RegisterService
        Boolean result = registerService.registerUser(user);
        if (result != null && result) {
            SessionUtil.setAttribute(request, "success", "Admin user created successfully!");
        } else {
            SessionUtil.setAttribute(request, "error", "Failed to create admin user");
        }

        // 10. Redirect back to admin dashboard
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}