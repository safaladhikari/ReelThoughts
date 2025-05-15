<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ReelThoughts - Create Account</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/registration.css">
</head>
<body>
    <div class="page-container">
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
        
        <div class="form-section">
            <div class="registration-card">
                <div class="form-header">
                    <h2>Create Account</h2>
                    <p class="login-prompt">Already a member? <a href="${pageContext.request.contextPath}/pages/login.jsp">Sign in</a></p>
                </div>
                
                <c:if test="${not empty error}">
                    <p class="error-message">${error}</p>
                </c:if>
                
                <c:if test="${not empty success}">
                    <p class="success-message">${success}</p>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/register" method="post" enctype="multipart/form-data">
                    <div class="form-row">
                        <div class="form-group">
                            <input type="text" id="firstName" name="firstName" value="${firstName}" placeholder="First Name" required>
                        </div>
                        <div class="form-group">
                            <input type="text" id="lastName" name="lastName" value="${lastName}" placeholder="Last Name" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <input type="email" id="email" name="email" value="${email}" placeholder="Email" required>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <input type="password" id="password" name="password" placeholder="Password" required>
                        </div>
                        <div class="form-group">
                            <input type="password" id="retypePassword" name="retypePassword" placeholder="Confirm Password" required>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <input type="date" id="birthday" name="dob" value="${dob}" required>
                        </div>
                        <div class="form-group">
                            <select id="gender" name="gender" required>
                                <option value="">Gender</option>
                                <option value="male" ${gender == 'male' ? 'selected' : ''}>Male</option>
                                <option value="female" ${gender == 'female' ? 'selected' : ''}>Female</option>
                                <option value="other" ${gender == 'other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-group file-upload">
                        <label for="image">Profile Photo</label>
                        <input type="file" id="image" name="image" accept="image/*">
                    </div>
                    
                    <button type="submit">Register Now</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>