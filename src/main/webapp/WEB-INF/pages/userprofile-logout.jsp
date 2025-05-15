<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Confirm Logout</title>
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: rgba(0, 0, 0, 0.75);
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
        }

        .logout-modal {
            background: rgba(255, 255, 255, 0.15);
            backdrop-filter: blur(15px);
            -webkit-backdrop-filter: blur(15px);
            border-radius: 16px;
            padding: 2.5rem 3rem;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
            text-align: center;
            border: 1px solid rgba(255, 255, 255, 0.18);
            animation: fadeIn 0.3s ease;
        }

        .logout-modal h2 {
            font-size: 1.6rem;
            color: #ffffff;
            margin-bottom: 1.5rem;
            font-weight: 500;
        }

        .logout-buttons {
            display: flex;
            justify-content: center;
            gap: 1rem;
        }

        .logout-buttons button {
            padding: 0.7rem 1.5rem;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.25s ease;
        }

        .btn-yes {
            background: #ff4d4d;
            color: white;
        }

        .btn-yes:hover {
            background: #e60000;
        }

        .btn-no {
            background: #5cdb5c;
            color: white;
        }

        .btn-no:hover {
            background: #2eb82e;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body>

<div class="logout-modal">
    <h2>Are you sure you want to logout?</h2>
    <div class="logout-buttons">
        <form action="logout.jsp" method="post">
            <button type="submit" class="btn-yes">Yes, Logout</button>
        </form>
        <button onclick="goBack()" class="btn-no">No, Stay</button>
    </div>
</div>

<script>
    function goBack() {
        window.history.back();
    }
</script>

</body>
</html>
