<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Reviews | ReelThoughts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <style>
        body {
            background-color: #141414;
            color: #fff;
            font-family: 'Helvetica Neue', Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        .reviews-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        .reviews-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-top: 2rem;
        }
        .review-card {
            background: #181818;
            border-radius: 4px;
            overflow: hidden;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            position: relative;
            display: flex;
            flex-direction: column;
        }
        .review-card:hover {
            transform: scale(1.02);
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        .movie-poster {
            width: 100%;
            aspect-ratio: 2/3;
            object-fit: cover;
        }
        .review-info {
            padding: 1rem;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
        }
        .movie-title {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #fff;
        }
        .review-text {
            color: #b3b3b3;
            font-size: 0.9rem;
            margin-bottom: 1rem;
            flex-grow: 1;
            line-height: 1.4;
        }
        .review-date {
            color: #666;
            font-size: 0.8rem;
            margin-bottom: 0.5rem;
        }
        .review-actions {
            display: flex;
            gap: 1rem;
            margin-top: auto;
        }
        .edit-button, .delete-button {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
            transition: background-color 0.2s;
        }
        .edit-button {
            background-color: #e50914;
            color: white;
        }
        .edit-button:hover {
            background-color: #f40612;
        }
        .delete-button {
            background-color: #333;
            color: white;
        }
        .delete-button:hover {
            background-color: #444;
        }
        .no-reviews {
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
        .edit-review-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            z-index: 1000;
        }
        .modal-content {
            position: relative;
            background: #181818;
            margin: 10% auto;
            padding: 2rem;
            width: 80%;
            max-width: 600px;
            border-radius: 8px;
        }
        .close-modal {
            position: absolute;
            right: 1rem;
            top: 1rem;
            font-size: 1.5rem;
            color: #fff;
            cursor: pointer;
        }
        .review-form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        .review-form textarea {
            width: 100%;
            min-height: 150px;
            padding: 0.5rem;
            background: #333;
            border: none;
            border-radius: 4px;
            color: #fff;
            font-size: 1rem;
            resize: vertical;
        }
        .review-form button {
            padding: 0.8rem;
            background: #e50914;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1rem;
        }
        .review-form button:hover {
            background: #f40612;
        }
    </style>
</head>
<body>
    <header>
        <nav class="navbar">
            <nav><a class="logo" href="${pageContext.request.contextPath}/home">ReelThoughts.</a></nav>
            <div class="search-bar">
                <input type="text" placeholder="Search movies...">
            </div>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/recentreleases">Recent Releases</a>
                <a href="${pageContext.request.contextPath}/popular">Popular</a>
                <a href="${pageContext.request.contextPath}/news">News</a>
                <a href="${pageContext.request.contextPath}/profile"><i class="fa-regular fa-user"></i></a>
            </div>
        </nav>
    </header>

    <div class="reviews-container">
        <h1 class="page-title">Your Reviews</h1>
        <p class="page-subtext">Movies you've reviewed</p>

        <div class="reviews-grid">
            <%
                List<Map<String, Object>> reviews = (List<Map<String, Object>>) request.getAttribute("reviews");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                
                if (reviews != null && !reviews.isEmpty()) {
                    for (Map<String, Object> review : reviews) {
            %>
                    <div class="review-card">
                        <a href="${pageContext.request.contextPath}/movie-details?id=<%= review.get("movieId") %>">
                            <img src="<%= review.get("imageLink") %>" alt="<%= review.get("title") %>" class="movie-poster">
                        </a>
                        <div class="review-info">
                            <h3 class="movie-title"><%= review.get("title") %></h3>
                            <p class="review-text"><%= review.get("reviewText") %></p>
                            <p class="review-date">Reviewed on: <%= dateFormat.format(review.get("reviewDate")) %></p>
                            <div class="review-actions">
                                <button class="edit-button" onclick="openEditModal('<%= review.get("movieId") %>', '<%= review.get("reviewText") %>')">
                                    <i class="fas fa-edit"></i> Edit
                                </button>
                                <button class="delete-button" onclick="deleteReview('<%= review.get("movieId") %>')">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </div>
                        </div>
                    </div>
            <%
                    }
                } else {
            %>
                <div class="no-reviews">
                    <p>You haven't written any reviews yet.</p>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Edit Review Modal -->
    <div id="editReviewModal" class="edit-review-modal">
        <div class="modal-content">
            <span class="close-modal" onclick="closeEditModal()">&times;</span>
            <h2>Edit Review</h2>
            <form class="review-form" id="editReviewForm">
                <input type="hidden" id="editMovieId" name="movieId">
                <textarea id="editReviewText" name="reviewText" required></textarea>
                <button type="submit">Save Changes</button>
            </form>
        </div>
    </div>

    <script>
        function openEditModal(movieId, reviewText) {
            document.getElementById('editMovieId').value = movieId;
            document.getElementById('editReviewText').value = reviewText;
            document.getElementById('editReviewModal').style.display = 'block';
        }

        function closeEditModal() {
            document.getElementById('editReviewModal').style.display = 'none';
        }

        function deleteReview(movieId) {
            if (confirm('Are you sure you want to delete this review?')) {
                fetch('${pageContext.request.contextPath}/userprofilereview', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'action=delete&movieId=' + movieId
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert('Error deleting review: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error deleting review');
                });
            }
        }

        document.getElementById('editReviewForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const movieId = document.getElementById('editMovieId').value;
            const reviewText = document.getElementById('editReviewText').value;

            fetch('${pageContext.request.contextPath}/userprofilereview', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'action=update&movieId=' + movieId + '&reviewText=' + encodeURIComponent(reviewText)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    alert('Error updating review: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error updating review');
            });
        });

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('editReviewModal');
            if (event.target == modal) {
                closeEditModal();
            }
        }
    </script>

    <jsp:include page="footer.jsp" />
</body>
</html> 