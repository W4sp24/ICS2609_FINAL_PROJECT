<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Error — Active Learning, Inc.</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; display: flex; flex-direction: column; min-height: 100vh; }
        header { background: #2c3e50; color: #fff; padding: 12px 24px; display: flex; justify-content: space-between; }
        header span { font-size: 13px; color: #bdc3c7; }
        .container { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 20px; }
        .card { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.12); padding: 40px 36px; max-width: 440px; width: 100%; text-align: center; }
        .icon { font-size: 52px; margin-bottom: 16px; }
        h2 { color: #c0392b; margin-bottom: 12px; font-size: 22px; }
        .error-box { background: #fdecea; border: 1px solid #ef9a9a; border-radius: 4px; padding: 10px 14px; color: #c62828; font-size: 14px; margin-bottom: 20px; text-align: left; }
        a.btn { display: inline-block; background: #2c3e50; color: #fff; padding: 10px 28px; border-radius: 5px; text-decoration: none; font-weight: bold; font-size: 15px; }
        a.btn:hover { background: #34495e; }
    </style>
</head>
<body>
<header>
    <strong>Active Learning, Inc.</strong>
    <span>Login Page</span>
</header>
<div class="container">
    <div class="card">
        <div class="icon">👤</div>
        <h2>Invalid Username</h2>
        <div class="error-box">
            <c:choose>
                <c:when test="${not empty errorMessage}">${errorMessage}</c:when>
                <c:otherwise>The username you entered is invalid or does not exist.</c:otherwise>
            </c:choose>
        </div>
        <p style="color:#666;margin-bottom:20px;">Please check your username and try again.</p>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn">← Back to Login</a>
    </div>
</div>
</body>
</html>
