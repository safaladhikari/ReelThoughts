package com.reelthoughts.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

        // Debug output
        System.out.println("[LOGIN] Successful login for: " + email);
        System.out.println("[SESSION] Role: " + role);

        // Redirect based on role
        if (ROLE_ADMIN.equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
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
 * package com.islington.controller;
 * 
 * import java.io.IOException; import jakarta.servlet.ServletException; import
 * jakarta.servlet.annotation.WebServlet; import
 * jakarta.servlet.http.HttpServlet; import
 * jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse;
 * 
 * import com.islington.service.LoginService; import
 * com.islington.util.SessionUtil; import com.islington.util.CookieUtil; import
 * com.islington.model.UserModel;
 * 
 * @WebServlet(asyncSupported = true, urlPatterns = { "/login" }) public class
 * LoginController extends HttpServlet { private static final long
 * serialVersionUID = 1L; private static final String ADMIN_EMAIL_SUFFIX =
 * "@adminreelthoughts.com"; private static final String ROLE_ADMIN = "admin";
 * private static final String ROLE_USER = "user"; private static final int
 * COOKIE_MAX_AGE = 60 * 30; // 30 minutes
 * 
 * private final LoginService loginService;
 * 
 * public LoginController() { this.loginService = new LoginService(); }
 * 
 * @Override protected void doGet(HttpServletRequest request,
 * HttpServletResponse response) throws ServletException, IOException { // Check
 * if user is already logged in if (SessionUtil.getAttribute(request, "user") !=
 * null) { redirectBasedOnRole(request, response); return; }
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
 * Boolean loginStatus = loginService.authenticateUser(user);
 * 
 * if (loginStatus != null && loginStatus) { handleSuccessfulLogin(req, resp,
 * email); } else { handleLoginFailure(req, resp, loginStatus); } }
 * 
 * private void handleSuccessfulLogin(HttpServletRequest req,
 * HttpServletResponse resp, String email) throws ServletException, IOException
 * { SessionUtil.setAttribute(req, "user", email);
 * 
 * // Derive role based on email and set session attribute String role =
 * email.endsWith(ADMIN_EMAIL_SUFFIX) ? ROLE_ADMIN : ROLE_USER;
 * SessionUtil.setAttribute(req, "userRole", role);
 * 
 * // Debug output System.out.println("[LOGIN] Successful login for: " + email);
 * System.out.println("[SESSION] ID: " + session.getId() + " | Role: " + role);
 * 
 * // Redirect based on role if (ROLE_ADMIN.equals(role)) { CookieUtil.addCookie(resp, "role", ROLE_ADMIN,
 * COOKIE_MAX_AGE);
 * req.getRequestDispatcher("/WEB-INF/pages/admindashboard.jsp").forward(req,
 * resp); } else { CookieUtil.addCookie(resp, "role", ROLE_USER,
 * COOKIE_MAX_AGE);
 * req.getRequestDispatcher("/WEB-INF/pages/home.jsp").forward(req, resp); } }
 * 
 * private void handleLoginFailure(HttpServletRequest req, HttpServletResponse
 * resp, Boolean loginStatus) throws ServletException, IOException { String
 * errorMessage = (loginStatus == null) ?
 * "Server maintenance in progress. Please try again later." :
 * "Incorrect email or password. Please try again.";
 * 
 * req.setAttribute("error", errorMessage);
 * req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp); }
 * 
 * private void redirectBasedOnRole(HttpServletRequest req, HttpServletResponse
 * resp) throws ServletException, IOException { String role = (String)
 * SessionUtil.getAttribute(req, "userRole");
 * 
 * if (ROLE_ADMIN.equals(role)) {
 * req.getRequestDispatcher("/WEB-INF/pages/admindashboard.jsp").forward(req,
 * resp); } else {
 * req.getRequestDispatcher("/WEB-INF/pages/home.jsp").forward(req, resp); } } }
 * 
 * 
 * 
 */



/*this works 9:21 PM 21st april
 * 
 * 
 * 
 * package com.islington.controller;
 * 
 * import java.io.IOException; import jakarta.servlet.ServletException; import
 * jakarta.servlet.annotation.WebServlet; import
 * jakarta.servlet.http.HttpServlet; import
 * jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse;
 * 
 * import com.islington.service.LoginService; import
 * com.islington.util.SessionUtil; import com.islington.util.CookieUtil; import
 * com.islington.model.UserModel;
 * 
 * @WebServlet(asyncSupported = true, urlPatterns = { "/login" }) public class
 * LoginController extends HttpServlet { private static final long
 * serialVersionUID = 1L; private static final String ADMIN_EMAIL_SUFFIX =
 * "@adminreelthoughts.com"; private static final String ROLE_ADMIN = "admin";
 * private static final String ROLE_USER = "user"; private static final int
 * COOKIE_MAX_AGE = 60 * 30; // 30 minutes
 * 
 * private final LoginService loginService;
 * 
 * public LoginController() { this.loginService = new LoginService(); }
 * 
 * @Override protected void doGet(HttpServletRequest request,
 * HttpServletResponse response) throws ServletException, IOException { // Check
 * if user is already logged in if (SessionUtil.getAttribute(request, "user") !=
 * null) { redirectBasedOnRole(request, response); return; }
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
 * Boolean loginStatus = loginService.authenticateUser(user);
 * 
 * if (loginStatus != null && loginStatus) { handleSuccessfulLogin(req, resp,
 * email); } else { handleLoginFailure(req, resp, loginStatus); } }
 * 
 * private void handleSuccessfulLogin(HttpServletRequest req,
 * HttpServletResponse resp, String email) throws IOException {
 * SessionUtil.setAttribute(req, "user", email);
 * 
 * // Derive role based on email and set session attribute String role =
 * email.endsWith(ADMIN_EMAIL_SUFFIX) ? ROLE_ADMIN : ROLE_USER;
 * SessionUtil.setAttribute(req, "userRole", role);
 * 
 * if (ROLE_ADMIN.equals(role)) { CookieUtil.addCookie(resp, "role", ROLE_ADMIN,
 * COOKIE_MAX_AGE); resp.sendRedirect(req.getContextPath() +
 * "/admin/admindashboard.jsp"); } else { CookieUtil.addCookie(resp, "role",
 * ROLE_USER, COOKIE_MAX_AGE); resp.sendRedirect(req.getContextPath() +
 * "/home.jsp"); } }
 * 
 * private void handleLoginFailure(HttpServletRequest req, HttpServletResponse
 * resp, Boolean loginStatus) throws ServletException, IOException { String
 * errorMessage = (loginStatus == null) ?
 * "Server maintenance in progress. Please try again later." :
 * "Incorrect email or password. Please try again.";
 * 
 * req.setAttribute("error", errorMessage);
 * req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp); }
 * 
 * private void redirectBasedOnRole(HttpServletRequest req, HttpServletResponse
 * resp) throws IOException { String role = (String)
 * SessionUtil.getAttribute(req, "userRole");
 * 
 * if (ROLE_ADMIN.equals(role)) { resp.sendRedirect(req.getContextPath() +
 * "/admin/admindashboard.jsp"); } else { resp.sendRedirect(req.getContextPath()
 * + "/home.jsp"); } } }
 * 
 * 
 * package com.islington.controller;
 * 
 * import com.islington.model.UserModel; import
 * com.islington.service.LoginService; import com.islington.util.CookieUtil;
 * import com.islington.util.SessionUtil;
 * 
 * import jakarta.servlet.ServletException; import
 * jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*;
 * 
 * import java.io.IOException;
 * 
 *//**
	 * LoginController handles user authentication and role-based routing
	 *//*
		 * @WebServlet(asyncSupported = true, urlPatterns = { "/login" }) public class
		 * LoginController extends HttpServlet { private static final long
		 * serialVersionUID = 1L; private static final String ADMIN_EMAIL_SUFFIX =
		 * "@adminreelthoughts.com"; private static final String ROLE_ADMIN = "admin";
		 * private static final String ROLE_USER = "user"; private static final int
		 * COOKIE_MAX_AGE = 60 * 30; // 30 minutes
		 * 
		 * private final LoginService loginService;
		 * 
		 * public LoginController() { this.loginService = new LoginService(); }
		 * 
		 * @Override protected void doGet(HttpServletRequest request,
		 * HttpServletResponse response) throws ServletException, IOException { // Check
		 * if user is already logged in if (SessionUtil.getAttribute(request, "user") !=
		 * null) { redirectBasedOnRole(request, response); return; }
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
		 * Boolean loginStatus = loginService.authenticateUser(user);
		 * 
		 * if (loginStatus != null && loginStatus) { handleSuccessfulLogin(req, resp,
		 * email); } else { handleLoginFailure(req, resp, loginStatus); } }
		 * 
		 * private void handleSuccessfulLogin(HttpServletRequest req,
		 * HttpServletResponse resp, String email) throws IOException {
		 * SessionUtil.setAttribute(req, "user", email);
		 * 
		 * if (isAdminEmail(email)) { CookieUtil.addCookie(resp, "role", ROLE_ADMIN,
		 * COOKIE_MAX_AGE); resp.sendRedirect(req.getContextPath() +
		 * "/admin/admindashboard.jsp"); } else { CookieUtil.addCookie(resp, "role",
		 * ROLE_USER, COOKIE_MAX_AGE); resp.sendRedirect(req.getContextPath() +
		 * "/home.jsp"); } }
		 * 
		 * private void handleLoginFailure(HttpServletRequest req, HttpServletResponse
		 * resp, Boolean loginStatus) throws ServletException, IOException { String
		 * errorMessage = (loginStatus == null) ?
		 * "Server maintenance in progress. Please try again later." :
		 * "Incorrect email or password. Please try again.";
		 * 
		 * req.setAttribute("error", errorMessage);
		 * req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, resp); }
		 * 
		 * private boolean isAdminEmail(String email) { return email != null &&
		 * email.toLowerCase().endsWith(ADMIN_EMAIL_SUFFIX); }
		 * 
		 * private void redirectBasedOnRole(HttpServletRequest req, HttpServletResponse
		 * resp) throws IOException { String email = (String)
		 * SessionUtil.getAttribute(req, "user"); if (isAdminEmail(email)) {
		 * resp.sendRedirect(req.getContextPath() + "/admin/admindashboard.jsp"); } else
		 * { resp.sendRedirect(req.getContextPath() + "/home.jsp"); } } }
		 */