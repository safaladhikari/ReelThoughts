package com.reelthoughts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.reelthoughts.model.UserModel;
import com.reelthoughts.service.UserService;
import com.reelthoughts.util.SessionUtil;

@WebServlet("/edit-profile")
public class EditProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Debug logging
        System.out.println("[EditProfile] Processing request to /edit-profile");
        System.out.println("[EditProfile] Session ID: " + (request.getSession(false) != null ? request.getSession(false).getId() : "No session"));
        
        // Get email from session (as String)
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
        String email = (String) SessionUtil.getAttribute(request, "user");
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        
        UserModel user = new UserModel();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDob(java.sql.Date.valueOf(dob));
        user.setGender(gender);
        
        UserService userService = new UserService();
        if (userService.updateUserProfile(user, null)) {
            request.getSession().setAttribute("successMessage", "Profile updated successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to update profile");
        }
        
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}