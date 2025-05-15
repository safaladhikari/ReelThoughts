<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%
    String profileName = "John Doe";
    String profileImageUrl = "https://via.placeholder.com/40";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Ratings | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userprofile-review.css">
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
                    <h1 class="page-title"> Your Reviews</h1>
                    <p class="page-subtext">Here are all your ratings and reviews for the movies you've watched.</p>
                </div>
                <div class="user-profile-box">
                    <img src="${pageContext.request.contextPath}/resources/images/akira.jpg" alt="Profile">
                    <span>Safal Adhikari</span>
                </div>
            </div>
        </div>

        <div class="movie-grid">
            <%
                class Movie {
                    String title, director, rating, hint, dateRated, imageUrl;
                    public Movie(String title, String director, String rating, String hint, String dateRated, String imageUrl) {
                        this.title = title;
                        this.director = director;
                        this.rating = rating;
                        this.hint = hint;
                        this.dateRated = dateRated;
                        this.imageUrl = imageUrl;
                    }
                }

                List<Movie> ratedMovies = Arrays.asList(
                    new Movie("Top Gun: Maverick", "Joseph Kosinski", "8.3", "Will Recommend", "2025-04-02", "https://m.media-amazon.com/images/M/MV5BZWYzOGEwNTgtNWU3NS00ZTQ0LWJkODUtMmVhMjIwMjA1ZmQwXkEyXkFqcGdeQXVyMjkwOTAyMDU@._V1_FMjpg_UX1000_.jpg"),
                    new Movie("Avatar: The Way of Water", "James Cameron", "7.6", "Will Recommend", "2025-03-28", "https://m.media-amazon.com/images/M/MV5BYjhiNjBlODctY2ZiOC00YjVlLWFlNzAtNTVhNzM1YjI1NzMxXkEyXkFqcGdeQXVyMjQxNTE1MDA@._V1_FMjpg_UX1000_.jpg"),
                    new Movie("Black Panther: Wakanda Forever", "Ryan Coogler", "7.2", "Could Be Worthwhile", "2025-03-10", "https://m.media-amazon.com/images/M/MV5BNTM4NjIxNmEtYWE5NS00NDczLTkyNWQtYThhNmQyZGQzMjM0XkEyXkFqcGdeQXVyODk4OTc3MTY@._V1_FMjpg_UX1000_.jpg"),
                    new Movie("The Super Mario Bros. Movie", "Aaron Horvath, Michael Jelenic", "7.0", "Could Be Worthwhile", "2025-02-25", "https://m.media-amazon.com/images/M/MV5BOTJhNzlmNzctNTU5Yy00N2YwLThhMjQtZDM0YjEzN2Y0ZjNhXkEyXkFqcGdeQXVyMTEwMTQ4MzU5._V1_FMjpg_UX1000_.jpg")
                );

                for (Movie movie : ratedMovies) {
            %>
            <div class="movie-card">
                <img src="<%= movie.imageUrl %>" alt="<%= movie.title %>" class="movie-poster">
                <div class="movie-info">
                    <h3 class="movie-title"><%= movie.title %></h3>
                    <p class="movie-director">Directed by: <%= movie.director %></p>
                    <p class="review-title">"A thrilling masterpiece!"</p>
                    <div class="rating-container">
                        <p class="movie-rating">Your Rating: <%= movie.rating %>/10</p>
                        <p class="rating-hint"><%= movie.hint %></p>
                        <p class="date-rated">Rated on: <%= movie.dateRated %></p>
                    </div>
                    <div class="review-buttons">
                        <button class="btn read-btn">Read Full Review</button>
                        <button class="btn edit-btn">Edit Review</button>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>
