<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CAPTCHA Failed — Active Learning, Inc.</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; display: flex; flex-direction: column; min-height: 100vh; }
        header { background: #2c3e50; color: #fff; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; }
        header span { font-size: 13px; color: #bdc3c7; }
        .container { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 20px; }
        .card { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.12); padding: 40px 36px; max-width: 440px; width: 100%; text-align: center; }
        .icon { font-size: 52px; margin-bottom: 16px; }
        h2 { color: #e67e22; margin-bottom: 12px; font-size: 22px; }
        .error-box { background: #fff8e1; border: 1px solid #ffe082; border-radius: 4px; padding: 10px 14px; color: #b45309; font-size: 14px; margin-bottom: 20px; }
        p { color: #666; margin-bottom: 20px; font-size: 14px; }
        a.btn { display: inline-block; background: #2c3e50; color: #fff; padding: 10px 28px; border-radius: 5px; text-decoration: none; font-weight: bold; font-size: 15px; }
        a.btn:hover { background: #34495e; }
    </style>
</head>
<body>
<header>
    <strong>Active Learning, Inc.</strong>
    <span>CAPTCHA Failed</span>
</header>
<div class="container">
    <div class="card">
        <div class="icon">🤖</div>
        <h2>CAPTCHA Failed</h2>
        <div class="error-box">
            <%= request.getAttribute("errorMessage") != null
                ? request.getAttribute("errorMessage")
                : "CAPTCHA verification failed. Please try again." %>
        </div>
        <% Object attemptsLeft = request.getAttribute("attemptsLeft");
           if (attemptsLeft != null) { %>
            <p>You have <strong><%= attemptsLeft %> attempt(s)</strong> remaining.</p>
        <% } %>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn">← Try Again</a>
    </div>
</div>
</body>
</html>
