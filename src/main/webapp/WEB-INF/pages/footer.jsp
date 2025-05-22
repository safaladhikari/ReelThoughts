<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Footer</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/footer.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
	<footer>
		<div class="footer-container">
			<div class="footer-brand">
				<div class="footer-logo">ReelThoughts</div>
				<p>Your trusted source for movie ratings<br>and reviews</p>
			</div>
			<div class="footer-links">
				<div class="footer-column">
					<h3>Explore</h3>
					<ul>
						<li><a href="#">Movies</a></li>
						<li><a href="#">Top Rated</a></li>
						<li><a href="#">Coming Soon</a></li>
					</ul>
				</div>
				<div class="footer-column">
					<h3>Community</h3>
					<ul>
						<li><a href="#">Discussions</a></li>
						<li><a href="#">Leaderboard</a></li>
						<li><a href="#">Events</a></li>
					</ul>
				</div>
				<div class="footer-column">
					<h3>Company</h3>
					<ul>
						<li><a href="#">About Us</a></li>
						<li><a href="#">Careers</a></li>
						<li><a href="#">Contact</a></li>
					</ul>
				</div>
			</div>
			<div class="footer-connect">
				<h3>Connect</h3>
				<div class="social-icons">
					<a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
					<a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
					<a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
					<a href="#" aria-label="YouTube"><i class="fab fa-youtube"></i></a>
					<a href="#" aria-label="TikTok"><i class="fab fa-tiktok"></i></a>
				</div>
			</div>
		</div>
		<div class="footer-bottom">
			<div class="footer-policies">
				<a href="#">Terms of Use</a>
				<a href="#">Privacy Policy</a>
				<a href="#">Cookie Policy</a>
			</div>
			<div class="copyright">
				&copy; 2024 CineRate. All rights reserved.
			</div>
		</div>
	</footer>
</body>
</html>