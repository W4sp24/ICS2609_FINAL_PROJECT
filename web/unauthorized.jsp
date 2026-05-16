<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Unauthorized — Active Learning, Inc.</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; display: flex; flex-direction: column; min-height: 100vh; }
        header { background: #2c3e50; color: #fff; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; }
        header span.label { font-size: 13px; color: #bdc3c7; }
        .container { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 20px; }
        .card { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.12); padding: 40px 36px; max-width: 440px; width: 100%; text-align: center; }
        .icon { font-size: 56px; margin-bottom: 16px; }
        h2 { color: #c0392b; margin-bottom: 10px; font-size: 22px; }
        p { color: #555; line-height: 1.6; margin-bottom: 20px; }
        .error-msg { background: #fdecea; border: 1px solid #e57373; border-radius: 4px; padding: 10px 14px; color: #c0392b; font-size: 14px; margin-bottom: 20px; }
        a.btn { display: inline-block; background: #2c3e50; color: #fff; padding: 10px 28px; border-radius: 5px; text-decoration: none; font-weight: bold; font-size: 15px; transition: background 0.2s; }
        a.btn:hover { background: #34495e; }
    </style>
</head>
<body>
<header>
    <strong>Active Learning, Inc.</strong>
    <span class="label">Access Denied</span>
</header>
<div class="container">
    <div class="card">
        <div class="icon">🚫</div>
        <h2>Access Denied</h2>
        <% String authError = (session != null) ? (String) session.getAttribute("authError") : null; %>
        <% if (authError != null) { %>
            <div class="error-msg"><%= authError %></div>
        <% } else { %>
            <p>You do not have permission to access this page.<br>
               Please contact your administrator if you believe this is an error.</p>
        <% } %>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn">Return to Login</a>
    </div>
</div>
</body>
</html>
