<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%
    Map<String, Object> user = (Map<String, Object>) request.getAttribute("user");
    String error = (String) request.getAttribute("error");
    List<Map<String, Object>> favoriteMovies = (List<Map<String, Object>>) request.getAttribute("favoriteMovies");
    List<Map<String, Object>> reviewedMovies = (List<Map<String, Object>>) request.getAttribute("reviewedMovies");
    List<Map<String, Object>> watchlistMovies = (List<Map<String, Object>>) request.getAttribute("watchlistMovies");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ReelThoughts Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userprofile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css">
    <!-- Font Awesome for social icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <script src="https://kit.fontawesome.com/your-kit-code.js" crossorigin="anonymous"></script>
</head>
<body>
    <header>
        <nav class="navbar">
            <nav><a class="logo" href="${pageContext.request.contextPath}/home">ReelThoughts.</a></nav>
            <div class="search-bar">
                <form action="${pageContext.request.contextPath}/search" method="GET" onsubmit="return validateSearch(this);">
                    <input type="text" name="query" placeholder="Search movies..." value="${param.query}">
                </form>
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/recentreleases">Recent Releases</a>
                <a href="${pageContext.request.contextPath}/popular">Popular</a>
                <a href="${pageContext.request.contextPath}/news">News</a>
                <a href="${pageContext.request.contextPath}/profile"><i class="fa-regular fa-user"></i></a>
            </div>
        </nav>
    </header>
    
    <div class="container">
        <!-- Main Content - Now on the left -->
        <main class="main-content">
            <section class="section">
                <h2>Recent Favorites
                    <a href="${pageContext.request.contextPath}/favorites" class="view-more">View All →</a>
                </h2>
                <div class="films-container">
                    <% if (favoriteMovies != null && !favoriteMovies.isEmpty()) {
                        for (Map<String, Object> movie : favoriteMovies) {
                            Object movieIdObj = movie.get("id");
                            String movieId = (movieIdObj != null) ? movieIdObj.toString() : "null";
                    %>
                        <a href="${pageContext.request.contextPath}/movie-details?id=<%= movieId %>" class="film-card">
                            <div class="film-poster">
                                <img src="<%= movie.get("imageLink") %>" alt="<%= movie.get("title") %>">
                            </div>
                            <div class="film-title"><%= movie.get("title") %></div>
                            <div class="film-year"><%= movie.get("year") %></div>
                        </a>
                    <% 
                        }
                    } else {
                    %>
                        <div class="no-movies">
                            <p>No favorite movies yet.</p>
                        </div>
                    <% } %>
                </div>
            </section>
            
            <section class="section">
                <h2>Recent Reviews
                    <a href="${pageContext.request.contextPath}/userprofilereview" class="view-more">View All →</a>
                </h2>
                <div class="films-container">
                    <% if (reviewedMovies != null && !reviewedMovies.isEmpty()) {
                        for (Map<String, Object> movie : reviewedMovies) {
                            Object movieIdObj = movie.get("id");
                            String movieId = (movieIdObj != null) ? movieIdObj.toString() : "null";
                    %>
                        <a href="${pageContext.request.contextPath}/movie-details?id=<%= movieId %>" class="film-card">
                            <div class="film-poster">
                                <img src="<%= movie.get("imageLink") %>" alt="<%= movie.get("title") %>">
                            </div>
                            <div class="film-title"><%= movie.get("title") %></div>
                            <div class="film-year"><%= movie.get("year") %></div>
                        </a>
                    <% 
                        }
                    } else {
                    %>
                        <div class="no-movies">
                            <p>No reviews yet.</p>
                        </div>
                    <% } %>
                </div>
            </section>
            
            <section class="section">
                <h2>Watch List
                    <a href="${pageContext.request.contextPath}/watchlist" class="view-more"></a>
                </h2>
                <div class="films-container">
                    <% if (watchlistMovies != null && !watchlistMovies.isEmpty()) {
                        for (Map<String, Object> movie : watchlistMovies) {
                            Object movieIdObj = movie.get("id");
                            String movieId = (movieIdObj != null) ? movieIdObj.toString() : "null";
                    %>
                        <a href="${pageContext.request.contextPath}/movie-details?id=<%= movieId %>" class="film-card">
                            <div class="film-poster">
                                <img src="<%= movie.get("imageLink") %>" alt="<%= movie.get("title") %>">
                            </div>
                            <div class="film-title"><%= movie.get("title") %></div>
                            <div class="film-year"><%= movie.get("year") %></div>
                        </a>
                    <% 
                        }
                    } else {
                    %>
                        <div class="no-movies">
                            <p>No movies in watchlist yet.</p>
                        </div>
                    <% } %>
                </div>
            </section>
        </main>
        
        <!-- Sidebar - Now on the right -->
        <aside class="sidebar">
            <div class="profile-header">
                <div class="profile-pic">
                    <% 
                        String profilePicPath = (user != null && user.get("profilePicture") != null) ? (String)user.get("profilePicture") : "";
                        System.out.println("[DEBUG JSP] Profile Picture Path from user object: " + profilePicPath);

                        if (user != null && !profilePicPath.isEmpty()) {
                    %>
                        <img src="${pageContext.request.contextPath}<%= profilePicPath %>" alt="Profile" style="width:100%; height:100%; object-fit:cover;">
                    <% } else {
                    %>
                        <i class="fas fa-user-circle" style="font-size: 150px; color: #666;"></i>
                    <% } %>
                </div>
                <div class="profile-name" style="color: white !important;"><%= user != null ? user.get("firstName") + " " + user.get("lastName") : "" %></div>
            </div>
            <ul>
                <li><a href="${pageContext.request.contextPath}/edit-profile">Edit Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/userprofilereview">Reviews</a></li>
                <li><a href="${pageContext.request.contextPath}//favorites">Favorites</a></li>
                <li><a href="${pageContext.request.contextPath}/userprofilerating">Ratings</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
            </ul>
        </aside>
    </div>

    <!-- Footer Section -->
    <footer>
        <div class="footer-container">
            <div class="footer-about">
                <div class="footer-logo">ReelThoughts.</div>
                <p>Your ultimate movie companion for reviews, ratings, and recommendations.</p>
            </div>
            <div class="footer-contact">
                <h3>Contact Us</h3>
                <p>Kamalpokhari, Kathmandu</p>
                <p>Phone: +977 9841XXXXXX</p>
            </div>
            <div class="footer-social-links">
                <h3>Follow Us</h3>
                <div class="footer-social">
                    <a href="#" aria-label="Facebook"><i class="fab fa-facebook"></i></a>
                    <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                    <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
                    <a href="#" aria-label="YouTube"><i class="fab fa-youtube"></i></a>
                </div>
            </div>
        </div>
        <div class="copyright">
            <p>&copy; 2025 ReelThoughts. All rights reserved.</p>
        </div>
    </footer>

    <script>
        function validateSearch(form) {
            const query = form.querySelector('input[name="query"]').value.trim();
            if (query.length < 2) {
                alert('Please enter at least 2 characters to search');
                return false;
            }
            return true;
        }
    </script>
</body>
</html>