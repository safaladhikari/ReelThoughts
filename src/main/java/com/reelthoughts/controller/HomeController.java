package com.reelthoughts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.reelthoughts.config.DbConfig;

/**
 * @author Safal Adhikari
 * Servlet implementation class Home
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/home" })
public class HomeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Fetch movies from database for the homepage
		List<Map<String, Object>> movies = new ArrayList<>();
		
		try {
			Connection conn = DbConfig.getDbConnection();
			
			try (Statement stmt = conn.createStatement()) {
				String query = "SELECT id, title, genre, year, director, imageLink FROM movies LIMIT 8";
				
				try (ResultSet rs = stmt.executeQuery(query)) {
					while (rs.next()) {
						Map<String, Object> movie = new HashMap<>();
						int movieId = rs.getInt("id");
						movie.put("id", movieId);
						movie.put("title", rs.getString("title"));
						movie.put("genre", rs.getString("genre"));
						movie.put("year", rs.getInt("year"));
						movie.put("director", rs.getString("director"));
						movie.put("imageLink", rs.getString("imageLink"));
						movies.add(movie);
						System.out.println("[DEBUG] Loaded movie with ID: " + movieId + ", Title: " + rs.getString("title"));
					}
				}
			}
			
			// Close the connection
			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Error fetching movies for homepage: " + e.getMessage());
		}
		
		// Set the movies as a request attribute
		request.setAttribute("movies", movies);
		
		// Forward to the JSP page
		request.getRequestDispatcher("/WEB-INF/pages/home.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
