package com.reelthoughts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import com.reelthoughts.model.UserModel;
import com.reelthoughts.service.UserService;
import com.reelthoughts.util.SessionUtil;

@WebServlet("/edit-profile")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class EditProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Debug logging
        System.out.println("[EditProfile] Processing request to /edit-profile");
        
        // Get session
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("[EditProfile] No session found, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get email from session
        String email = (String) SessionUtil.getAttribute(request, "user");
        System.out.println("[EditProfile] Session email: " + email);
        
        if (email == null) {
            System.out.println("[EditProfile] No user email in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get complete user object from database
        UserModel user = new UserService().getUserByEmail(email);
        System.out.println("[EditProfile] User found in database: " + (user != null));
        
        if (user == null) {
            System.out.println("[EditProfile] User not found in database, clearing session");
            SessionUtil.removeAttribute(request, "user");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Set the user object in request attribute with the expected name "user"
        request.setAttribute("user", user);
        System.out.println("[EditProfile] Forwarding to userprofile-edit.jsp");
        request.getRequestDispatcher("/WEB-INF/pages/userprofile-edit.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get session
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String email = (String) SessionUtil.getAttribute(request, "user");
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get form data
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String password = request.getParameter("password");
        String retypePassword = request.getParameter("retypePassword");
        Part imagePart = request.getPart("image");
        
        // Create user model
        UserModel user = new UserModel();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDob(java.sql.Date.valueOf(dob));
        user.setGender(gender);
        
        // Update user profile
        UserService userService = new UserService();
        boolean profileUpdated = userService.updateUserProfile(user, imagePart);
        
        // Update password if provided
        if (password != null && !password.trim().isEmpty()) {
            if (!password.equals(retypePassword)) {
                request.setAttribute("error", "Passwords do not match");
                request.getRequestDispatcher("/WEB-INF/pages/userprofile-edit.jsp").forward(request, response);
                return;
            }
            boolean passwordUpdated = userService.updatePassword(email, password);
            if (!passwordUpdated) {
                request.setAttribute("error", "Failed to update password");
                request.getRequestDispatcher("/WEB-INF/pages/userprofile-edit.jsp").forward(request, response);
                return;
            }
        }
        
        if (profileUpdated) {
            SessionUtil.setAttribute(request, "successMessage", "Profile updated successfully!");
        } else {
            SessionUtil.setAttribute(request, "errorMessage", "Failed to update profile");
        }
        
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}