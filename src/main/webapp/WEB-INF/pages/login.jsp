<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ReelThoughts - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
    <div class="page-container">
        <!-- Left Section (Same as registration) -->
        <div class="welcome-section">
            <div class="welcome-content">
                <h1>Welcome to ReelThoughts.</h1>
                <p class="subtitle">Join Like-Minded Movie Enthusiasts and Critics</p>
                <div class="features">
                    <div class="feature-item">
                        <span class="feature-icon">✓</span>
                        <span>Rate and Review Movies</span>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">✓</span>
                        <span>Explore and Manage New Movies</span>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">✓</span>
                        <span>Share your thoughts and favorites</span>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Right Section with Card Form -->
        <div class="form-section">
            <div class="login-card">
                <div class="form-header">
                    <h2>Sign In</h2>
                    <p class="register-prompt">New to ReelThoughts?   <a href="${pageContext.request.contextPath}/register">Create account</a></p>
                </div>
                
                <c:if test="${not empty success}">
                    <p class="success-message">${success}</p>
                </c:if>
                
                <c:if test="${not empty error}">
                    <p class="error-message">${error}</p>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="form-group">
                        <input type="email" id="email" name="email" value="${email}" placeholder="Email" required>
                    </div>
                    
                    <div class="form-group">
                        <input type="password" id="password" name="password" placeholder="Password" required>
                    </div>
                    
                    <div class="form-options">
                        <label class="remember-me">
                            <input type="checkbox" name="remember"> Remember me
                        </label>
                        <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password">Forgot password?</a>
                    </div>
                    
                    <button type="submit">Sign In</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>



<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ReelThoughts - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
    <div class="page-container">
        <!-- Left Section (Same as registration) -->
        <div class="welcome-section">
            <div class="welcome-content">
                <h1>Welcome to ReelThoughts.</h1>
                <p class="subtitle">Join Like-Minded Movie Enthusiasts and Critics</p>
                <div class="features">
                    <div class="feature-item">
                        <span class="feature-icon">✓</span>
                        <span>Rate and Review Movies</span>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">✓</span>
                        <span>Explore and Manage New Movies</span>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">✓</span>
                        <span>Share your thoughts and favorites</span>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Right Section with Card Form -->
        <div class="form-section">
            <div class="login-card">
                <div class="form-header">
                    <h2>Sign In</h2>
                    <p class="register-prompt">New to ReelThoughts?   <a href="${pageContext.request.contextPath}/register">Create account</a></p>
                </div>
                
                <c:if test="${not empty error}">
                    <p class="error-message">${error}</p>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="form-group">
                        <input type="email" id="email" name="email" value="${email}" placeholder="Email" required>
                    </div>
                    
                    <div class="form-group">
                        <input type="password" id="password" name="password" placeholder="Password" required>
                    </div>
                    
                    <div class="form-options">
                        <label class="remember-me">
                            <input type="checkbox" name="remember"> Remember me
                        </label>
                        <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password">Forgot password?</a>
                    </div>
                    
                    <button type="submit">Sign In</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html> --%>