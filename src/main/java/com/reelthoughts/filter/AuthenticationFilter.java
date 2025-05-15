package com.reelthoughts.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import com.reelthoughts.util.SessionUtil;

@WebFilter(asyncSupported = true, urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    private static final String LOGIN = "/login";
    private static final String HOME = "/home";
    private static final String REGISTER = "/register";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic, if required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        System.out.println("[DEBUG] AuthenticationFilter: Processing URI: " + uri);
        System.out.println("[DEBUG] Context Path: " + contextPath);
        System.out.println("[DEBUG] Session ID: " + (req.getSession(false) != null ? req.getSession(false).getId() : "No session"));

        // Handle root URL - redirect to login
        if (uri.equals(contextPath) || uri.equals(contextPath + "/")) {
            System.out.println("[DEBUG] Root URL detected, redirecting to login");
            res.sendRedirect(contextPath + LOGIN);
            return;
        }

        // Allow access to login, register, home, and static resources without authentication
        if (uri.contains(LOGIN) || uri.contains(HOME) || uri.contains(REGISTER) ||
            uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/")) {
            System.out.println("[DEBUG] Allowing access to public resource: " + uri);
            chain.doFilter(request, response);
            return;
        }

        // Check if the user is logged in
        String userRole = (String) SessionUtil.getAttribute(req, "userRole");
        String userEmail = (String) SessionUtil.getAttribute(req, "user");

        System.out.println("[DEBUG] User Role: " + userRole);
        System.out.println("[DEBUG] User Email: " + userEmail);
        System.out.println("[DEBUG] Session exists: " + (req.getSession(false) != null));

        // If not logged in, redirect to login
        if (userRole == null || userEmail == null) {
            System.out.println("[DEBUG] User not logged in, redirecting to login");
            res.sendRedirect(contextPath + LOGIN);
            return;
        }

        // Handle admin access
        if (uri.contains("/admin/") && !"admin".equals(userRole)) {
            System.out.println("[DEBUG] Non-admin user attempting to access admin area");
            res.sendRedirect(contextPath + HOME);
            return;
        }

        // Continue with the request
        System.out.println("[DEBUG] Allowing access to protected resource: " + uri);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic, if required
    }
}






/*
 * package com.islington.filter;
 * 
 * import jakarta.servlet.Filter; import jakarta.servlet.FilterChain; import
 * jakarta.servlet.FilterConfig; import jakarta.servlet.ServletException; import
 * jakarta.servlet.ServletRequest; import jakarta.servlet.ServletResponse;
 * import jakarta.servlet.annotation.WebFilter; import
 * jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse;
 * 
 * import java.io.IOException;
 * 
 * import com.islington.util.SessionUtil;
 * 
 * @WebFilter(asyncSupported = true, urlPatterns = "/*") public class
 * AuthenticationFilter implements Filter {
 * 
 * private static final String LOGIN = "/login"; private static final String
 * HOME = "/home"; private static final String ADMIN_DASHBOARD =
 * "/admin/admindashboard.jsp";
 * 
 * @Override public void init(FilterConfig filterConfig) throws ServletException
 * { // Initialization logic, if required }
 * 
 * @Override public void doFilter(ServletRequest request, ServletResponse
 * response, FilterChain chain) throws IOException, ServletException {
 * 
 * HttpServletRequest req = (HttpServletRequest) request; HttpServletResponse
 * res = (HttpServletResponse) response;
 * 
 * String uri = req.getRequestURI();
 * 
 * // Allow access to login, register, and home pages if (uri.endsWith(LOGIN) ||
 * uri.endsWith(HOME)) { chain.doFilter(request, response); return; }
 * 
 * // Check if the user is logged in and has a role set in the session String
 * userRole = (String) SessionUtil.getAttribute(req, "userRole");
 * 
 * if (userRole == null) { // Redirect to login if the user is not logged in
 * res.sendRedirect(req.getContextPath() + LOGIN); return; }
 * 
 * if (uri.endsWith(ADMIN_DASHBOARD) && !"admin".equals(userRole)) { // Redirect
 * non-admin users trying to access admin dashboard
 * res.sendRedirect(req.getContextPath() + HOME); return; }
 * 
 * chain.doFilter(request, response); // Continue with the request if the user
 * is authorized }
 * 
 * @Override public void destroy() { // Cleanup logic, if required } }
 */