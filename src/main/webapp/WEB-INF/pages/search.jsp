<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recentreleases.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css">
    <style>
        .search-header {
            text-align: center;
            margin: 2rem 0;
        }
        
        .search-header h1 {
            font-size: 2.5rem;
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .search-header p {
            color: #666;
            font-size: 1.1rem;
        }
        
        .search-query {
            color: #004000;
            font-weight: bold;
        }
        
        .no-results {
            text-align: center;
            padding: 3rem;
            color: #666;
        }
        
        .no-results i {
            font-size: 3rem;
            color: #ddd;
            margin-bottom: 1rem;
        }
        
        .no-results h3 {
            font-size: 1.5rem;
            margin-bottom: 0.5rem;
        }
        
        .no-results p {
            color: #888;
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
    
    <div class="recent-releases-container">
        <div class="search-header">
            <h1>Search Results</h1>
            <p>Showing results for: <span class="search-query">${param.query}</span></p>
        </div>
        
        <div class="movie-grid">
            <%
            List<Map<String, Object>> movies = (List<Map<String, Object>>) request.getAttribute("movies");
            if (movies != null && !movies.isEmpty()) {
                for (Map<String, Object> movie : movies) {
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
                <div class="no-results">
                    <i class="fas fa-search"></i>
                    <h3>No movies found</h3>
                    <p>Try different keywords or check out our recent releases</p>
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
            const contextPath = '${pageContext.request.contextPath}';

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
                    const encodedQuery = encodeURIComponent(query);
                    fetch(contextPath + '/search?query=' + encodedQuery + '&ajax=true')
                        .then(response => response.json())
                        .then(movies => {
                            if (movies.length === 0) {
                                searchResultsContainer.innerHTML = '<div class="no-results">No movies found</div>';
                            } else {
                                searchResultsContainer.innerHTML = movies.map(movie => `
                                    <div class="search-result-item" onclick="window.location.href='${contextPath}/movie-details?id=${movie.id}'">
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