<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ReelThoughts - Home</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recentreleases.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/favorite.css">
    
    <style>
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 1.5rem;
        }
        .rating-container {
            margin-top: 10px;
        }
        .movie-rating {
            margin-bottom: 5px;
        }
        .reelthoughts-rating {
            color: #000000;
            font-weight: bold;
        }
        
        /* Simple Popup Styles */
        .popup-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.7);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }
        
        .popup {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            max-width: 400px;
            width: 90%;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.3);
        }
        
        .popup-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .popup-title {
            margin: 0;
            font-size: 18px;
        }
        
        .popup-close {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
        }
        
        .popup-content {
            margin-bottom: 20px;
        }
        
        .popup-buttons {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }
        
        .popup-button {
            padding: 8px 15px;
            border-radius: 4px;
            cursor: pointer;
        }
        
        .popup-button.cancel {
            background-color: #f5f5f5;
            border: 1px solid #ddd;
        }
        
        .popup-button.confirm {
            background-color: #007bff;
            border: none;
            color: white;
        }
        
        /* Rating Popup Styles */
        .rating-stars {
            display: flex;
            justify-content: center;
            gap: 5px;
            margin: 20px 0;
        }
        
        .rating-star {
            font-size: 24px;
            color: #ddd;
            cursor: pointer;
            transition: color 0.2s;
        }
        
        .rating-star:hover,
        .rating-star.active {
            color: #ffd700;
        }
        
        .rating-value {
            text-align: center;
            font-size: 18px;
            margin-top: 10px;
            color: #333;
        }

        /* Add styles for rated button */
        .rate-btn.rated {
            background-color: #ffd700;
            color: #000;
        }
        
        .rate-btn.rated i {
            color: #000;
        }

        /* Update movie grid layout */
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

        .actions {
            margin-top: auto;
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .actions button {
            flex: 1;
            min-width: 80px;
            padding: 8px;
            border: none;
            border-radius: 4px;
            background: #f0f0f0;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 0.9em;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 5px;
        }

        .actions button:hover {
            background: #e0e0e0;
            transform: translateY(-2px);
        }

        /* Favorite button styles */
        .favorite-btn {
            color: #000;
        }

        .favorite-btn.active {
            background: #fff;
            color: #000;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .favorite-btn.active i {
            color: #ff4d4d;
        }

        /* Watchlist button styles */
        .watchlist-btn {
            color: #000;
        }

        .watchlist-btn.active {
            background: #fff;
            color: #000;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .watchlist-btn.active i {
            color: #000;
        }

        /* Rate button styles */
        .rate-btn {
            color: #000;
        }

        .rate-btn.rated {
            background: #fff;
            color: #000;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .rate-btn.rated i {
            color: #ffd700;
        }

        /* Add hover effects for active states */
        .favorite-btn.active:hover {
            background: #f8f8f8;
        }

        .watchlist-btn.active:hover {
            background: #f8f8f8;
        }

        .rate-btn.rated:hover {
            background: #f8f8f8;
        }

        /* Responsive adjustments */
        @media (max-width: 1200px) {
            .movie-grid {
                grid-template-columns: repeat(3, 1fr);
            }
        }

        @media (max-width: 900px) {
            .movie-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 600px) {
            .movie-grid {
                grid-template-columns: 1fr;
            }
        }

        .movie-slider {
            /* margin-top: 30px;  Remove space between navbar and slider */
        }
        
        .slider-container {
            position: relative;
            width: 100%;
            overflow: hidden;
        }
        
        .slider-wrapper {
            display: flex;
            width: 100%;
            overflow: hidden;
        }
        
        .slide {
            flex: 0 0 100%;
            width: 100%;
            position: relative;
        }
        
        .slide img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .slide-info {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 10px;
            background-color: rgba(0, 0, 0, 0.5);
            color: white;
        }
        
        .slide-info h3 {
            margin: 0;
            font-size: 1.5em;
            line-height: 1.2;
        }
        
        .movie-meta {
            font-size: 0.9em;
            margin-top: 5px;
        }
        
        .watch-now {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 10px;
        }
        
        .slider-dots {
            position: absolute;
            bottom: 10px;
            left: 0;
            right: 0;
            display: flex;
            justify-content: center;
        }
        
        .dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background-color: #ccc;
            margin: 0 5px;
        }
        
        .dot.active {
            background-color: #007bff;
        }

        /* Remove navbar underline hover effect */
        .nav-links a::after {
            display: none !important;
        }
        /* Remove underline from the logo link */
        .logo {
            text-decoration: none;
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
   <main class="container">
        <section class="movie-slider">
            <div class="slider-container">
              <div class="slider-wrapper">
                <!-- Slide 1 -->
                <div class="slide">
                  <img src="${pageContext.request.contextPath}/resources/images/system/batman.avif" alt="Batman" />
                  <div class="slide-info">
                    <h3>The Batman</h3>
                    <div class="movie-meta">
                      <span class="rating"><i class="fas fa-star"></i> 8.1</span>
                      <span>2022</span>
                      <span>2h 56m</span>
                    </div>
                    <button class="watch-now">Watch Now</button>
                  </div>
                </div>
          
                <!-- Slide 2 -->
                <div class="slide">
                  <img src="${pageContext.request.contextPath}/resources/images/system/topgun.jpeg" alt="Top Gun" />
                  <div class="slide-info">
                    <h3>Top Gun : Maverick</h3>
                    <div class="movie-meta">
                      <span class="rating"><i class="fas fa-star"></i> 8.2</span>
                      <span>2022</span>
                      <span>2h 10m</span>
                    </div>
                    <button class="watch-now">Watch Now</button>
                  </div>
                </div>
              </div>
          
              <!-- Dots for aesthetics -->
              <div class="slider-dots">
                <span class="dot active"></span>
                <span class="dot"></span>
              </div>
            </div>
          </section>
          
        <!-- Trending Movies Section -->
        <section class="trending-section">
            <div class="section-header">
                <h2>Trending This Week</h2>
                <a href="#" class="see-all">See All</a>
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
                        <div class="rating-badge">8.7</div>
                    </div>
                    <div class="movie-info">
                        <h3 class="movie-title"><%= movie.get("title") %></h3>
                        <p class="meta">Director: <%= movie.get("director") %></p>
                        <p class="meta"><%= movie.get("year") %> • <%= movie.get("genre") %></p>
                        <div class="actions" onclick="event.stopPropagation();">
                            <button class="watchlist-btn" data-movie-id="<%= movieId %>" onclick="handleButtonClick(this, 'watchlist')">
                                <i class="far fa-bookmark"></i> Watchlist
                            </button>
                            <button class="favorite-btn" data-movie-id="<%= movieId %>" onclick="handleButtonClick(this, 'favorite')">
                                <i class="far fa-heart"></i> Favorite
                            </button>
                            <button class="rate-btn" data-movie-id="<%= movieId %>" onclick="handleButtonClick(this, 'rate')">
                                <i class="far fa-star"></i> Rate
                            </button>
                        </div>
                    </div>
                </div>
                <% 
                        }
                    } else {
                %>
                    <div class="no-movies">
                        <p>No movies found.</p>
                </div>
                <% } %>
            </div>
        </section>
    </main>

    <!-- Add back the popups -->
    <div id="favoritePopup" class="popup-overlay">
        <div class="popup">
            <div class="popup-header">
                <h3 class="popup-title">Add to Favorites</h3>
                <button class="popup-close" onclick="closePopup('favoritePopup')">&times;</button>
            </div>
            <div class="popup-content">
                Would you like to add this movie to your favorites?
            </div>
            <div class="popup-buttons">
                <button class="popup-button cancel" onclick="closePopup('favoritePopup')">Cancel</button>
                <button id="confirmFavoriteBtn" class="popup-button confirm">Add to Favorites</button>
            </div>
        </div>
    </div>
    
    <div id="favoriteRemovePopup" class="popup-overlay">
        <div class="popup">
            <div class="popup-header">
                <h3 class="popup-title">Remove from Favorites</h3>
                <button class="popup-close" onclick="closePopup('favoriteRemovePopup')">&times;</button>
            </div>
            <div class="popup-content">
                Would you like to remove this movie from your favorites?
            </div>
            <div class="popup-buttons">
                <button class="popup-button cancel" onclick="closePopup('favoriteRemovePopup')">Cancel</button>
                <button id="confirmFavoriteRemoveBtn" class="popup-button confirm">Remove from Favorites</button>
            </div>
        </div>
    </div>
    
    <div id="watchlistPopup" class="popup-overlay">
        <div class="popup">
            <div class="popup-header">
                <h3 class="popup-title">Add to Watchlist</h3>
                <button class="popup-close" onclick="closePopup('watchlistPopup')">&times;</button>
            </div>
            <div class="popup-content">
                Would you like to add this movie to your watchlist?
            </div>
            <div class="popup-buttons">
                <button class="popup-button cancel" onclick="closePopup('watchlistPopup')">Cancel</button>
                <button id="confirmWatchlistBtn" class="popup-button confirm">Add to Watchlist</button>
            </div>
        </div>
    </div>
    
    <div id="watchlistRemovePopup" class="popup-overlay">
        <div class="popup">
            <div class="popup-header">
                <h3 class="popup-title">Remove from Watchlist</h3>
                <button class="popup-close" onclick="closePopup('watchlistRemovePopup')">&times;</button>
            </div>
            <div class="popup-content">
                Would you like to remove this movie from your watchlist?
            </div>
            <div class="popup-buttons">
                <button class="popup-button cancel" onclick="closePopup('watchlistRemovePopup')">Cancel</button>
                <button id="confirmWatchlistRemoveBtn" class="popup-button confirm">Remove from Watchlist</button>
            </div>
        </div>
    </div>

    <div id="ratingPopup" class="popup-overlay">
        <div class="popup">
            <div class="popup-header">
                <h3 class="popup-title">Rate Movie</h3>
                <button class="popup-close" onclick="closePopup('ratingPopup')">&times;</button>
            </div>
            <div class="popup-content">
                <div class="rating-stars">
                    <i class="fas fa-star rating-star" data-rating="1"></i>
                    <i class="fas fa-star rating-star" data-rating="2"></i>
                    <i class="fas fa-star rating-star" data-rating="3"></i>
                    <i class="fas fa-star rating-star" data-rating="4"></i>
                    <i class="fas fa-star rating-star" data-rating="5"></i>
                    <i class="fas fa-star rating-star" data-rating="6"></i>
                    <i class="fas fa-star rating-star" data-rating="7"></i>
                    <i class="fas fa-star rating-star" data-rating="8"></i>
                    <i class="fas fa-star rating-star" data-rating="9"></i>
                    <i class="fas fa-star rating-star" data-rating="10"></i>
                </div>
                <div class="rating-value">Select a rating (1-10)</div>
            </div>
            <div class="popup-buttons">
                <button class="popup-button cancel" onclick="closePopup('ratingPopup')">Cancel</button>
                <button id="confirmRatingBtn" class="popup-button confirm">Submit Rating</button>
            </div>
        </div>
    </div>
    <!-- Footer -->
    <footer class="site-footer">
        <div class="footer-container">
            <div class="footer-logo">
                <h2>ReelThoughts</h2>
                <p>Your trusted source for movie ratings and reviews</p>
            </div>
    
            <div class="footer-links">
                <div class="links-column">
                    <h3>Explore</h3>
                    <ul>
                        <li><a href="#">Movies</a></li>
                        <li><a href="#">Top Rated</a></li>
                        <li><a href="#">Coming Soon</a></li>
                    </ul>
                </div>
                <div class="links-column">
                    <h3>Community</h3>
                    <ul>
                        <li><a href="#">Discussions</a></li>
                        <li><a href="#">Leaderboard</a></li>
                        <li><a href="#">Events</a></li>
                    </ul>
                </div>
                <div class="links-column">
                    <h3>Company</h3>
                    <ul>
                        <li><a href="#">About Us</a></li>
                        <li><a href="#">Careers</a></li>                   
                        <li><a href="#">Contact</a></li>
                    </ul>
                </div>
            </div>
    
            <div class="footer-social">
                <h3>Connect</h3>
                <div class="social-icons">
                    <a href="#"><i class="fab fa-twitter"></i></a>
                    <a href="#"><i class="fab fa-facebook-f"></i></a>
                    <a href="#"><i class="fab fa-instagram"></i></a>
                    <a href="#"><i class="fab fa-youtube"></i></a>
                    <a href="#"><i class="fab fa-tiktok"></i></a>
                </div>
            </div>
        </div>
    
        <div class="footer-bottom">
            <div class="footer-legal">
                <a href="#">Terms of Use</a>
                <a href="#">Privacy Policy</a>
                <a href="#">Cookie Policy</a>
            </div>
            <div class="copyright">
                <p>&copy; 2024 ReelThoughts. All rights reserved.</p>
            </div>
        </div>
    </footer>
    <script>
        // Variables to store the current movie ID
        let currentMovieId = null;
        
        // Functions to open and close popups
        function openPopup(popupId, movieId) {
            currentMovieId = movieId;
            document.getElementById(popupId).style.display = 'flex';
            
            // If it's the rating popup, check current rating
            if (popupId === 'ratingPopup') {
                fetch('${pageContext.request.contextPath}/rating?movieId=' + movieId)
                    .then(response => {
                        if (!response.ok) {
                            if (response.status === 401) {
                                window.location.href = '${pageContext.request.contextPath}/login';
                                return;
                            }
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        // Reset all stars first
                        const stars = document.querySelectorAll('.rating-star');
                        stars.forEach(star => {
                            star.classList.remove('active');
                        });
                        
                        if (data && data.rating !== null && data.rating !== undefined) {
                            // If user has already rated, show their current rating
                            stars.forEach(star => {
                                const rating = parseInt(star.getAttribute('data-rating'));
                                if (rating <= data.rating) {
                                    star.classList.add('active');
                                }
                            });
                            document.querySelector('.rating-value').textContent = `Current rating: ${data.rating}/10`;
                        } else {
                            document.querySelector('.rating-value').textContent = 'Select a rating (1-10)';
                        }
                    })
                    .catch(error => {
                        console.error('Error checking rating status:', error);
                        // Reset stars and text
                        document.querySelectorAll('.rating-star').forEach(star => {
                            star.classList.remove('active');
                        });
                        document.querySelector('.rating-value').textContent = 'Select a rating (1-10)';
                    });
            }
        }
        
        function closePopup(popupId) {
            document.getElementById(popupId).style.display = 'none';
            currentMovieId = null;
        }
        
        function handleButtonClick(button, action) {
            const movieId = button.getAttribute('data-movie-id');
            
            switch(action) {
                case 'favorite':
                    if (button.classList.contains('active')) {
                        openPopup('favoriteRemovePopup', movieId);
                    } else {
                        openPopup('favoritePopup', movieId);
                    }
                    break;
                case 'watchlist':
                    if (button.classList.contains('active')) {
                        openPopup('watchlistRemovePopup', movieId);
                    } else {
                        openPopup('watchlistPopup', movieId);
                    }
                    break;
                case 'rate':
                    openPopup('ratingPopup', movieId);
                    break;
            }
        }

        document.addEventListener('DOMContentLoaded', function() {
            // Add event listeners for favorite buttons
            const favoriteButtons = document.querySelectorAll('.favorite-btn');
            favoriteButtons.forEach(function(button) {
                const movieId = button.getAttribute('data-movie-id');
                
                // Check initial favorite status
                fetch('${pageContext.request.contextPath}/favorite?movieId=' + movieId)
                    .then(response => {
                        if (!response.ok) return null;
                        return response.json();
                    })
                    .then(data => {
                        if (data && data.isFavorite) {
                            button.classList.add('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.remove('far');
                                icon.classList.add('fas');
                            }
                        }
                    })
                    .catch(error => console.error('Error checking favorite status:', error));
            });
            
            // Add event listeners for watchlist buttons
            const watchlistButtons = document.querySelectorAll('.watchlist-btn');
            watchlistButtons.forEach(function(button) {
                const movieId = button.getAttribute('data-movie-id');
                
                // Check initial watchlist status
                fetch('${pageContext.request.contextPath}/watchlist?movieId=' + movieId)
                    .then(response => {
                        if (!response.ok) return null;
                        return response.json();
                    })
                    .then(data => {
                        if (data && data.isWatchlisted) {
                            button.classList.add('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.remove('far');
                                icon.classList.add('fas');
                            }
                        }
                    })
                    .catch(error => console.error('Error checking watchlist status:', error));
            });
            
            // Add event listeners for rate buttons
            const rateButtons = document.querySelectorAll('.rate-btn');
            rateButtons.forEach(function(button) {
                const movieId = button.getAttribute('data-movie-id');
                
                // Check if user has already rated this movie
                fetch('${pageContext.request.contextPath}/rating?movieId=' + movieId)
                    .then(response => {
                        if (!response.ok) {
                            if (response.status === 401) {
                                window.location.href = '${pageContext.request.contextPath}/login';
                                return;
                            }
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data && data.rating) {
                            button.classList.add('rated');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.remove('far');
                                icon.classList.add('fas');
                            }
                            button.innerHTML = `<i class="fas fa-star"></i> Update Rating`;
                        }
                    })
                    .catch(error => console.error('Error checking rating status:', error));
            });
                
            // Add event listeners for rating stars
            const ratingStars = document.querySelectorAll('.rating-star');
            ratingStars.forEach(star => {
                star.addEventListener('click', function() {
                    const rating = parseInt(this.getAttribute('data-rating'));
                    
                    // Update all stars
                    ratingStars.forEach(s => {
                        const starRating = parseInt(s.getAttribute('data-rating'));
                        if (starRating <= rating) {
                            s.classList.add('active');
                        } else {
                            s.classList.remove('active');
                        }
                    });
                    
                    // Update rating value text
                    document.querySelector('.rating-value').textContent = `Rating: ${rating}/10`;
                });
            });
            
            // Add confirm handlers for popup buttons
            document.getElementById('confirmFavoriteBtn').addEventListener('click', function() {
                if (!currentMovieId) return;
                
                fetch('${pageContext.request.contextPath}/favorite', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'movieId=' + encodeURIComponent(currentMovieId) + '&action=add'
                })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = '${pageContext.request.contextPath}/login';
                            return;
                        }
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(data => {
                    if (data === 'Success') {
                        const buttons = document.querySelectorAll('.favorite-btn[data-movie-id="' + currentMovieId + '"]');
                        buttons.forEach(function(button) {
                            button.classList.add('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.remove('far');
                                icon.classList.add('fas');
                            }
                        });
                    }
                    closePopup('favoritePopup');
                })
                .catch(error => {
                    console.error('Error adding favorite:', error);
                    alert('Failed to add movie to favorites. Please try again.');
                    closePopup('favoritePopup');
                });
            });
            
            document.getElementById('confirmFavoriteRemoveBtn').addEventListener('click', function() {
                if (!currentMovieId) return;
                
                fetch('${pageContext.request.contextPath}/favorite', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'movieId=' + encodeURIComponent(currentMovieId) + '&action=remove'
                })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = '${pageContext.request.contextPath}/login';
                            return;
                        }
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(data => {
                    if (data === 'Success') {
                        const buttons = document.querySelectorAll('.favorite-btn[data-movie-id="' + currentMovieId + '"]');
                        buttons.forEach(function(button) {
                            button.classList.remove('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.add('far');
                                icon.classList.remove('fas');
                            }
                        });
                    }
                    closePopup('favoriteRemovePopup');
                })
                .catch(error => {
                    console.error('Error removing favorite:', error);
                    alert('Failed to remove movie from favorites. Please try again.');
                    closePopup('favoriteRemovePopup');
                });
            });
            
            document.getElementById('confirmWatchlistBtn').addEventListener('click', function() {
                if (!currentMovieId) return;
                
                fetch('${pageContext.request.contextPath}/watchlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'movieId=' + encodeURIComponent(currentMovieId) + '&action=add'
                })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = '${pageContext.request.contextPath}/login';
                            return;
                        }
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(data => {
                    if (data === 'Success') {
                        const buttons = document.querySelectorAll('.watchlist-btn[data-movie-id="' + currentMovieId + '"]');
                        buttons.forEach(function(button) {
                            button.classList.add('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.remove('far');
                                icon.classList.add('fas');
                            }
                        });
                    }
                    closePopup('watchlistPopup');
                })
                .catch(error => {
                    console.error('Error adding to watchlist:', error);
                    alert('Failed to add movie to watchlist. Please try again.');
                    closePopup('watchlistPopup');
                });
            });
            
            document.getElementById('confirmWatchlistRemoveBtn').addEventListener('click', function() {
                if (!currentMovieId) return;
                
                fetch('${pageContext.request.contextPath}/watchlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'movieId=' + encodeURIComponent(currentMovieId) + '&action=remove'
                })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = '${pageContext.request.contextPath}/login';
                            return;
                        }
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(data => {
                    if (data === 'Success') {
                        const buttons = document.querySelectorAll('.watchlist-btn[data-movie-id="' + currentMovieId + '"]');
                        buttons.forEach(function(button) {
                            button.classList.remove('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.add('far');
                                icon.classList.remove('fas');
                            }
                        });
                    }
                    closePopup('watchlistRemovePopup');
                })
                .catch(error => {
                    console.error('Error removing from watchlist:', error);
                    alert('Failed to remove movie from watchlist. Please try again.');
                    closePopup('watchlistRemovePopup');
                });
            });
            
            // Add event listener for rating submission
            document.getElementById('confirmRatingBtn').addEventListener('click', function() {
                const activeStars = document.querySelectorAll('.rating-star.active');
                if (activeStars.length === 0) {
                    alert('Please select a rating');
                    return;
                }
                
                const rating = activeStars.length;
                const movieId = currentMovieId;
                
                fetch('${pageContext.request.contextPath}/rating', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'movieId=' + movieId + '&rating=' + rating
                })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = '${pageContext.request.contextPath}/login';
                            return;
                        }
                        return response.text().then(text => {
                            try {
                                return JSON.parse(text);
                            } catch (e) {
                                console.error('Error parsing response:', text);
                                throw new Error('Invalid response format');
                            }
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        alert(data.error);
                        return;
                    }
                    
                    // Update UI to show the new rating
                    const rateButtons = document.querySelectorAll('.rate-btn[data-movie-id="' + movieId + '"]');
                    rateButtons.forEach(function(button) {
                        button.classList.add('rated');
                        const icon = button.querySelector('i');
                        if (icon) {
                            icon.classList.remove('far');
                            icon.classList.add('fas');
                        }
                        button.innerHTML = `<i class="fas fa-star"></i> Update Rating`;
                    });
                    
                    closePopup('ratingPopup');
                    window.location.reload(); // Reload to show updated rating
                })
                .catch(error => {
                    console.error('Error submitting rating:', error);
                    alert('Failed to submit rating. Please try again.');
                });
            });
            
            // Close popups when clicking outside
            window.addEventListener('click', function(event) {
                if (event.target.classList.contains('popup-overlay')) {
                    closePopup(event.target.id);
                }
            });
        });

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

        // Slider functionality
        const sliderWrapper = document.querySelector(".slider-wrapper");
        const slides = document.querySelectorAll(".slide");
        const dots = document.querySelectorAll(".dot");
        let currentIndex = 0;
      
        function updateSlider() {
          const slideWidth = slides[0].clientWidth;
          sliderWrapper.style.transform = `translateX(-${currentIndex * slideWidth}px)`;
      
          dots.forEach(dot => dot.classList.remove("active"));
          if (dots[currentIndex]) dots[currentIndex].classList.add("active");
        }
      
        function autoSlide() {
          currentIndex = (currentIndex + 1) % slides.length;
          updateSlider();
        }
      
        let slideInterval = setInterval(autoSlide, 6000);
      
        function resetInterval() {
          clearInterval(slideInterval);
          slideInterval = setInterval(autoSlide, 6000);
        }
      
        dots.forEach((dot, index) => {
          dot.addEventListener("click", () => {
            currentIndex = index;
            updateSlider();
            resetInterval();
          });
        });
      
        const nextBtn = document.createElement("button");
        nextBtn.innerText = "Next";
        nextBtn.classList.add("next-slide-btn");
        document.querySelector(".slider-container").appendChild(nextBtn);
      
        nextBtn.addEventListener("click", () => {
          currentIndex = (currentIndex + 1) % slides.length;
          updateSlider();
          resetInterval();
        });
      
        // Initial setup
        updateSlider();
    </script>
    
</body>
</html>