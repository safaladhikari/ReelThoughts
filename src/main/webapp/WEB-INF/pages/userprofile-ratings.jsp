<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Ratings | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recentreleases.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css">
    <style>
        body {
            background-color: #141414;
            color: #fff;
            font-family: 'Helvetica Neue', Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        .ratings-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        .ratings-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-top: 2rem;
        }
        .rating-card {
            background: #181818;
            border-radius: 4px;
            overflow: hidden;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            position: relative;
        }
        .rating-card:hover {
            transform: scale(1.05);
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        .movie-poster {
            width: 100%;
            aspect-ratio: 2/3;
            object-fit: cover;
        }
        .rating-info {
            padding: 1rem;
            background: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.7) 50%, transparent 100%);
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
        }
        .movie-title {
            font-size: 1rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #fff;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .rating-value {
            color: #FFD700;
            font-weight: 600;
            font-size: 1.1rem;
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .rating-value i {
            color: #FFD700;
        }
        .rating-date {
            color: #b3b3b3;
            font-size: 0.8rem;
        }
        .no-ratings {
            text-align: center;
            padding: 3rem;
            color: #b3b3b3;
            font-size: 1.1rem;
            background: #181818;
            border-radius: 4px;
            margin-top: 2rem;
        }
        .page-title {
            color: #fff;
            font-size: 2rem;
            margin-bottom: 0.5rem;
            font-weight: 600;
        }
        .page-subtext {
            color: #b3b3b3;
            margin-bottom: 2rem;
            font-size: 1.1rem;
        }
    </style>
</head>
<body>
    <header>
        <nav class="navbar">
            <nav><a class="logo" href="${pageContext.request.contextPath}/home">ReelThoughts.</a></nav>
            <div class="search-bar">
                <form action="${pageContext.request.contextPath}/search" method="GET" onsubmit="return validateSearch(this);">
                    <input type="text" name="query" placeholder="Search movies..." value="${param.query}">
                </form>
                <div class="search-results"></div>
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/recentreleases">Recent Releases</a>
                <a href="${pageContext.request.contextPath}/popular">Popular</a>
                <a href="${pageContext.request.contextPath}/news">News</a>
                <a href="${pageContext.request.contextPath}/profile"><i class="fa-regular fa-user"></i></a>
            </div>
        </nav>
    </header>

    <div class="ratings-container">
        <h1 class="page-title">Your Ratings</h1>
        <p class="page-subtext">Movies you've rated</p>

        <div class="ratings-grid">
            <%
                List<Map<String, Object>> ratings = (List<Map<String, Object>>) request.getAttribute("ratings");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                
                if (ratings != null && !ratings.isEmpty()) {
                    for (Map<String, Object> rating : ratings) {
            %>
                    <a href="${pageContext.request.contextPath}/movie-details?id=<%= rating.get("movieId") %>" class="rating-card">
                        <img src="<%= rating.get("imageLink") %>" alt="<%= rating.get("title") %>" class="movie-poster">
                        <div class="rating-info">
                            <h3 class="movie-title"><%= rating.get("title") %></h3>
                            <div class="rating-value">
                                <i class="fas fa-star"></i> <%= rating.get("rating") %>/10
                            </div>
                            <div class="rating-date">
                                Rated on: <%= dateFormat.format(rating.get("date")) %>
                            </div>
                        </div>
                    </a>
            <%
                    }
                } else {
            %>
                <div class="no-ratings">
                    <p>You haven't rated any movies yet.</p>
                </div>
            <% } %>
        </div>
    </div>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            let searchTimeout;
            const searchInput = document.querySelector('.search-bar input');
            const searchResultsContainer = document.querySelector('.search-results');
            const searchForm = document.querySelector('.search-bar form');

            // Handle form submission
            searchForm.addEventListener('submit', function(e) {
                const query = searchInput.value.trim();
                if (query.length < 2) {
                    e.preventDefault();
                    alert('Please enter at least 2 characters to search');
                }
            });

            // Handle real-time search
            searchInput.addEventListener('input', function() {
                clearTimeout(searchTimeout);
                const query = this.value.trim();
                
                if (query.length < 2) {
                    searchResultsContainer.style.display = 'none';
                    return;
                }
                
                searchTimeout = setTimeout(() => {
                    fetch('${pageContext.request.contextPath}/search?query=' + encodeURIComponent(query) + '&ajax=true')
                        .then(response => response.json())
                        .then(movies => {
                            if (movies.length === 0) {
                                searchResultsContainer.innerHTML = '<div class="no-results">No movies found</div>';
                            } else {
                                searchResultsContainer.innerHTML = movies.map(movie => `
                                    <div class="search-result-item" onclick="window.location.href='${pageContext.request.contextPath}/movie-details?id=${movie.id}'">
                                        <img src="${movie.imageLink}" alt="${movie.title}" class="search-result-poster">
                                        <div class="search-result-info">
                                            <h4>${movie.title}</h4>
                                            <p>${movie.year} • ${movie.genre}</p>
                                            <p>Director: ${movie.director}</p>
                                        </div>
                                    </div>
                                `).join('');
                            }
                            searchResultsContainer.style.display = 'block';
                        })
                        .catch(error => {
                            console.error('Error fetching search results:', error);
                            searchResultsContainer.innerHTML = '<div class="error">Error loading results</div>';
                            searchResultsContainer.style.display = 'block';
                        });
                }, 300);
            });

            // Close search results when clicking outside
            document.addEventListener('click', function(event) {
                if (!searchInput.contains(event.target) && !searchResultsContainer.contains(event.target)) {
                    searchResultsContainer.style.display = 'none';
                }
            });
        });

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
