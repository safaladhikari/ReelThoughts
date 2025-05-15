package com.reelthoughts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Map this servlet to the URL "/profile"
@WebServlet("/profile")
public class UserProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Optional: Check if user is logged in (secure your page)
        if (request.getSession().getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Forward to the JSP (server-side, so /WEB-INF/ is accessible)
        request.getRequestDispatcher("/WEB-INF/pages/userprofile.jsp").forward(request, response);
    }
}