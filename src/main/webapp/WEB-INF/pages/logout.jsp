<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Logout Confirmation</title>
    <style>
        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }

        .modal-content {
            background-color: #111;
            margin: 15% auto;
            padding: 20px;
            border: 1px solid #004000;
            border-radius: 10px;
            width: 300px;
            text-align: center;
            color: white;
        }

        .modal-buttons {
            margin-top: 20px;
            display: flex;
            justify-content: center;
            gap: 10px;
        }

        .btn {
            padding: 8px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
            transition: background-color 0.3s;
        }

        .btn-yes {
            background-color: #004000;
            color: white;
        }

        .btn-no {
            background-color: #333;
            color: white;
        }

        .btn:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <div id="logoutModal" class="modal">
        <div class="modal-content">
            <h2>Logout Confirmation</h2>
            <p>Are you sure you want to logout?</p>
            <div class="modal-buttons">
                <button class="btn btn-yes" onclick="confirmLogout()">Yes</button>
                <button class="btn btn-no" onclick="cancelLogout()">No</button>
            </div>
        </div>
    </div>

    <script>
        // Show modal when page loads
        window.onload = function() {
            document.getElementById('logoutModal').style.display = 'block';
        }

        function confirmLogout() {
            window.location.href = '${pageContext.request.contextPath}/logout/confirm';
        }

        function cancelLogout() {
            window.location.href = '${pageContext.request.contextPath}/profile';
        }
    </script>
</body>
</html> 