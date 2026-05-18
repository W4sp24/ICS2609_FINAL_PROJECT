<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — Active Learning, Inc.</title>

    <!-- Google reCAPTCHA v2 API -->
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>

    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; display: flex; flex-direction: column; min-height: 100vh; }

        header {
            background: #2c3e50; color: #fff;
            padding: 12px 24px; display: flex;
            justify-content: space-between; align-items: center;
        }
        header span { font-size: 13px; color: #bdc3c7; }

        .container { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 20px; }

        .card {
            background: #fff; border-radius: 8px;
            box-shadow: 0 2px 16px rgba(0,0,0,0.13);
            padding: 40px 36px; width: 100%; max-width: 420px;
        }
        .card h2 { font-size: 18px; color: #2c3e50; margin-bottom: 6px; }
        .card .subtitle { font-size: 13px; color: #888; margin-bottom: 24px; }

        label { display: block; font-size: 13px; font-weight: bold; color: #333; margin-bottom: 5px; }

        input[type="text"], input[type="password"] {
            width: 100%; padding: 9px 12px; border: 1px solid #ccc;
            border-radius: 4px; font-size: 14px; margin-bottom: 16px;
            outline: none; transition: border-color 0.2s;
        }
        input:focus { border-color: #2c3e50; }

        .captcha-section { margin-bottom: 16px; }
        .captcha-label { font-size: 13px; font-weight: bold; color: #333; margin-bottom: 8px; display: block; }

        .btn-login {
            width: 100%; padding: 11px; background: #2c3e50; color: #fff;
            border: none; border-radius: 4px; font-size: 15px;
            font-weight: bold; cursor: pointer; transition: background 0.2s;
        }
        .btn-login:hover { background: #34495e; }

        .alert-success {
            background: #d4edda; border: 1px solid #c3e6cb;
            color: #155724; border-radius: 4px;
            padding: 10px 14px; font-size: 13px; margin-bottom: 16px;
        }
        .alert-error {
            background: #fdecea; border: 1px solid #ef9a9a;
            color: #c0392b; border-radius: 4px;
            padding: 10px 14px; font-size: 13px; margin-bottom: 16px;
        }
    </style>
</head>
<body>

<header>
    <strong>Active Learning, Inc.</strong>
    <span>Login Page</span>
</header>

<div class="container">
    <div class="card">
        <h2>Login</h2>
        <p class="subtitle">Full-Stack Secure Web Application</p>

        <c:if test="${param.loggedOut == 'true'}">
            <div class="alert-success">✔ You have been logged out successfully.</div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert-error">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/CaptchaServlet" method="post">

            <label for="username">Username</label>
            <input type="text"
                   id="username"
                   name="username"
                   placeholder="Enter username"
                   autocomplete="username"
                   required
                   maxlength="100">

            <label for="password">Password</label>
            <input type="password"
                   id="password"
                   name="password"
                   placeholder="Enter password"
                   autocomplete="current-password"
                   required
                   maxlength="255">

            <div class="captcha-section">
                <span class="captcha-label">CAPTCHA</span>
                <div class="g-recaptcha"
                     data-sitekey="${initParam['recaptchaSiteKey']}">
                </div>
            </div>

            <button type="submit" class="btn-login">LOGIN</button>
        </form>
    </div>
</div>

</body>
</html>
