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
    <title>Movie News | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/news.css">
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
        
        List<NewsItem> newsItems = Arrays.asList(
            new NewsItem("Christopher Nolan's Next Film Revealed", "Director News", 
                "The acclaimed director shares details about his upcoming sci-fi project...",
                "https://via.placeholder.com/600x400?text=Nolan", "https://example.com/nolan-news"),
                
            new NewsItem("Marvel Phase 6 Plans Leaked", "Superhero News", 
                "Insiders reveal surprising details about the next Avengers lineup...",
                "https://via.placeholder.com/600x400?text=Marvel", "https://example.com/marvel-news"),
                
            new NewsItem("2024 Award Season Predictions", "Awards", 
                "Experts weigh in on which films might dominate this year's ceremonies...",
                "https://via.placeholder.com/600x400?text=Awards", "https://example.com/awards"),
                
            new NewsItem("Exclusive: Rising Star Interview", "Interviews", 
                "We sit down with the breakout actor everyone will be talking about...",
                "https://via.placeholder.com/600x400?text=Interview", "https://example.com/interview"),
                
            new NewsItem("Dune: Part Two Behind-the-Scenes", "Movie Features", 
                "Go behind the cameras of this year's most anticipated sequel...",
                "https://via.placeholder.com/600x400?text=Dune+BTS", "https://example.com/dune-bts"),
                
            new NewsItem("Streaming Wars: New Platform Enters", "Industry News", 
                "How the new competitor plans to challenge Netflix and Disney+...",
                "https://via.placeholder.com/600x400?text=Streaming", "https://example.com/streaming")
        );
        
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
</body>
</html>