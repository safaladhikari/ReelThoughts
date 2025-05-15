<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.temporal.ChronoUnit" %>

<%
    class Movie {
        String title, director, imageUrl;
        LocalDate addedDate;

        public Movie(String title, String director, String imageUrl, LocalDate addedDate) {
            this.title = title;
            this.director = director;
            this.imageUrl = imageUrl;
            this.addedDate = addedDate;
        }
    }

    List<Movie> watchlist = Arrays.asList(
    	new Movie("Spirited Away", "Hayao Miyazaki", "https://image.tmdb.org/t/p/w500/dL11DBPcRhWWnJcFXl9A07MrqTI.jpg", LocalDate.of(2025, 1, 25)),
        new Movie("The Matrix", "Lana & Lilly Wachowski", "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg", LocalDate.of(2025, 1, 5)),
        new Movie("Fight Club", "David Fincher", "https://image.tmdb.org/t/p/w500/bptfVGEQuv6vDTIMVCHjJ9Dz8PX.jpg", LocalDate.of(2025, 2, 1)),
        new Movie("Parasite", "Bong Joon-ho", "https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg", LocalDate.of(2025, 3, 1)),
        new Movie("The Dark Knight", "Christopher Nolan", "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", LocalDate.of(2024, 12, 15)),
        new Movie("Pulp Fiction", "Quentin Tarantino", "https://image.tmdb.org/t/p/w500/dM2w364MScsjFf8pfMbaWUcWrR.jpg", LocalDate.of(2025, 2, 20)),
        new Movie("Spirited Away", "Hayao Miyazaki", "https://image.tmdb.org/t/p/w500/dL11DBPcRhWWnJcFXl9A07MrqTI.jpg", LocalDate.of(2025, 1, 25)),
        new Movie("Fight Club", "David Fincher", "https://image.tmdb.org/t/p/w500/bptfVGEQuv6vDTIMVCHjJ9Dz8PX.jpg", LocalDate.of(2025, 2, 1))
    );

    LocalDate today = LocalDate.now();
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Watchlist | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userprofile-watchlist.css">
</head>
<body>
    <header>
        <nav class="navbar">
            <div class="logo">ReelThoughts.</div>
            <div class="search-bar">
                <input type="text" placeholder="Search movies...">
            </div>
            <div class="nav-links">
                <a href="recentreleases.jsp">Recent Releases</a>
                <a href="popular.jsp">Popular</a>
                <a href="news.jsp">News</a>
                <a href="userprofile.jsp"><i class="fa-regular fa-user"></i></a>
            </div>
        </nav>
    </header>

    <div class="recent-releases-container">
        <div class="page-header">
            <div class="header-row">
                <div>
                    <h1 class="page-title">Your Watchlist</h1>
                    <p class="page-subtext">All the movies you want to watch now stacked up in one place.</p>
                </div>
                <div class="user-profile-box">
                    <img src="${pageContext.request.contextPath}/resources/images/akira.jpg" alt="Profile">
                    <span>Safal Adhikari</span>
                </div>
            </div>
        </div>

        <div class="watchlist-grid">
            <%
                for (Movie movie : watchlist) {
                    long daysInList = ChronoUnit.DAYS.between(movie.addedDate, today);
            %>
                <div class="movie-card">
                    <img src="<%= movie.imageUrl %>" alt="<%= movie.title %>" class="movie-poster">
                    <div class="movie-info">
                        <h3 class="movie-title"><%= movie.title %></h3>
                        <p class="movie-director">Director: <%= movie.director %></p>
                        <p class="watchlist-added">Added on: <%= movie.addedDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")) %></p>
                        <p class="watchlist-days">In watchlist for <%= daysInList %> days</p>
                        <div class="watchlist-actions">
                            
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
