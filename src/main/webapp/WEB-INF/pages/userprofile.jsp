<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ReelThoughts Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userprofile.css">
    <!-- Font Awesome for social icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <script src="https://kit.fontawesome.com/your-kit-code.js" crossorigin="anonymous"></script>
</head>
<body>
    <header>
        <nav class="navbar">
            <div class="logo">ReelThoughts.</div>
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
    
    <div class="container">
        <!-- Main Content - Now on the left -->
        <main class="main-content">
            <section class="section">
                <h2>Recent Favorites
                    <a href="${pageContext.request.contextPath}/favorites" class="view-more">View All →</a>
                </h2>
                <div class="films-container">
                    <a href="goodfellas.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BY2NkZjEzMDgtN2RjYy00YzM1LWI4ZmQtMjIwYjFjNmI3ZGEwXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg" alt="Goodfellas">
                        </div>
                        <div class="film-title">Goodfellas</div>
                        <div class="film-year">1990</div>
                    </a>
                    <a href="inthemoodforlove.html" class="film-card">
                        <div class="film-poster">
                            <img src="${pageContext.request.contextPath}/resources/images/system/moodforlove.jpg"  alt="In the Mood for Love">
                        </div>
                        <div class="film-title">In the Mood for Love</div>
                        <div class="film-year">2000</div>
                    </a>
                    <a href="woodjob.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMTY5NjI2MjQxMl5BMl5BanBnXkFtZTgwMDA2MzM2NzE@._V1_FMjpg_UX1000_.jpg" alt="Wood Job!">
                        </div>
                        <div class="film-title">Wood Job!</div>
                        <div class="film-year">2014</div>
                    </a>
                    <a href="nocountryforoldmen.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMjA5NjkxMjg2Nl5BMl5BanBnXkFtZTcwMTkyMTc1Mw@@._V1_FMjpg_UX1000_.jpg" alt="No Country for Old Men">
                        </div>
                        <div class="film-title">No Country for Old Men</div>
                        <div class="film-year">2007</div>
                    </a>
                </div>
            </section>
            
            <section class="section">
                <h2>Recent Reviews
                    <a href="${pageContext.request.contextPath}/reviews" class="view-more">View All →</a>
                </h2>
                <div class="films-container">
                    <a href="shawshank.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_FMjpg_UX1000_.jpg" alt="The Shawshank Redemption">
                        </div>
                        <div class="film-title">The Shawshank Redemption</div>
                        <div class="film-year">1994</div>
                    </a>
                    <a href="godfather.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg" alt="The Godfather">
                        </div>
                        <div class="film-title">The Godfather</div>
                        <div class="film-year">1972</div>
                    </a>
                    <a href="pulpfiction.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWZiLWIyMDctNDk2ZDQ2YjRjMWQ0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg" alt="Pulp Fiction">
                        </div>
                        <div class="film-title">Pulp Fiction</div>
                        <div class="film-year">1994</div>
                    </a>
                    <a href="schindlerslist.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMTUxMzQyNjA5MF5BMl5BanBnXkFtZTYwOTU2NTY3._V1_FMjpg_UX1000_.jpg" alt="Schindler's List">
                        </div>
                        <div class="film-title">Schindler's List</div>
                        <div class="film-year">1993</div>
                    </a>
                </div>
            </section>
            
            <section class="section">
                <h2>Watch List
                    <a href="${pageContext.request.contextPath}/watchlist" class="view-more">View All →</a>
                </h2>
                <div class="films-container">
                    <a href="shawshank.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_FMjpg_UX1000_.jpg" alt="The Shawshank Redemption">
                        </div>
                        <div class="film-title">The Shawshank Redemption</div>
                        <div class="film-year">1994</div>
                    </a>
                    <a href="godfather.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg" alt="The Godfather">
                        </div>
                        <div class="film-title">The Godfather</div>
                        <div class="film-year">1972</div>
                    </a>
                    <a href="pulpfiction.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWZiLWIyMDctNDk2ZDQ2YjRjMWQ0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg" alt="Pulp Fiction">
                        </div>
                        <div class="film-title">Pulp Fiction</div>
                        <div class="film-year">1994</div>
                    </a>
                    <a href="schindlerslist.html" class="film-card">
                        <div class="film-poster">
                            <img src="https://m.media-amazon.com/images/M/MV5BMTUxMzQyNjA5MF5BMl5BanBnXkFtZTYwOTU2NTY3._V1_FMjpg_UX1000_.jpg" alt="Schindler's List">
                        </div>
                        <div class="film-title">Schindler's List</div>
                        <div class="film-year">1993</div>
                    </a>
                </div>
            </section>
        </main>
        
        <!-- Sidebar - Now on the right -->
        <aside class="sidebar">
            <div class="profile-header">
                <div class="profile-pic">
                    <img src="${pageContext.request.contextPath}/resources/images/system/akira.jpg" alt="Profile" style="width:100%; height:100%; object-fit:cover;">
                </div>
                <div class="profile-name">Safal Adhikari</div>
                
            </div>
            <ul>
                <li><a href="${pageContext.request.contextPath}/edit-profile">Edit Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/favorites">Favorites</a></li>
                <li><a href="${pageContext.request.contextPath}/ratings">Ratings</a></li>
                <li><a href="${pageContext.request.contextPath}/reviews">Reviews</a></li>
                <li><a href="${pageContext.request.contextPath}/watchlist">Watchlist</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
            </ul>
        </aside>
    </div>

    <!-- Footer Section -->
    <footer>
        <div class="footer-container">
            <div class="footer-about">
                <div class="footer-logo">ReelThoughts.</div>
                <p>Your ultimate movie companion for reviews, ratings, and recommendations.</p>
            </div>
            <div class="footer-contact">
                <h3>Contact Us</h3>
                <p>Kamalpokhari, Kathmandu</p>
                <p>Phone: +977 9841XXXXXX</p>
            </div>
            <div class="footer-social-links">
                <h3>Follow Us</h3>
                <div class="footer-social">
                    <a href="#" aria-label="Facebook"><i class="fab fa-facebook"></i></a>
                    <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                    <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
                    <a href="#" aria-label="YouTube"><i class="fab fa-youtube"></i></a>
                </div>
            </div>
        </div>
        <div class="copyright">
            <p>&copy; 2025 ReelThoughts. All rights reserved.</p>
        </div>
    </footer>
</body>
</html>