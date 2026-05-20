<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin Dashboard</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/adminDashboard.css">
</head>
<body>
    <div id="background">
        <img class="twilight" src="${pageContext.request.contextPath}/images/Twilight.png">
            <img class="layer mountain far" src="${pageContext.request.contextPath}/images/mountain-far.png">
                    <img class="layer mountain mid" src="${pageContext.request.contextPath}/images/mountain-mid.png">
                    <img class="layer mountain near" src="${pageContext.request.contextPath}/images/mountain-near.png">
                    <!-- foreground -->
                    <img class="layer greenery" src="${pageContext.request.contextPath}/images/greenery2.png">
    </div>
    <!--sidebar for nav -->
    <aside class="sidebar">
        <div>
            <img src="${pageContext.request.contextPath}/images/logo-small.png" class="sidebar-logo">
            <div class="sidebar-title">ADMIN PANEL</div>
            <nav class="sidebar-nav">
                <a href="AdminDashboard" class="active">Dashboard</a>
                <a href="Course">Courses</a>
                <a href="Grade">Grade Submissions</a>
                <a href="UserManagement">Manage Students</a>
                <a href="ReportServlet">Reports</a>
            </nav>
        </div>
        <div class="sidebar-bottom">
            <a href="#">Help</a>
            <a href="LogoutServlet">Logout</a>
        </div>
    </aside>
    <!--main dashboard-->
    <main class="main-content">
        <!-- nav bar on top-->
        <header class="top-bar">
            <div class="search-box">
                <input type="text" placeholder="Search Here">
            </div>
            <div class="top-actions">
                <button class="top-btn">💌</button>
                <button class="top-btn">🔔</button>
                <div class="profile-box">
                    <!--part to put pfp if possible pero baka wag na ewan-->
                    <img src="${pageContext.request.contextPath}/images/!!insertProfilePicHere!!.png">
                    <div>
                        <h4>${sessionScope.username}</h4>
                        <p>System Admin</p>
                    </div>
                </div>
            </div>
        </header>
        <section class="hero-card">
            <div class="hero-content">
                <h1>
                    Welcome Back,<br>
                    ${sessionScope.username}
                </h1>
                <p>
                    Here's what's happening today.
                </p>
                <div class="hero-buttons">
                    <button onclick="location.href='UserManagement'">Manage Students</button>
                    <button onclick="location.href='Course'">Manage Courses</button>
                </div>
            </div>
            <img src="${pageContext.request.contextPath}/images/flower-book.png" class="hero-image">
        </section>
        <!--for showing stats,: placeholders pa lang-->
        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">👤</div>
                <div>
                    <h2>${totalStudents}</h2>
                    <p>Total Students</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📚</div>
                <div>
                    <h2>${totalCourses}</h2>
                    <p>Courses</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">🎓</div>
                <div>
                    <h2>${totalTeachers}</h2>
                    <p>Teachers</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📜</div>
                <div>
                    <h2>${mysqlRole}</h2>
                    <p>Your Role</p>
                </div>
            </div>
        </section>
        <section class="lower-grid">
            <div class="panel">
                <div class="panel-header">
                    <h2>Quick Actions</h2>
                </div>
                <div class="quick-actions">
                    <button onclick="location.href='UserManagement'">Manage Students</button>
                    <button onclick="location.href='Course'">Manage Courses</button>
                    <button onclick="location.href='Grade'">Grade Submissions</button>
                </div>
            </div>
            <div class="panel">
                <div class="panel-header">
                    <h2>Your Role</h2>
                </div>
                <div style="padding:20px 0;text-align:center;font-size:15px;color:rgba(255,255,255,0.75)">
                    Logged in as <strong>${mysqlRole}</strong>
                </div>
            </div>
        </section>
    </main>
</body>
</html>