<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movie News | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/news.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recentreleases.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css">
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
    
    <div class="news-header">
        <h1 class="news-title">Latest Movie News</h1>
        <p class="news-subtext">Stay updated with Hollywood buzz, releases, and exclusive interviews</p>
    </div>
        
    <div class="news-grid-container">
        <% 
        class NewsItem {
            String title;
            String category;
            String excerpt;
            String imageUrl;
            String sourceUrl;
            
            public NewsItem(String title, String category, String excerpt, String imageUrl, String sourceUrl) {
                this.title = title;
                this.category = category;
                this.excerpt = excerpt;
                this.imageUrl = imageUrl;
                this.sourceUrl = sourceUrl;
            }
        }
        
        List<NewsItem> newsItems = new ArrayList<>();
        
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/reelthoughts";
        String username = "root";
        String password = "";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT title, category, excerpt, image_url, source_url FROM news_articles ORDER BY published_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    newsItems.add(new NewsItem(
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("excerpt"),
                        rs.getString("image_url"),
                        rs.getString("source_url")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error fetching news: " + e.getMessage() + "</p>");
        }
        
        for (NewsItem item : newsItems) {
        %>
        <article class="news-card">
            <div class="news-image-container">
                <img src="<%= item.imageUrl %>" alt="<%= item.title %>" class="news-image">
                <span class="news-category"><%= item.category %></span>
            </div>
            <div class="news-content">
                <h2><a href="<%= item.sourceUrl %>" target="_blank"><%= item.title %></a></h2>
                <p class="news-excerpt"><%= item.excerpt %></p>
                <a href="<%= item.sourceUrl %>" target="_blank" class="read-more">Read More <i class="fas fa-arrow-right"></i></a>
            </div>
        </article>
        <% } %>
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