package com.reelthoughts.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.reelthoughts.config.DbConfig;
import com.reelthoughts.service.LoginService;
import com.reelthoughts.model.UserModel;
import com.reelthoughts.util.SessionUtil;

@WebServlet(asyncSupported = true, urlPatterns = { "/login" })
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ADMIN_EMAIL_SUFFIX = "@adminreelthoughts.com";
    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_USER = "user";
    
    private final LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is already logged in
        if (SessionUtil.getAttribute(request, "user") != null) {
            redirectBasedOnRole(request, response);
            return;
        }

        // Check for success message from registration
        String success = (String) SessionUtil.getAttribute(request, "success");
        if (success != null) {
            request.setAttribute("success", success);
            SessionUtil.removeAttribute(request, "success");
        }

        request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setPassword(password);

        try {
            Boolean loginStatus = loginService.authenticateUser(user);
            
            if (loginStatus != null && loginStatus) {
                handleSuccessfulLogin(req, resp, email);
            } else {
                handleLoginFailure(req, resp, loginStatus);
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            handleLoginFailure(req, resp, false);
        }
    }

    private void handleSuccessfulLogin(HttpServletRequest req, HttpServletResponse resp, String email)
            throws ServletException, IOException {
        // Set session attributes using SessionUtil
        SessionUtil.setAttribute(req, "user", email);
        String role = email.endsWith(ADMIN_EMAIL_SUFFIX) ? ROLE_ADMIN : ROLE_USER;
        SessionUtil.setAttribute(req, "userRole", role);
        
        // Get and set user ID in session
        int userId = getUserIdFromDatabase(email);
        if (userId > 0) {
            SessionUtil.setAttribute(req, "userId", userId);
            System.out.println("[LOGIN] User ID set in session: " + userId);
        }

        // Debug output
        HttpSession session = req.getSession(false);
        System.out.println("[LOGIN] Successful login for: " + email);
        System.out.println("[SESSION] Role: " + role);
        System.out.println("[SESSION] Session ID: " + (session != null ? session.getId() : "No session"));
        System.out.println("[SESSION] Session creation time: " + (session != null ? session.getCreationTime() : "No session"));
        System.out.println("[SESSION] Session last accessed time: " + (session != null ? session.getLastAccessedTime() : "No session"));
        System.out.println("[SESSION] Session max inactive interval: " + (session != null ? session.getMaxInactiveInterval() : "No session"));

        // Redirect based on role
        if (ROLE_ADMIN.equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }
    
    private int getUserIdFromDatabase(String email) {
        try {
            Connection conn = DbConfig.getDbConnection();
            String query = "SELECT user_id FROM users WHERE email = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        System.out.println("[DEBUG] Found user ID: " + userId);
                        return userId;
                    }
                }
            }
            
            // Close the connection
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error retrieving user ID: " + e.getMessage());
        }
        
        return -1;
    }

    private void handleLoginFailure(HttpServletRequest req, HttpServletResponse resp, Boolean loginStatus)
            throws ServletException, IOException {
        String errorMessage = (loginStatus == null) 
                ? "Server error. Please try again later."
                : "Incorrect email or password. Please try again.";

        req.setAttribute("error", errorMessage);
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp);
        
        System.out.println("[LOGIN] Failed login attempt: " + errorMessage);
    }

    private void redirectBasedOnRole(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String role = (String) SessionUtil.getAttribute(req, "userRole");
        
        if (role == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (ROLE_ADMIN.equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }
}




/*
 * package com.reelthoughts.controller;
 * 
 * import java.io.IOException; import java.sql.Connection; import
 * java.sql.PreparedStatement; import java.sql.ResultSet; import
 * java.sql.SQLException; import java.sql.Statement;
 * 
 * import jakarta.servlet.ServletException; import
 * jakarta.servlet.annotation.WebServlet; import
 * jakarta.servlet.http.HttpServlet; import
 * jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse; import
 * jakarta.servlet.http.HttpSession;
 * 
 * import com.reelthoughts.config.DbConfig; import
 * com.reelthoughts.service.LoginService; import
 * com.reelthoughts.model.UserModel; import com.reelthoughts.util.SessionUtil;
 * 
 * @WebServlet(asyncSupported = true, urlPatterns = { "/login" }) public class
 * LoginController extends HttpServlet { private static final long
 * serialVersionUID = 1L; private static final String ADMIN_EMAIL_SUFFIX =
 * "@adminreelthoughts.com"; private static final String ROLE_ADMIN = "admin";
 * private static final String ROLE_USER = "user";
 * 
 * private final LoginService loginService;
 * 
 * public LoginController() { this.loginService = new LoginService(); }
 * 
 * @Override protected void doGet(HttpServletRequest request,
 * HttpServletResponse response) throws ServletException, IOException { // Check
 * if user is already logged in if (SessionUtil.getAttribute(request, "user") !=
 * null) { redirectBasedOnRole(request, response); return; }
 * 
 * // Check for success message from registration String success = (String)
 * SessionUtil.getAttribute(request, "success"); if (success != null) {
 * request.setAttribute("success", success);
 * SessionUtil.removeAttribute(request, "success"); }
 * 
 * request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request,
 * response); }
 * 
 * @Override protected void doPost(HttpServletRequest req, HttpServletResponse
 * resp) throws ServletException, IOException { String email =
 * req.getParameter("email"); String password = req.getParameter("password");
 * 
 * UserModel user = new UserModel(); user.setEmail(email);
 * user.setPassword(password);
 * 
 * try { Boolean loginStatus = loginService.authenticateUser(user);
 * 
 * if (loginStatus != null && loginStatus) { handleSuccessfulLogin(req, resp,
 * email); } else { handleLoginFailure(req, resp, loginStatus); } } catch
 * (Exception e) { System.err.println("Login error: " + e.getMessage());
 * handleLoginFailure(req, resp, false); } }
 * 
 * private void handleSuccessfulLogin(HttpServletRequest req,
 * HttpServletResponse resp, String email) throws ServletException, IOException
 * { // Set session attributes using SessionUtil SessionUtil.setAttribute(req,
 * "user", email); String role = email.endsWith(ADMIN_EMAIL_SUFFIX) ? ROLE_ADMIN
 * : ROLE_USER; SessionUtil.setAttribute(req, "userRole", role);
 * 
 * // Get and set user ID in session int userId = getUserIdFromDatabase(email);
 * if (userId > 0) { SessionUtil.setAttribute(req, "userId", userId);
 * System.out.println("[LOGIN] User ID set in session: " + userId); }
 * 
 * // Debug output System.out.println("[LOGIN] Successful login for: " + email);
 * System.out.println("[SESSION] Role: " + role);
 * 
 * // Redirect based on role if (ROLE_ADMIN.equals(role)) {
 * resp.sendRedirect(req.getContextPath() + "/admin/dashboard"); } else {
 * resp.sendRedirect(req.getContextPath() + "/home"); } }
 * 
 * private int getUserIdFromDatabase(String email) { try { Connection conn =
 * DbConfig.getDbConnection(); String query =
 * "SELECT user_id FROM users WHERE email = ?";
 * 
 * try (PreparedStatement stmt = conn.prepareStatement(query)) {
 * stmt.setString(1, email);
 * 
 * try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) { int userId =
 * rs.getInt("user_id"); System.out.println("[DEBUG] Found user ID: " + userId);
 * return userId; } } }
 * 
 * // Close the connection conn.close(); } catch (SQLException |
 * ClassNotFoundException e) { System.err.println("Error retrieving user ID: " +
 * e.getMessage()); }
 * 
 * return -1; }
 * 
 * private void handleLoginFailure(HttpServletRequest req, HttpServletResponse
 * resp, Boolean loginStatus) throws ServletException, IOException { String
 * errorMessage = (loginStatus == null) ?
 * "Server error. Please try again later." :
 * "Incorrect email or password. Please try again.";
 * 
 * req.setAttribute("error", errorMessage);
 * req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp);
 * 
 * System.out.println("[LOGIN] Failed login attempt: " + errorMessage); }
 * 
 * private void redirectBasedOnRole(HttpServletRequest req, HttpServletResponse
 * resp) throws ServletException, IOException { String role = (String)
 * SessionUtil.getAttribute(req, "userRole");
 * 
 * if (role == null) { resp.sendRedirect(req.getContextPath() + "/login");
 * return; }
 * 
 * if (ROLE_ADMIN.equals(role)) { resp.sendRedirect(req.getContextPath() +
 * "/admin/dashboard"); } else { resp.sendRedirect(req.getContextPath() +
 * "/home"); } } }
 * 
 */