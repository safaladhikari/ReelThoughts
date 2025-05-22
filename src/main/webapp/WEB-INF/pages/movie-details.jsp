<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%
    Map<String, Object> movie = (Map<String, Object>) request.getAttribute("movie");
    String error = (String) request.getAttribute("error");
    List<Map<String, Object>> reviews = (List<Map<String, Object>>) request.getAttribute("reviews");
    Boolean isFavorite = (Boolean) request.getAttribute("isFavorite");
    Boolean isInWatchlist = (Boolean) request.getAttribute("isInWatchlist");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    Integer userRating = (Integer) request.getAttribute("userRating");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= (movie != null) ? movie.get("title") : "Movie Details" %></title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movie-details.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recentreleases.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css">
    <style>
        .rating-input {
            margin-bottom: 15px;
        }

        .rating-input label {
            display: block;
            margin-bottom: 5px;
            color: #fff;
        }

        .star-rating {
            display: flex;
            flex-direction: row-reverse;
            justify-content: flex-end;
        }

        .star-rating input {
            display: none;
        }

        .star-rating label {
            cursor: pointer;
            font-size: 25px;
            color: #444;
            padding: 0 2px;
        }

        .star-rating label:hover,
        .star-rating label:hover ~ label,
        .star-rating input:checked ~ label {
            color: #00ff00;
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
            color: #333;
        }
        
        .popup-close {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #666;
        }
        
        .popup-content {
            margin-bottom: 20px;
            color: #666;
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
            color: #333;
        }
        
        .popup-button.confirm {
            background-color: #007bff;
            border: none;
            color: white;
        }

        /* Action Button Styles */
        .actions {
            display: flex;
            gap: 10px;
            margin-top: 20px;
            flex-wrap: wrap;
        }

        .favorite-btn, .watchlist-btn, .rate-btn {
            flex: 1;
            min-width: 80px;
            padding: 8px 16px;
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
            color: #000;
        }

        .favorite-btn:hover, .watchlist-btn:hover, .rate-btn:hover {
            background: #e0e0e0;
            transform: translateY(-2px);
        }

        /* Favorite button styles */
        .favorite-btn.active {
            background: #fff;
            color: #000;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .favorite-btn.active i {
            color: #ff4d4d;
        }

        /* Watchlist button styles */
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
            color: #ffd700 !important;
        }
        
        .rating-value {
            text-align: center;
            font-size: 18px;
            margin-top: 10px;
            color: #333;
        }

        /* Remove conflicting styles */
        .rating-section {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            margin-left: 10px;
        }

        .rating-stars {
            display: flex;
            gap: 2px;
        }

        .rating-star {
            font-size: 20px;
            color: #ddd;
            cursor: pointer;
            transition: color 0.2s;
        }

        .rating-star:hover,
        .rating-star.active {
            color: #ffd700 !important;
        }

        .rating-value {
            color: #333;
            font-size: 14px;
        }

        #submitRatingBtn {
            padding: 8px 16px;
            border: 1px solid #ffffff;
            border-radius: 4px;
            background: transparent;
            color: #ffffff;
            cursor: pointer;
            transition: all 0.2s;
        }

        #submitRatingBtn:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }

        .star-rating label:hover,
        .star-rating label:hover ~ label,
        .star-rating input:checked ~ label {
            color: #ffffff;
        }

        .review-form textarea {
            background-color: rgba(255, 255, 255, 0.1);
            color: #ffffff;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .review-form textarea::placeholder {
            color: rgba(255, 255, 255, 0.5);
        }

        .review-form button {
            background-color: transparent;
            border: 1px solid #ffffff;
            color: #ffffff;
        }

        .review-form button:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }

        /* Added these styles */
        h1, h2, h3, .popup-title {
            color: #ffffff;
        }

        .movie-meta p {
            color: #ffffff;
        }

        .movie-description h2 {
            color: #ffffff;
        }

        .movie-description p {
            color: #ffffff;
        }

        .reviews-section h2 {
            color: #ffffff;
        }

        .review-header h3 {
            color: #ffffff;
        }

        .review-date {
            color: rgba(255, 255, 255, 0.7);
        }

        .review-text {
            color: #ffffff;
        }

        .no-reviews {
            color: #ffffff;
        }

        /* Review Form Styles */
        .review-form {
            background: rgba(255, 255, 255, 0.1);
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }

        .review-form textarea {
            width: 100%;
            min-height: 120px;
            padding: 12px;
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 4px;
            background-color: rgba(255, 255, 255, 0.1);
            color: #ffffff;
            font-size: 16px;
            resize: vertical;
            margin-bottom: 10px;
        }

        .review-form textarea:focus {
            outline: none;
            border-color: #ffd700;
        }

        .review-form textarea::placeholder {
            color: rgba(255, 255, 255, 0.5);
        }

        .review-form .char-counter {
            text-align: right;
            color: rgba(255, 255, 255, 0.7);
            font-size: 14px;
            margin-bottom: 10px;
        }

        .review-form .char-counter.warning {
            color: #ffd700;
        }

        .review-form .char-counter.error {
            color: #ff4d4d;
        }

        .review-form button {
            background-color: #ffd700;
            color: #000;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            transition: all 0.3s ease;
        }

        .review-form button:hover {
            background-color: #ffed4a;
            transform: translateY(-2px);
        }

        .review-form button:disabled {
            background-color: #666;
            cursor: not-allowed;
            transform: none;
        }

        .review-form .loading {
            display: none;
            text-align: center;
            margin-top: 10px;
        }

        .review-form .loading.active {
            display: block;
        }

        .review-form .loading i {
            color: #ffd700;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* Review Card Styles */
        .review-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
        }

        .review-header {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 15px;
            object-fit: cover;
        }

        .review-info h3 {
            margin: 0;
            font-size: 18px;
            color: #ffffff;
        }

        .review-date {
            font-size: 14px;
            color: rgba(255, 255, 255, 0.7);
            margin: 5px 0 0 0;
        }

        .review-text {
            color: #ffffff;
            line-height: 1.6;
            margin: 0 0 15px 0;
        }

        .delete-review {
            background: none;
            border: 1px solid #ff4d4d;
            color: #ff4d4d;
            padding: 5px 10px;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .delete-review:hover {
            background-color: #ff4d4d;
            color: #ffffff;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <header>
        <nav class="navbar">
            <div><a class="logo" href="${pageContext.request.contextPath}/home">ReelThoughts.</a></div>
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
        <a href="${pageContext.request.contextPath}/home" class="back-link">
            <i class="fas fa-arrow-left"></i> Back to Home
        </a>

        <% if (movie != null) { %>
            <div class="movie-details">
                <div class="movie-poster">
                    <img src="${movie.imageLink}" alt="${movie.title}">
                </div>
                <div class="movie-info">
                    <h1>${movie.title}</h1>
                    <div class="movie-meta">
                        <p><strong>Director:</strong> ${movie.director}</p>
                        <p><strong>Year:</strong> ${movie.year}</p>
                        <p><strong>Genre:</strong> ${movie.genre}</p>
                        <p><strong>Cast:</strong> ${movie.cast}</p>
                    </div>
                    <div class="movie-description">
                        <h2>Description</h2>
                        <p>${movie.description}</p>
                    </div>
                    <div class="actions" onclick="event.stopPropagation();">
                        <button class="watchlist-btn ${isInWatchlist ? 'active' : ''}" data-movie-id="${movie.id}" onclick="handleButtonClick(this, 'watchlist')">
                            <i class="${isInWatchlist ? 'fas' : 'far'} fa-bookmark"></i> ${isInWatchlist ? 'Remove from Watchlist' : 'Add to Watchlist'}
                        </button>
                        <button class="favorite-btn ${isFavorite ? 'active' : ''}" data-movie-id="${movie.id}" onclick="handleButtonClick(this, 'favorite')">
                            <i class="${isFavorite ? 'fas' : 'far'} fa-heart"></i> ${isFavorite ? 'Remove from Favorites' : 'Add to Favorites'}
                        </button>
                        <button class="rate-btn ${userRating != null ? 'rated' : ''}" data-movie-id="${movie.id}" onclick="handleButtonClick(this, 'rate')">
                            <i class="${userRating != null ? 'fas' : 'far'} fa-star"></i> ${userRating != null ? 'Update Rating' : 'Rate'}
                        </button>
                    </div>
                </div>
            </div>

            <div class="reviews-section">
                <h2>Reviews</h2>
                <div class="review-form">
                    <form id="reviewForm" onsubmit="return false;">
                        <input type="hidden" name="movieId" value="${movie.id}">
                        <textarea name="reviewText" placeholder="Write your review..." required maxlength="1000"></textarea>
                        <div class="char-counter">0/1000 characters</div>
                        <button type="submit" id="submitReviewBtn">Submit Review</button>
                        <div class="loading">
                            <i class="fas fa-spinner"></i> Submitting review...
                        </div>
                    </form>
                </div>
                <div class="reviews-list">
                    <% 
                    if (reviews != null && !reviews.isEmpty()) {
                        for (Map<String, Object> review : reviews) {
                    %>
                        <div class="review-card" id="review-<%= review.get("id") %>">
                            <div class="review-header">
                                <img src="<%= review.get("userProfilePicture") != null ? review.get("userProfilePicture") : "${pageContext.request.contextPath}/resources/images/system/user avatar.avif" %>" 
                                     alt="<%= review.get("username") %>" class="user-avatar">
                                <div class="review-info">
                                    <h3><%= review.get("username") %></h3>
                                    <p class="review-date"><%= dateFormat.format(review.get("createdAt")) %></p>
                                </div>
                            </div>
                            <p class="review-text"><%= review.get("reviewText") %></p>
                            <% if (review.get("userId").equals(request.getSession().getAttribute("userId"))) { %>
                                <button class="delete-review" onclick="deleteReview('<%= review.get("id") %>', '<%= movie.get("title") %>')">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            <% } %>
                        </div>
                    <% 
                        }
                    } else {
                    %>
                        <p class="no-reviews">No reviews yet. Be the first to review this movie!</p>
                    <%
                    } 
                    %>
                </div>
            </div>
        <% } else { %>
            <div class="error-message">
                <p><%= error != null ? error : "Movie not found" %></p>
            </div>
        <% } %>
    </main>

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
        let currentMovieId = '${movie.id}';  // Set the initial movie ID from the JSP

        function handleButtonClick(button, type) {
            const movieId = button.getAttribute("data-movie-id");
            console.log("Button clicked for movie ID:", movieId);
            
            if (!movieId) {
                console.error("Movie ID is missing from button data attribute");
                return;
            }

            currentMovieId = movieId;
            console.log("Current movie ID set to:", currentMovieId);

            switch(type) {
                case "favorite":
                    if (button.classList.contains("active")) {
                        openPopup("favoriteRemovePopup", movieId);
                    } else {
                        openPopup("favoritePopup", movieId);
                    }
                    break;
                case "watchlist":
                    if (button.classList.contains("active")) {
                        openPopup("watchlistRemovePopup", movieId);
                    } else {
                        openPopup("watchlistPopup", movieId);
                    }
                    break;
                case "rate":
                    console.log('Checking rating for movie ID:', movieId);
                    // Check if user has already rated this movie
                    fetch('${pageContext.request.contextPath}/rating?movieId=' + movieId)
                        .then(response => {
                            console.log('Rating check response status:', response.status);
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
                            console.log('Rating data from server:', data);
                            
                            // Reset all stars first
                            const stars = document.querySelectorAll('.rating-star');
                            stars.forEach(star => {
                                star.classList.remove('active');
                            });
                            
                            if (data && data.rating !== null && data.rating !== undefined) {
                                console.log('Setting stars for rating:', data.rating);
                                // If user has already rated, show their current rating
                                stars.forEach(star => {
                                    const rating = parseInt(star.getAttribute('data-rating'));
                                    console.log('Checking star rating:', rating, 'against user rating:', data.rating);
                                    if (rating <= data.rating) {
                                        star.classList.add('active');
                                    }
                                });
                                document.querySelector('.rating-value').textContent = `Current rating: ${data.rating}/10`;
                            } else {
                                console.log('No previous rating found');
                                document.querySelector('.rating-value').textContent = 'Select a rating (1-10)';
                            }
                            openPopup('ratingPopup', movieId);
                        })
                        .catch(error => {
                            console.error('Error checking rating status:', error);
                            // Reset stars and open popup
                            document.querySelectorAll('.rating-star').forEach(star => {
                                star.classList.remove('active');
                            });
                            document.querySelector('.rating-value').textContent = 'Select a rating (1-10)';
                            openPopup('ratingPopup', movieId);
                        });
                    break;
            }
        }

        function openPopup(popupId, movieId) {
            console.log("Opening popup:", popupId, "for movie:", movieId);
            document.getElementById(popupId).style.display = 'flex';
            currentMovieId = movieId;
            console.log("Current movie ID in popup:", currentMovieId);
        }

        function closePopup(popupId) {
            document.getElementById(popupId).style.display = 'none';
        }

        // Add event listeners for popup buttons
        document.addEventListener('DOMContentLoaded', function() {
            // Favorite add handler
            document.getElementById('confirmFavoriteBtn').addEventListener('click', function() {
                console.log("Confirm favorite clicked for movie:", currentMovieId);
                
                if (!currentMovieId) {
                    console.error("No movie ID available");
                    closePopup('favoritePopup');
                    return;
                }
                
                const formData = new URLSearchParams();
                formData.append('movieId', currentMovieId);
                formData.append('action', 'add');
                
                console.log("Sending favorite request with data:", formData.toString());
                
                fetch('${pageContext.request.contextPath}/favorite', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData.toString()
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
                    console.log("Favorite response:", data);
                    if (data === 'Success') {
                        const button = document.querySelector('.favorite-btn');
                        button.classList.add('active');
                        const icon = button.querySelector('i');
                        icon.classList.remove('far');
                        icon.classList.add('fas');
                        button.innerHTML = `<i class="fas fa-heart"></i> Remove from Favorites`;
                        window.location.reload();
                    }
                    closePopup('favoritePopup');
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to add to favorites');
                    closePopup('favoritePopup');
                });
            });

            // Add confirm handlers for popup buttons for removing favorites
            document.getElementById('confirmFavoriteRemoveBtn').addEventListener('click', function() {
                console.log('Confirm remove favorite for movie ID:', currentMovieId);
                if (!currentMovieId) {
                    console.error('No movie ID set for favorite removal action');
                    closePopup('favoriteRemovePopup');
                    return;
                }
                
                const requestBody = 'movieId=' + encodeURIComponent(currentMovieId) + '&action=remove';
                
                fetch('${pageContext.request.contextPath}/favorite', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: requestBody
                })
                .then(response => {
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = '${pageContext.request.contextPath}/login';
                            return;
                        }
                        throw new Error('Network response was not ok: ' + response.status);
                    }
                    return response.text();
                })
                .then(data => {
                    console.log('Response data:', data);
                    if (data === 'Success') {
                        // Find and update the button to non-favorite state
                        const buttons = document.querySelectorAll('.favorite-btn[data-movie-id="' + currentMovieId + '"]');
                        buttons.forEach(function(button) {
                            button.classList.remove('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.add('far');
                                icon.classList.remove('fas');
                            }
                            button.innerHTML = `<i class="far fa-heart"></i> Add to Favorites`;
                        });
                        console.log('Movie removed from favorites successfully');
                    } else {
                        console.error('Unexpected response:', data);
                        alert('Failed to remove movie from favorites. Unexpected response from server.');
                    }
                    closePopup('favoriteRemovePopup');
                })
                .catch(error => {
                    console.error('Error removing from favorites:', error);
                    alert('Failed to remove movie from favorites. Please try again.');
                    closePopup('favoriteRemovePopup');
                });
            });

            // Watchlist add handler
            document.getElementById('confirmWatchlistBtn').addEventListener('click', function() {
                console.log("Confirm watchlist clicked for movie:", currentMovieId);
                
                if (!currentMovieId) {
                    console.error("No movie ID available");
                    closePopup('watchlistPopup');
                    return;
                }
                
                const formData = new URLSearchParams();
                formData.append('movieId', currentMovieId);
                formData.append('action', 'add');
                
                console.log("Sending watchlist request with data:", formData.toString());
                
                fetch('${pageContext.request.contextPath}/watchlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData.toString()
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
                    console.log("Watchlist response:", data);
                    if (data === 'Success') {
                        const button = document.querySelector('.watchlist-btn');
                        button.classList.add('active');
                        const icon = button.querySelector('i');
                        icon.classList.remove('far');
                        icon.classList.add('fas');
                        button.innerHTML = `<i class="fas fa-bookmark"></i> Remove from Watchlist`;
                        window.location.reload();
                    }
                    closePopup('watchlistPopup');
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to add to watchlist');
                    closePopup('watchlistPopup');
                });
            });

            // Add confirm handlers for popup buttons for removing from watchlist
            document.getElementById('confirmWatchlistRemoveBtn').addEventListener('click', function() {
                console.log('Confirm remove watchlist for movie ID:', currentMovieId);
                if (!currentMovieId) {
                    console.error('No movie ID set for watchlist removal action');
                    closePopup('watchlistRemovePopup');
                    return;
                }
                
                const requestBody = 'movieId=' + encodeURIComponent(currentMovieId) + '&action=remove';
                
                fetch('${pageContext.request.contextPath}/watchlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: requestBody
                })
                .then(response => {
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        if (response.status === 401) {
                            window.location.href = '${pageContext.request.contextPath}/login';
                            return;
                        }
                        throw new Error('Network response was not ok: ' + response.status);
                    }
                    return response.text();
                })
                .then(data => {
                    console.log('Response data:', data);
                    if (data === 'Success') {
                        // Find and update the button to non-watchlist state
                        const buttons = document.querySelectorAll('.watchlist-btn[data-movie-id="' + currentMovieId + '"]');
                        buttons.forEach(function(button) {
                            button.classList.remove('active');
                            const icon = button.querySelector('i');
                            if (icon) {
                                icon.classList.add('far');
                                icon.classList.remove('fas');
                            }
                            button.innerHTML = `<i class="far fa-bookmark"></i> Add to Watchlist`;
                        });
                        console.log('Movie removed from watchlist successfully');
                    } else {
                        console.error('Unexpected response:', data);
                        alert('Failed to remove movie from watchlist. Unexpected response from server.');
                    }
                    closePopup('watchlistRemovePopup');
                })
                .catch(error => {
                    console.error('Error removing from watchlist:', error);
                    alert('Failed to remove movie from watchlist. Please try again.');
                    closePopup('watchlistRemovePopup');
                });
            });

            // Review form functionality
            const reviewForm = document.getElementById('reviewForm');
            const reviewTextarea = reviewForm.querySelector('textarea[name="reviewText"]');
            const charCounter = reviewForm.querySelector('.char-counter');
            const submitButton = reviewForm.querySelector('button[type="submit"]');
            const loadingIndicator = reviewForm.querySelector('.loading');

            // Character counter
            reviewTextarea.addEventListener('input', function() {
                const length = this.value.length;
                const maxLength = this.getAttribute('maxlength');
                charCounter.textContent = `${length}/${maxLength} characters`;
                
                // Update counter color based on length
                if (length > maxLength * 0.9) {
                    charCounter.className = 'char-counter warning';
                } else if (length === maxLength) {
                    charCounter.className = 'char-counter error';
                } else {
                    charCounter.className = 'char-counter';
                }
            });

            // Form submission
            reviewForm.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const reviewText = reviewTextarea.value.trim();
                
                if (!reviewText) {
                    alert('Please enter a review');
                    return;
                }
                
                // Disable form while submitting
                submitButton.disabled = true;
                loadingIndicator.classList.add('active');
                
                const formData = new URLSearchParams();
                formData.append('movieId', currentMovieId);
                formData.append('reviewText', reviewText);
                
                console.log('Submitting review with data:', formData.toString());
                
                fetch('${pageContext.request.contextPath}/review', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData.toString()
                })
                .then(response => {
                    console.log('Response status:', response.status);
                    return response.text().then(text => {
                        if (!response.ok) {
                            if (response.status === 401) {
                                window.location.href = '${pageContext.request.contextPath}/login';
                                return;
                            }
                            throw new Error(text || 'Network response was not ok');
                        }
                        return text;
                    });
                })
                .then(data => {
                    console.log('Response data:', data);
                    if (data === 'Success') {
                        // Clear form
                        reviewTextarea.value = '';
                        charCounter.textContent = '0/1000 characters';
                        charCounter.className = 'char-counter';
                        
                        // Show success message
                        alert('Review submitted successfully!');
                        
                        // Reload page to show new review
                        window.location.reload();
                    } else {
                        throw new Error(data || 'Failed to submit review');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert(error.message || 'Failed to submit review. Please try again.');
                })
                .finally(() => {
                    // Re-enable form
                    submitButton.disabled = false;
                    loadingIndicator.classList.remove('active');
                });
            });

            // Close popups when clicking outside
            window.addEventListener('click', function(event) {
                if (event.target.classList.contains('popup-overlay')) {
                    closePopup(event.target.id);
                }
            });

            // Add event listeners for rating stars
            const ratingStars = document.querySelectorAll('.rating-star');
            ratingStars.forEach(star => {
                star.addEventListener('click', function() {
                    const rating = parseInt(this.getAttribute('data-rating'));
                    console.log('Star clicked with rating:', rating);
                    
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

            // Add event listener for rating submission
            document.getElementById('confirmRatingBtn').addEventListener('click', function() {
                const activeStars = document.querySelectorAll('.rating-star.active');
                if (activeStars.length === 0) {
                    alert('Please select a rating');
                    return;
                }
                
                const rating = activeStars.length;
                console.log('Submitting rating:', rating, 'for movie:', currentMovieId);
                
                if (!currentMovieId) {
                    console.error('No movie ID available for rating');
                    alert('Error: Movie ID not found');
                    return;
                }
                
                fetch('${pageContext.request.contextPath}/rating', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'movieId=' + currentMovieId + '&rating=' + rating
                })
                .then(response => {
                    console.log('Rating submission response status:', response.status);
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
                    console.log('Rating submission response:', data);
                    if (data.error) {
                        alert(data.error);
                        return;
                    }
                    
                    // Update UI to show the new rating
                    const rateButton = document.querySelector('.rate-btn[data-movie-id="' + currentMovieId + '"]');
                    if (rateButton) {
                        rateButton.classList.add('rated');
                        const icon = rateButton.querySelector('i');
                        if (icon) {
                            icon.classList.remove('far');
                            icon.classList.add('fas');
                        }
                        rateButton.innerHTML = `<i class="fas fa-star"></i> Update Rating`;
                    }
                    
                    closePopup('ratingPopup');
                    window.location.reload(); // Reload to show updated rating
                })
                .catch(error => {
                    console.error('Error submitting rating:', error);
                    alert('Failed to submit rating. Please try again.');
                });
            });
        });

        function deleteReview(reviewId, movieTitle) {
            if (confirm("Are you sure you want to delete this review?")) {
                fetch("${pageContext.request.contextPath}/review", {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: new URLSearchParams({
                        "reviewId": reviewId,
                        "movieId": "${movie.id}"
                    })
                })
                .then(response => {
                    if (response.ok) {
                        const reviewCard = document.getElementById("review-" + reviewId);
                        if (reviewCard) {
                            reviewCard.remove();
                        }
                        
                        const reviewsList = document.querySelector(".reviews-list");
                        if (reviewsList.children.length === 0) {
                            reviewsList.innerHTML = "<p class=\"no-reviews\">No reviews yet. Be the first to review this movie!</p>";
                        }
                    } else {
                        throw new Error("Failed to delete review");
                    }
                })
                .catch(error => {
                    alert("Error deleting review: " + error.message);
                });
            }
        }
    </script>

    <!-- Popups -->
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

    <!-- Add Rating Popup -->
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