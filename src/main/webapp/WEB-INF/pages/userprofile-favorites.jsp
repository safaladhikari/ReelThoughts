<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%
    // Dummy data for now – replace with session or database data
    String profileName = "John Doe";
    String profileImageUrl = "https://via.placeholder.com/40"; // Replace with user's actual image URL
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Favorites | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userprofile-favorites.css">
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
                    <h1 class="page-title"> Recent Favorites</h1>
                    <p class="page-subtext">All your personal favorite movies collected in one place.</p>
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
                    String title, director, rating, reelthoughtsRating, imageUrl;
                    public Movie(String title, String director, String rating, String reelthoughtsRating, String imageUrl) {
                        this.title = title;
                        this.director = director;
                        this.rating = rating;
                        this.reelthoughtsRating = reelthoughtsRating;
                        this.imageUrl = imageUrl;
                    }
                }

            List<Movie> recentMovies = Arrays.asList(
                    new Movie("Dune: Part Two", "Denis Villeneuve", "8.7", "9.0", "https://m.media-amazon.com/images/M/MV5BN2FjNmEyNWMtYzM0ZS00NjIyLTg5YzYtYThlMGVjNzE1OGViXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_FMjpg_UX1000_.jpg"),
                    new Movie("Oppenheimer", "Christopher Nolan", "8.6", "8.9", "https://m.media-amazon.com/images/M/MV5BMDBmYTZjNjUtN2M1MS00MTQ2LTk2ODgtNzc2M2QyZGE5NTVjXkEyXkFqcGdeQXVyNzAwMjU2MTY@._V1_.jpg"),
                    new Movie("The Batman", "Matt Reeves", "7.9", "8.2", "https://m.media-amazon.com/images/M/MV5BMDdmMTBiNTYtMDIzNi00NGVlLWIzMDYtZTk3MTQ3NGQxZGEwXkEyXkFqcGdeQXVyMzMwOTU5MDk@._V1_.jpg"),
                    new Movie("Everything Everywhere All at Once", "Daniel Kwan, Daniel Scheinert", "8.1", "8.5", "https://m.media-amazon.com/images/M/MV5BYTdiOTIyZTQtNmQ1OS00NjZlLWIyMTgtYzk5Y2M3ZDVmMDk1XkEyXkFqcGdeQXVyMTAzMDg4NzU0._V1_FMjpg_UX1000_.jpg"),
                    new Movie("Top Gun: Maverick", "Joseph Kosinski", "8.3", "8.6", "https://m.media-amazon.com/images/M/MV5BZWYzOGEwNTgtNWU3NS00ZTQ0LWJkODUtMmVhMjIwMjA1ZmQwXkEyXkFqcGdeQXVyMjkwOTAyMDU@._V1_FMjpg_UX1000_.jpg"),
                    new Movie("Avatar: The Way of Water", "James Cameron", "7.6", "7.9", "https://m.media-amazon.com/images/M/MV5BYjhiNjBlODctY2ZiOC00YjVlLWFlNzAtNTVhNzM1YjI1NzMxXkEyXkFqcGdeQXVyMjQxNTE1MDA@._V1_FMjpg_UX1000_.jpg"),
                    new Movie("Black Panther: Wakanda Forever", "Ryan Coogler", "7.2", "7.5", "https://m.media-amazon.com/images/M/MV5BNTM4NjIxNmEtYWE5NS00NDczLTkyNWQtYThhNmQyZGQzMjM0XkEyXkFqcGdeQXVyODk4OTc3MTY@._V1_FMjpg_UX1000_.jpg"),
                    new Movie("The Super Mario Bros. Movie", "Aaron Horvath, Michael Jelenic", "7.0", "7.3", "https://m.media-amazon.com/images/M/MV5BOTJhNzlmNzctNTU5Yy00N2YwLThhMjQtZDM0YjEzN2Y0ZjNhXkEyXkFqcGdeQXVyMTEwMTQ4MzU5._V1_FMjpg_UX1000_.jpg")
                );

                for (Movie movie : recentMovies) {
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
