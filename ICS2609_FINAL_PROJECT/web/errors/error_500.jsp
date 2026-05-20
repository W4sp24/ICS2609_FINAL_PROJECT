<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error — Active Learning, Inc.</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; display: flex; flex-direction: column; min-height: 100vh; }
        header { background: #2c3e50; color: #fff; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; }
        header span { font-size: 13px; color: #bdc3c7; }
        .container { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 20px; }
        .card { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.12); padding: 40px 36px; max-width: 440px; width: 100%; text-align: center; }
        .icon { font-size: 52px; margin-bottom: 16px; }
        h2 { color: #c0392b; margin-bottom: 12px; font-size: 22px; }
        p { color: #666; margin-bottom: 20px; line-height: 1.6; font-size: 14px; }
        .error-box { background: #fdecea; border: 1px solid #ef9a9a; border-radius: 4px; padding: 10px 14px; color: #c62828; font-size: 13px; margin-bottom: 20px; text-align: left; font-family: monospace; word-break: break-word; }
        a.btn { display: inline-block; background: #2c3e50; color: #fff; padding: 10px 28px; border-radius: 5px; text-decoration: none; font-weight: bold; font-size: 15px; }
        a.btn:hover { background: #34495e; }
    </style>
</head>
<body>
<header>
    <strong>Active Learning, Inc.</strong>
    <span>500 Server Error</span>
</header>
<div class="container">
    <div class="card">
        <div class="icon">⚠️</div>
        <h2>Internal Server Error</h2>
        <p>Something went wrong on our end. Please try again or contact your administrator.</p>
        <% if (exception != null) { %>
            <div class="error-box"><%= exception.getMessage() %></div>
        <% } %>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn">← Back to Login</a>
    </div>
</div>
</body>
</html>
