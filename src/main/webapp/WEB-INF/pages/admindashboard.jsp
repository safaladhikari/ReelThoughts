<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | Movie Management System</title>
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admindashboard.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <div class="dashboard-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2><i class="fas fa-film"></i> Movie Admin</h2>
            </div>
            <nav class="sidebar-nav">
                <ul>
                    <li class="active"><a href="#dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                    <li><a href="#users"><i class="fas fa-users"></i> Users</a></li>
                    <li><a href="#movies"><i class="fas fa-film"></i> Movies</a></li>
                    <li><a href="#analytics"><i class="fas fa-chart-line"></i> Analytics</a></li>
                    <li><a href="#settings"><i class="fas fa-cog"></i> Settings</a></li>
                </ul>
            </nav>
            <div class="sidebar-footer">
                <a href="#" class="logout-btn"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <header class="main-header">
                <h1>Admin Dashboard</h1>
                <div class="user-profile">
                    <span>Welcome, Admin</span>
                    <div class="avatar">
                        <i class="fas fa-user-circle"></i>
                    </div>
                </div>
            </header>

            <!-- Success/Error Messages -->
            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${success}
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            </c:if>

            <!-- Dashboard Overview -->
            <section class="dashboard-overview">
                <div class="stats-card">
                    <div class="card-icon green">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="card-info">
                        <h3>Total Users</h3>
                        <p><%= request.getAttribute("totalUsers") %></p>
                    </div>
                </div>
                <div class="stats-card">
                    <div class="card-icon green">
                        <i class="fas fa-film"></i>
                    </div>
                    <div class="card-info">
                        <h3>Total Movies</h3>
                        <p><%= request.getAttribute("totalMovies") %></p>
                    </div>
                </div>
                <div class="stats-card">
                    <div class="card-icon green">
                        <i class="fas fa-heart"></i>
                    </div>
                    <div class="card-info">
                        <h3>Favorites</h3>
                        <p><%= request.getAttribute("totalFavorites") %></p>
                    </div>
                </div>
                <div class="stats-card">
                    <div class="card-icon green">
                        <i class="fas fa-bookmark"></i>
                    </div>
                    <div class="card-info">
                        <h3>Watchlists</h3>
                        <p><%= request.getAttribute("totalWatchlists") %></p>
                    </div>
                </div>
            </section>

            <!-- Users Section -->
            <section id="users" class="content-section">
                <div class="section-header">
                    <h2><i class="fas fa-users"></i> User Management</h2>
                    <button class="btn btn-primary" onclick="showAddUserModal()">
                        <i class="fas fa-plus"></i> Add Admin
                    </button>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>First Name</th>
                                <th>Last Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Joined</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            Object users = request.getAttribute("users");
                            if (users != null && users instanceof java.util.List) {
                                java.util.List userList = (java.util.List) users;
                                for (Object userObj : userList) {
                                    java.util.Map<String, Object> user = (java.util.Map<String, Object>) userObj;
                            %>
                                <tr>
                                    <td><%= user.get("id") %></td>
                                    <td><%= user.get("firstName") %></td>
                                    <td><%= user.get("lastName") %></td>
                                    <td><%= user.get("email") %></td>
                                    <td><%= user.get("role") %></td>
                                    <td><%= user.get("joinDate") %></td>
                                    <td>
                                        <button class="btn btn-sm btn-edit" onclick="editUser(<%= user.get("id") %>)">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn btn-sm btn-delete" onclick="deleteUser(<%= user.get("id") %>)">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            <% 
                                }
                            } 
                            %>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- Movies Section -->
            <section id="movies" class="content-section">
                <div class="section-header">
                    <h2><i class="fas fa-film"></i> Movie Management</h2>
                    <button class="btn btn-primary" onclick="showAddMovieModal()">
                        <i class="fas fa-plus"></i> Add Movie
                    </button>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Poster</th>
                                <th>Title</th>
                                <th>Genre</th>
                                <th>Year</th>
                                <th>Director</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            Object movies = request.getAttribute("movies");
                            System.out.println("[DEBUG] Movies attribute in JSP: " + (movies != null ? "not null" : "null"));
                            if (movies != null) {
                                System.out.println("[DEBUG] Movies attribute type: " + movies.getClass().getName());
                                if (movies instanceof java.util.List) {
                                    java.util.List movieList = (java.util.List) movies;
                                    System.out.println("[DEBUG] Number of movies in list: " + movieList.size());
                                    for (Object movieObj : movieList) {
                                        java.util.Map<String, Object> movie = (java.util.Map<String, Object>) movieObj;
                                        System.out.println("[DEBUG] Processing movie: " + movie.get("title") + " (ID: " + movie.get("id") + ")");
                            %>
                                <tr>
                                    <td><%= movie.get("id") %></td>
                                    <td>
                                        <% if (movie.get("imageLink") != null) { %>
                                            <img src="<%= movie.get("imageLink") %>" alt="Movie Poster" class="movie-poster">
                                        <% } else { %>
                                            <div class="no-poster"><i class="fas fa-image"></i></div>
                                        <% } %>
                                    </td>
                                    <td><%= movie.get("title") %></td>
                                    <td><%= movie.get("genre") %></td>
                                    <td><%= movie.get("year") %></td>
                                    <td><%= movie.get("director") %></td>
                                    <td>
                                        <div class="action-buttons">
                                            <button class="btn btn-sm btn-update" onclick="updateMovie(<%= movie.get("id") %>)">
                                                <i class="fas fa-edit"></i> Update
                                            </button>
                                            <button class="btn btn-sm btn-delete" onclick="deleteMovie(<%= movie.get("id") %>)">
                                                <i class="fas fa-trash"></i> Delete
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            <% 
                                    }
                                } else {
                                    System.out.println("[DEBUG] Movies attribute is not a List instance");
                                }
                            } else { 
                                System.out.println("[DEBUG] No movies found or invalid movies attribute type");
                            %>
                                <tr>
                                    <td colspan="7" class="text-center">No movies found</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- Analytics Section -->
            <section id="analytics" class="content-section">
                <div class="section-header">
                    <h2><i class="fas fa-chart-line"></i> Analytics</h2>
                </div>
                <div class="analytics-grid">
                    <div class="analytics-card">
                        <h3>Top 5 Most Favorited Movies</h3>
                        <div class="chart-container">
                            <table class="analytics-table">
                                <thead>
                                    <tr>
                                        <th>Movie</th>
                                        <th>Favorites</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% 
                                    Object topFavoritedMovies = request.getAttribute("topFavoritedMovies");
                                    if (topFavoritedMovies != null && topFavoritedMovies instanceof java.util.List) {
                                        java.util.List topMovies = (java.util.List) topFavoritedMovies;
                                        for (Object movieObj : topMovies) {
                                            java.util.Map<String, Object> movie = (java.util.Map<String, Object>) movieObj;
                                    %>
                                        <tr>
                                            <td><%= movie.get("title") %></td>
                                            <td><%= movie.get("favoriteCount") %></td>
                                        </tr>
                                    <% 
                                        }
                                    } 
                                    %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="analytics-card">
                        <h3>Top 5 Most Watchlisted Movies</h3>
                        <div class="chart-container">
                            <table class="analytics-table">
                                <thead>
                                    <tr>
                                        <th>Movie</th>
                                        <th>Watchlists</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% 
                                    Object topWatchlistedMovies = request.getAttribute("topWatchlistedMovies");
                                    if (topWatchlistedMovies != null && topWatchlistedMovies instanceof java.util.List) {
                                        java.util.List topMovies = (java.util.List) topWatchlistedMovies;
                                        for (Object movieObj : topMovies) {
                                            java.util.Map<String, Object> movie = (java.util.Map<String, Object>) movieObj;
                                    %>
                                        <tr>
                                            <td><%= movie.get("title") %></td>
                                            <td><%= movie.get("watchlistCount") %></td>
                                        </tr>
                                    <% 
                                        }
                                    } 
                                    %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </section>
        </main>
    </div>

    <!-- Modals -->
<div id="addUserModal" class="modal">
    <div class="modal-content">
        <span class="close-btn" onclick="closeModal('addUserModal')">&times;</span>
        <h2>Add New Admin User</h2>
        <form id="addUserForm" action="admin/add-user" method="POST" enctype="multipart/form-data">
            <!-- Required fields matching RegisterController -->
            <div class="form-row">
                <div class="form-group">
                    <label for="firstName">First Name*</label>
                    <input type="text" id="firstName" name="firstName" required>
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name*</label>
                    <input type="text" id="lastName" name="lastName" required>
                </div>
            </div>
            
            <div class="form-group">
                <label for="email">Email* (@adminreelthoughts.com)</label>
                <input type="email" id="email" name="email" required 
                       pattern=".*@adminreelthoughts\.com$" 
                       title="Must use admin domain email">
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="password">Password*</label>
                    <input type="password" id="password" name="password" required minlength="8">
                </div>
                <div class="form-group">
                    <label for="retypePassword">Confirm Password*</label>
                    <input type="password" id="retypePassword" name="retypePassword" required minlength="8">
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="dob">Date of Birth*</label>
                    <input type="date" id="dob" name="dob" required max="<%= java.time.LocalDate.now().minusYears(18) %>">
                </div>
                <div class="form-group">
                    <label for="gender">Gender*</label>
                    <select id="gender" name="gender" required>
                        <option value="">Select Gender</option>
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                        <option value="other">Other</option>
                    </select>
                </div>
            </div>
            
            <div class="form-group">
                <label for="image">Profile Photo</label>
                <input type="file" id="image" name="image" accept="image/*">
            </div>
            
            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="closeModal('addUserModal')">Cancel</button>
                <button type="submit" class="btn btn-primary">Create Admin</button>
            </div>
        </form>
    </div>
</div>

    <div id="addMovieModal" class="modal">
        <div class="modal-content">
            <span class="close-btn" onclick="closeModal('addMovieModal')">&times;</span>
            <h2>Add New Movie</h2>
            <form id="addMovieForm" action="${pageContext.request.contextPath}/AddMovieServlet" method="POST" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="title">Title</label>
                    <input type="text" id="title" name="title" required>
                </div>
                <div class="form-group">
                    <label for="genre">Genre</label>
                    <input type="text" id="genre" name="genre" required>
                </div>
                <div class="form-group">
                    <label for="year">Release Year</label>
                    <input type="number" id="year" name="year" min="1900" max="2030" required>
                </div>
                <div class="form-group">
                    <label for="director">Director</label>
                    <input type="text" id="director" name="director" required>
                </div>
                <div class="form-group">
                    <label for="cast1">Cast Member 1</label>
                    <input type="text" id="cast1" name="cast1" required>
                </div>
                <div class="form-group">
                    <label for="cast2">Cast Member 2</label>
                    <input type="text" id="cast2" name="cast2">
                </div>
                <div class="form-group">
                    <label for="cast3">Cast Member 3</label>
                    <input type="text" id="cast3" name="cast3">
                </div>
                <div class="form-group">
                    <label for="imageLink">Movie Image URL</label>
                    <input type="text" id="imageLink" name="imageLink" required placeholder="https://example.com/movie-image.jpg">
                </div>
                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="4" required></textarea>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="closeModal('addMovieModal')">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Movie</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Modal functions
        function showAddUserModal() {
            document.getElementById('addUserModal').style.display = 'block';
        }

        function showAddMovieModal() {
            document.getElementById('addMovieModal').style.display = 'block';
        }

        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }

        // User management functions
        function editUser(userId) {
            // Implement edit functionality
            alert('Edit user with ID: ' + userId);
        }

        function deleteUser(userId) {
            if (confirm('Are you sure you want to delete this user?')) {
                window.location.href = 'DeleteUserServlet?id=' + userId;
            }
        }

        // Movie management functions
        function updateMovie(movieId) {
            // Implement update functionality
            alert('Update movie with ID: ' + movieId);
        }

        function deleteMovie(movieId) {
            if (confirm('Are you sure you want to delete this movie?')) {
                window.location.href = 'DeleteMovieServlet?id=' + movieId;
            }
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            if (event.target.className === 'modal') {
                event.target.style.display = 'none';
            }
        }
    </script>
</body>
</html>