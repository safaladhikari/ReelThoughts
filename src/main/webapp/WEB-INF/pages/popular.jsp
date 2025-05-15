<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Popular Movies | MovieHub</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recentreleases.css">
    <style>
        .rating-container {
            margin-top: 10px;
        }
        .movie-rating {
            margin-bottom: 5px;
        }
        .reelthoughts-rating {
            color: #004000;
            font-weight: bold;
        }
    </style>
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
            <h1 class="page-title">Popular Movies</h1>
            <p class="page-subtext">Check out the most talked about movies in the platform.</p>
        </div>
        
        <div class="movie-grid">
            <%
            class Movie {
                String title;
                String director;
                String rating;
                String reelthoughtsRating;
                String imageUrl;

                public Movie(String title, String director, String rating, String reelthoughtsRating, String imageUrl) {
                    this.title = title;
                    this.director = director;
                    this.rating = rating;
                    this.reelthoughtsRating = reelthoughtsRating;
                    this.imageUrl = imageUrl;
                }
            }

            List<Movie> popularMovies = Arrays.asList(
                new Movie("Inception", "Christopher Nolan", "8.8", "9.1", "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_FMjpg_UX1000_.jpg"),
                new Movie("The Shawshank Redemption", "Frank Darabont", "9.3", "9.5", "https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NmNlLWJiNDMtZDViZWM2MzIxZDY2XkEyXkFqcGdeQXVyNjAwNDUxODI@._V1_.jpg"),
                new Movie("The Dark Knight", "Christopher Nolan", "9.0", "9.3", "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg"),
                new Movie("Pulp Fiction", "Quentin Tarantino", "8.9", "9.0", "https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg"),
                new Movie("Fight Club", "David Fincher", "8.8", "8.9", "https://m.media-amazon.com/images/M/MV5BNDIzNDU0YzEtYzE5Ni00ZjlkLTk5ZjgtNjM3NWE4YzA3Nzk3XkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_.jpg"),
                new Movie("Forrest Gump", "Robert Zemeckis", "8.8", "8.7", "https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg"),
                new Movie("The Matrix", "Lana & Lilly Wachowski", "8.7", "8.8", "https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg"),
                new Movie("Parasite", "Bong Joon Ho", "8.5", "8.9", "https://m.media-amazon.com/images/M/MV5BYWZjMjk3ZTItODQ2ZC00NTY5LWE0ZDYtZTI3MjcwN2Q5NTVkXkEyXkFqcGdeQXVyODk4OTc3MTY@._V1_.jpg")
            );

            for (Movie movie : popularMovies) {
            %>
                <div class="movie-card">
                    <img src="<%= movie.imageUrl %>" alt="<%= movie.title %>" class="movie-poster">
                    <div class="movie-info">
                        <h3 class="movie-title"><%= movie.title %></h3>
                        <p class="movie-director">Director: <%= movie.director %></p>
                        <div class="rating-container">
                            <p class="movie-rating">IMDb: ⭐ <%= movie.rating %></p>
                            <p class="movie-rating">ReelThoughts: ⭐ <%= movie.reelthoughtsRating %></p>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>