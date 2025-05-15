<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recent Releases | MovieHub</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recentreleases.css">
    <style>
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
    </style>
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
                <!-- Movie Card 1 -->
                <div class="movie-card">
                    <div class="movie-poster">
                        <img src="${pageContext.request.contextPath}/resources/images/system/dune.jpg" alt="Dune">
                        <div class="rating-badge">8.7</div>
                    </div>
                    <div class="movie-info">
                        <h3>Dune: Part Two</h3>
                        <p class="meta">2024 • Sci-Fi • 2h 46m</p>
                        <div class="actions">
                            <button class="watchlist-btn"><i class="far fa-bookmark"></i> Watchlist</button>
                            <button class="rate-btn"><i class="far fa-star"></i> Rate</button>
                        </div>
                    </div>
                </div>

                <!-- Movie Card 2 -->
                <div class="movie-card">
                    <div class="movie-poster">
                        <img src="${pageContext.request.contextPath}/resources/images/system/openheimer.jpg" alt="Openheimer">
                        <div class="rating-badge">7.9</div>
                    </div>
                    <div class="movie-info">
                        <h3>Oppenheimer</h3>
                        <p class="meta">2023 • Biography • 3h</p>
                        <div class="actions">
                            <button class="watchlist-btn"><i class="far fa-bookmark"></i> Watchlist</button>
                            <button class="rate-btn"><i class="far fa-star"></i> Rate</button>
                        </div>
                    </div>
                </div>

                <!-- Movie Card 3 -->
                <div class="movie-card">
                    <div class="movie-poster">
                        <img src="${pageContext.request.contextPath}/resources/images/system/the godfather.jpg" alt="The Godfather">
                        <div class="rating-badge">9.1</div>
                    </div>
                    <div class="movie-info">
                        <h3>The Godfather</h3>
                        <p class="meta">1972 • Crime • 2h 55m</p>
                        <div class="actions">
                            <button class="watchlist-btn"><i class="far fa-bookmark"></i> Watchlist</button>
                            <button class="rate-btn"><i class="far fa-star"></i> Rate</button>
                        </div>
                    </div>
                </div>

                <!-- Movie Card 4 -->
                <div class="movie-card">
                    <div class="movie-poster">
                        <img src="${pageContext.request.contextPath}/resources/images/system/parasite.jpg" alt="Parasite">
                        <div class="rating-badge">8.4</div>
                    </div>
                    <div class="movie-info">
                        <h3>Parasite</h3>
                        <p class="meta">2019 • Thriller • 2h 12m</p>
                        <div class="actions">
                            <button class="watchlist-btn"><i class="far fa-bookmark"></i> Watchlist</button>
                            <button class="rate-btn"><i class="far fa-star"></i> Rate</button>
                        </div>
                    </div>
                </div>

                <!-- Movie Card 5 -->
                <div class="movie-card">
                    <div class="movie-poster">
                        <img src="${pageContext.request.contextPath}/resources/images/system/snowhite.jpg" alt="Snow White">
                        <div class="rating-badge">1.6</div>
                    </div>
                    <div class="movie-info">
                        <h3>Snowwhite</h3>
                        <p class="meta">2025 • Fantasy • 1h 49m</p>
                        <div class="actions">
                            <button class="watchlist-btn"><i class="far fa-bookmark"></i> Watchlist</button>
                            <button class="rate-btn"><i class="far fa-star"></i> Rate</button>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Community Highlights -->
        <section class="community-section">
            <div class="section-header">
                <h2>Community Highlights</h2>
            </div>
            <div class="community-grid">
                <div class="highlight-card review-card">
                    <div class="user-info">
                        <img src="${pageContext.request.contextPath}/resources/images/system/user avatar.avif" alt="User Avatar" class="user-avatar">
                        <span class="username">@filmcritic101</span>
                    </div>
                    <div class="review-content">
                        <h3>Review of "Everything Everywhere All at Once"</h3>
                        <div class="star-rating">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star-half-alt"></i>
                            <span>4.5/5</span>
                        </div>
                        <p class="review-excerpt">"A mind-bending masterpiece that combines multiverse theory with heartfelt family drama..."</p>
                        <a href="#" class="read-more">Read full review</a>
                    </div>
                </div>

                <div class="highlight-card list-card">
                    <h3>Top 10 Sci-Fi Movies of the Decade</h3>
                    <p class="list-author">by @scifilover</p>
                    <ul class="list-preview">
                        <li>1. Dune (2021)</li>
                        <li>2. Arrival (2016)</li>
                        <li>3. Ex Machina (2014)</li>
                    </ul>
                    <a href="#" class="read-more">View full list</a>
                </div>
            </div>
        </section>

        <!-- Newsletter Signup -->
        <section class="newsletter">
            <div class="newsletter-content">
                <h2>Stay Updated</h2>
                <p>Subscribe to our newsletter for weekly movie recommendations and community highlights</p>
                <form class="signup-form">
                    <input type="email" placeholder="Your email address">
                    <button type="submit">Subscribe</button>
                </form>
            </div>
        </section>
    </main>

    <footer class="site-footer">
        <div class="footer-container compact-layout">
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
    
    <!-- slider for the homepage-->
    <script>
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