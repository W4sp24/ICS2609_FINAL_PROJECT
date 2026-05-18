<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin Dashboard</title>

    <link rel="stylesheet" href="css/adminDashboard.css">
</head>
<body>
    <div id="background">
        <img class="twilight" src="images/Twilight.png">
            <img class="layer mountain far" src="images/mountain-far.png">
                    <img class="layer mountain mid" src="images/mountain-mid.png">
                    <img class="layer mountain near" src="images/mountain-near.png">
                    <!-- foreground -->
                    <img class="layer greenery" src="images/greenery2.png">
    </div>
    <!--sidebar for nav -->
    <aside class="sidebar">
        <div>
            <img src="images/logo-small.png" class="sidebar-logo">
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
                    <img src="images/!!insertProfilePicHere!!.png">
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
                    <button onclick="location.href='ReportServlet'">Generate Reports</button>
                </div>
            </div>
            <img src="images/flower-book.png" class="hero-image">
        </section>
        <!--for showing stats,: only placeholders are here for now-->
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
                <div class="stat-icon">⭐</div>
                <div>
                    <h2>N/A</h2>
                    <p>Completion Rate</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📜</div>
                <div>
                    <h2>N/A</h2>
                    <p>Reports</p>
                </div>
            </div>
        </section>
        <!--lower pane;l for recent activity-->
        <section class="lower-grid">
            <div class="panel">
                <div class="panel-header">
                    <h2>Recent Activity</h2>
                    <button>View All</button>
                </div>
                <div class="activity-list">
                    <%-- TODO: replace with c:forEach over activityLogs once PostgresQLDAO.getLogs() is implemented --%>
                    <div class="activity-item">
                        <div class="activity-avatar"></div>
                        <div>
                            <h4>No recent activity</h4>
                            <p>Activity log coming soon</p>
                        </div>
                    </div>
                </div>
            </div>
            <!--lower planel for quick actions-->
            <div class="panel">
                <div class="panel-header">
                    <h2>Quick Actions</h2>
                </div>
                <div class="quick-actions">
                    <button onclick="location.href='UserManagement'">Manage Students</button>
                    <button onclick="location.href='Course'">Manage Courses</button>
                    <button onclick="location.href='Grade'">Grade Submissions</button>
                    <button onclick="location.href='ReportServlet'">Generate Reports</button>
                </div>
            </div>
        </section>
    </main>
</body>
</html>