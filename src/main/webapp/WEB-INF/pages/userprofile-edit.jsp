<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Profile - ReelThoughts</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/registration.css">
</head>
<body>
    <div class="page-container">
        <div class="welcome-section">
            <div class="welcome-content">
                <h1>Edit Your Profile</h1>
                <p class="subtitle">Keep your info fresh and updated</p>
                <div class="features">
                    <div class="feature-item">
                        <span class="feature-icon">✎</span>
                        <span>Update personal details anytime</span>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">✎</span>
                        <span>Refresh your profile picture</span>
                    </div>
                    <div class="feature-item">
                        <span class="feature-icon">✎</span>
                        <span>Maintain secure access</span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="form-section">
            <div class="registration-card">
                <div class="form-header">
                    <h2>Edit Profile</h2>
                    <p class="login-prompt"><a href="userprofile.jsp">← Back to Profile</a></p>
                </div>

                <c:if test="${not empty error}">
                    <p class="error-message">${error}</p>
                </c:if>
                
                <c:if test="${not empty success}">
                    <p class="success-message">${success}</p>
                </c:if>

                <form action="${pageContext.request.contextPath}/edit-profile" method="post" enctype="multipart/form-data">
                    <div class="form-row">
                        <div class="form-group">
                            <input type="text" id="firstName" name="firstName" value="${user.firstName}" placeholder="First Name" required>
                        </div>
                        <div class="form-group">
                            <input type="text" id="lastName" name="lastName" value="${user.lastName}" placeholder="Last Name" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <input type="email" id="email" name="email" value="${user.email}" placeholder="Email" required>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <input type="password" id="password" name="password" placeholder="New Password (optional)">
                        </div>
                        <div class="form-group">
                            <input type="password" id="retypePassword" name="retypePassword" placeholder="Confirm New Password">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <input type="date" id="birthday" name="dob" value="${user.dob}" required>
                        </div>
                        <div class="form-group">
                            <select id="gender" name="gender" required>
                                <option value="">Gender</option>
                                <option value="male" ${user.gender == 'male' ? 'selected' : ''}>Male</option>
                                <option value="female" ${user.gender == 'female' ? 'selected' : ''}>Female</option>
                                <option value="other" ${user.gender == 'other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group file-upload">
                        <label for="image">Change Profile Photo</label>
                        <input type="file" id="image" name="image" accept="image/*">
                    </div>

                    <button type="submit">Save Changes</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
