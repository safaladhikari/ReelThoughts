<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    Map<String, Object> user = (Map<String, Object>) request.getAttribute("user");
    List<Map<String, Object>> favoriteMovies = (List<Map<String, Object>>) request.getAttribute("favoriteMovies");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Favorites | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userprofile-favorites.css">
    <style>
        /* Override header.css search bar styles */
        .search-bar {
            position: relative;
            flex: 1;
            max-width: 600px !important;
            margin: 0 2rem;
        }

        .search-bar form {
            display: flex;
            align-items: center;
            background: #fff;
            border-radius: 25px;
            padding: 0.5rem 1rem;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            height: 40px;
        }

        .search-bar input {
            flex: 1;
            border: none;
            outline: none;
            padding: 0.5rem;
            font-size: 1rem;
            background: transparent;
            height: 100%;
            width: 100%;
        }

        /* Movie Grid Layout */
        .movie-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            padding: 20px;
        }

        .movie-card {
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            transition: transform 0.3s ease;
            height: 100%;
            display: flex;
            flex-direction: column;
            cursor: pointer;
        }

        .movie-card:hover {
            transform: translateY(-5px);
        }

        .movie-poster {
            position: relative;
            width: 100%;
            padding-top: 150%; /* 2:3 aspect ratio */
            overflow: hidden;
        }

        .movie-poster img {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .movie-info {
            padding: 15px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
        }

        .movie-info h3 {
            margin: 0 0 10px 0;
            font-size: 1.1em;
            line-height: 1.3;
        }

        .meta {
            color: #666;
            font-size: 0.9em;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <header>
        <nav class="navbar">
            <div class="logo">ReelThoughts.</div>
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

    <div class="recent-releases-container">
        <div class="page-header">
            <h1 class="page-title">My Favorites</h1>
            <p class="page-subtext">All your personal favorite movies collected in one place.</p>
        </div>

        <div class="movie-grid">
            <% if (favoriteMovies != null && !favoriteMovies.isEmpty()) {
                for (Map<String, Object> movie : favoriteMovies) {
                    Object movieIdObj = movie.get("id");
                    String movieId = (movieIdObj != null) ? movieIdObj.toString() : "null";
            %>
                <div class="movie-card" onclick="window.location.href='${pageContext.request.contextPath}/movie-details?id=<%= movieId %>'">
                    <div class="movie-poster">
                        <img src="<%= movie.get("imageLink") %>" alt="<%= movie.get("title") %>">
                    </div>
                    <div class="movie-info">
                        <h3 class="movie-title"><%= movie.get("title") %></h3>
                        <p class="meta">Director: <%= movie.get("director") %></p>
                        <p class="meta"><%= movie.get("year") %> • <%= movie.get("genre") %></p>
                    </div>
                </div>
            <% 
                }
            } else {
            %>
                <div class="no-movies">
                    <p>No favorite movies yet.</p>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
