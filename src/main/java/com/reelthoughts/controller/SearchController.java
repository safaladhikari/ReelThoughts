package com.reelthoughts.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.reelthoughts.config.DbConfig;

@WebServlet("/search")
public class SearchController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String searchQuery = request.getParameter("query");
        String isAjax = request.getParameter("ajax");
        
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            if ("true".equals(isAjax)) {
                response.setContentType("application/json");
                response.getWriter().write("[]");
            } else {
                request.getRequestDispatcher("/WEB-INF/pages/search.jsp").forward(request, response);
            }
            return;
        }

        List<Map<String, Object>> searchResults = new ArrayList<>();
        
        try {
            Connection conn = DbConfig.getDbConnection();
            
            String sql = "SELECT id, title, genre, year, director, imageLink FROM movies " +
                        "WHERE LOWER(title) LIKE LOWER(?) " +
                        "ORDER BY title LIMIT 10";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + searchQuery.trim() + "%";
                stmt.setString(1, searchPattern);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> movie = new HashMap<>();
                        movie.put("id", rs.getInt("id"));
                        movie.put("title", rs.getString("title"));
                        movie.put("genre", rs.getString("genre"));
                        movie.put("year", rs.getInt("year"));
                        movie.put("director", rs.getString("director"));
                        movie.put("imageLink", rs.getString("imageLink"));
                        searchResults.add(movie);
                    }
                }
            }
            
            if ("true".equals(isAjax)) {
                response.setContentType("application/json");
                response.getWriter().write(convertToJson(searchResults));
            } else {
                request.setAttribute("movies", searchResults);
                request.getRequestDispatcher("/WEB-INF/pages/search.jsp").forward(request, response);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            if ("true".equals(isAjax)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Database error occurred\"}");
            } else {
                request.setAttribute("error", "An error occurred while searching movies");
                request.getRequestDispatcher("/WEB-INF/pages/search.jsp").forward(request, response);
            }
        }
    }
    
    private String convertToJson(List<Map<String, Object>> results) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> movie = results.get(i);
            json.append("{");
            json.append("\"id\":").append(movie.get("id")).append(",");
            json.append("\"title\":\"").append(escapeJson(movie.get("title").toString())).append("\",");
            json.append("\"genre\":\"").append(escapeJson(movie.get("genre").toString())).append("\",");
            json.append("\"year\":").append(movie.get("year")).append(",");
            json.append("\"director\":\"").append(escapeJson(movie.get("director").toString())).append("\",");
            json.append("\"imageLink\":\"").append(escapeJson(movie.get("imageLink").toString())).append("\"");
            json.append("}");
            if (i < results.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
    
    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
} 