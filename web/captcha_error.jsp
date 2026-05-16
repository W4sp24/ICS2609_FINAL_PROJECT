<%-- ============================================================
     FILE: captcha_error.jsp
     Shown after 3 consecutive CAPTCHA failures (lockout).
     Displays a countdown or remaining wait time.
     ============================================================ --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CAPTCHA Locked — Active Learning, Inc.</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; display: flex; flex-direction: column; min-height: 100vh; }
        header { background: #2c3e50; color: #fff; padding: 12px 24px; display: flex; justify-content: space-between; }
        header span { font-size: 13px; color: #bdc3c7; }
        .container { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 20px; }
        .card { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.12); padding: 40px 36px; max-width: 460px; width: 100%; text-align: center; }
        .icon { font-size: 52px; margin-bottom: 16px; }
        h2 { color: #c0392b; margin-bottom: 12px; font-size: 22px; }
        .error-box { background: #fdecea; border: 1px solid #ef9a9a; border-radius: 4px; padding: 12px 16px; color: #c62828; font-size: 14px; margin-bottom: 20px; }
        .timer-box { background: #fff3e0; border: 1px solid #ffb74d; border-radius: 4px; padding: 12px 16px; color: #e65100; font-size: 18px; font-weight: bold; margin-bottom: 20px; }
        p { color: #666; line-height: 1.6; margin-bottom: 20px; font-size: 14px; }
        a.btn { display: inline-block; background: #7f8c8d; color: #fff; padding: 10px 28px; border-radius: 5px; text-decoration: none; font-weight: bold; font-size: 15px; }
        #countdown { font-size: 22px; font-weight: bold; color: #c0392b; }
    </style>
</head>
<body>
<header>
    <strong>Active Learning, Inc.</strong>
    <span>CAPTCHA Lockout</span>
</header>
<div class="container">
    <div class="card">
        <div class="icon">🔒</div>
        <h2>Too Many Failed Attempts</h2>

        <div class="error-box">
            You have exceeded the maximum number of CAPTCHA attempts (3).<br>
            Login has been temporarily disabled.
        </div>

        <c:choose>
            <c:when test="${not empty remainingSeconds}">
                <div class="timer-box">
                    Please wait: <span id="countdown">${remainingSeconds}</span> second(s)
                </div>
            </c:when>
            <c:otherwise>
                <div class="timer-box">Please wait <strong>5 minutes</strong> before trying again.</div>
            </c:otherwise>
        </c:choose>

        <p>After the lockout period expires, return to the login page and try again.<br>
           If you continue to have trouble, contact your system administrator.</p>

        <a href="${pageContext.request.contextPath}/index.jsp" class="btn">Back to Login Page</a>
    </div>
</div>

<c:if test="${not empty remainingSeconds}">
<script>
    let seconds = parseInt("${remainingSeconds}", 10);
    const el = document.getElementById("countdown");

    if (el && seconds > 0) {
        const timer = setInterval(() => {
            seconds--;
            if (seconds <= 0) {
                clearInterval(timer);
                el.textContent = "0";
                el.closest(".timer-box").innerHTML = "Lockout expired. You may try again.";
            } else {
                el.textContent = seconds;
            }
        }, 1000);
    }
</script>
</c:if>

</body>
</html>
