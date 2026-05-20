<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.ActivityLog,java.util.List,java.util.Set,java.util.Collections"%>
<%
    List<String[]>    allUsers      = (List<String[]>)    request.getAttribute("allUsers");
    List<ActivityLog> recentLogs    = (List<ActivityLog>) request.getAttribute("recentLogs");
    Set               activeSessions= (Set)               request.getAttribute("activeSessions");
    String cp = request.getContextPath();

    if (allUsers       == null) allUsers       = Collections.emptyList();
    if (recentLogs     == null) recentLogs     = Collections.emptyList();
    if (activeSessions == null) activeSessions = Collections.emptySet();

    int sysAdminCount = ((Integer) request.getAttribute("sysAdminCount") != null)
        ? (Integer) request.getAttribute("sysAdminCount") : 0;
    int adminCount = ((Integer) request.getAttribute("adminCount") != null)
        ? (Integer) request.getAttribute("adminCount") : 0;
    int staffCount = sysAdminCount + adminCount;
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>SysAdmin Dashboard</title>
    <link rel="stylesheet" href="<%= cp %>/css/adminDashboard.css">
    <link rel="stylesheet" href="<%= cp %>/css/sysadminDashboard.css">
</head>
<body>

<!-- Parallax background -->
<div id="background">
    <img class="twilight"            src="<%= cp %>/images/Twilight.png">
    <img class="layer mountain far"  src="<%= cp %>/images/mountain-far.png">
    <img class="layer mountain mid"  src="<%= cp %>/images/mountain-mid.png">
    <img class="layer mountain near" src="<%= cp %>/images/mountain-near.png">
    <img class="layer greenery"      src="<%= cp %>/images/greenery2.png">
</div>

<!-- Sidebar -->
<aside class="sidebar">
    <div>
        <img src="<%= cp %>/images/logo-small.png" class="sidebar-logo">
        <div class="sidebar-title">SYSADMIN</div>
        <nav class="sidebar-nav">
            <a href="SysAdminDashboard" class="active">Dashboard</a>
            <a href="#section-users"  onclick="scrollTo('section-users')">Auth Database</a>
            <a href="#section-logs"   onclick="scrollTo('section-logs')">Activity Logs</a>
            <a href="SysAdminDashboard?action=report" target="_blank">Generate Report</a>
        </nav>
    </div>
    <div class="sidebar-bottom">
        <a href="<%= cp %>/LogoutServlet">Logout</a>
    </div>
</aside>

<!-- Main content -->
<main class="main-content">
    <header class="top-bar">
        <div class="search-box">
            <input type="text" placeholder="Search Here">
        </div>
        <div class="top-actions">
            <div class="profile-box">
                <div>
                    <h4>${sessionScope.username}</h4>
                    <p>System Administrator</p>
                </div>
            </div>
        </div>
    </header>

    <!-- Hero -->
    <section class="hero-card">
        <div class="hero-content">
            <h1>System Administration<br>Panel</h1>
            <p>Monitor platform authentication, user activity, and system health.</p>
            <div class="hero-buttons">
                <button onclick="document.getElementById('section-users').scrollIntoView({behavior:'smooth'})">Auth Database</button>
                <button onclick="document.getElementById('section-logs').scrollIntoView({behavior:'smooth'})">Activity Logs</button>
                <button onclick="window.open('SysAdminDashboard?action=report','_blank')">Generate Report</button>
            </div>
        </div>
        <img src="<%= cp %>/images/flower-book.png" class="hero-image">
    </section>

    <!-- Stats -->
    <section class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">&#128100;</div>
            <div>
                <h2>${totalUsers}</h2>
                <p>Total Users</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">&#128994;</div>
            <div>
                <h2>${onlineCount}</h2>
                <p>Currently Online</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">&#128274;</div>
            <div>
                <h2><%= staffCount %></h2>
                <p>Staff (SysAdmin + Teachers)</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">&#127891;</div>
            <div>
                <h2>${guestCount}</h2>
                <p>Students</p>
            </div>
        </div>
    </section>

    <!-- Authentication Database panel -->
    <div class="panel" id="section-users" style="margin-top:28px">
        <div class="panel-header">
            <h2>Authentication Database</h2>
            <a href="SysAdminDashboard?action=report" target="_blank">
                <button style="border:none;padding:8px 18px;border-radius:14px;background:rgba(255,255,255,0.18);color:white;cursor:pointer;font-size:13px;">
                    &#128438; Print Report
                </button>
            </a>
        </div>
        <% if (allUsers.isEmpty()) { %>
            <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No users found.</div>
        <% } else { %>
        <table class="data-table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Username</th>
                    <th>Derby Role</th>
                    <th>Registered</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
            <% int idx = 1;
               for (String[] u : allUsers) {
                   boolean isOnline = activeSessions.contains(u[0]);
                   String roleBadge = "SysAdmin".equals(u[1]) ? "badge-sysadmin"
                                    : "Admin".equals(u[1])    ? "badge-admin"
                                                              : "badge-guest";
            %>
            <tr class="<%= isOnline ? "row-online" : "" %>">
                <td style="color:rgba(255,255,255,0.45)"><%= idx++ %></td>
                <td><strong><%= isOnline ? "(" + u[0] + ")" : u[0] %></strong></td>
                <td><span class="<%= roleBadge %>"><%= u[1] %></span></td>
                <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= u[2] %></td>
                <td>
                    <% if (isOnline) { %>
                        <span class="badge-online">&#128994; Online</span>
                    <% } else { %>
                        <span style="color:rgba(255,255,255,0.35);font-size:13px">&#8212;</span>
                    <% } %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } %>
    </div>

    <!-- Activity Log panel -->
    <div class="panel" id="section-logs" style="margin-top:22px">
        <div class="panel-header">
            <h2>Activity Log</h2>
            <span style="font-size:13px;color:rgba(255,255,255,0.55)">Last 100 entries</span>
        </div>
        <% if (recentLogs.isEmpty()) { %>
            <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No activity logged yet.</div>
        <% } else { %>
        <table class="data-table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Username</th>
                    <th>Action</th>
                    <th>IP Address</th>
                    <th>Role</th>
                    <th>Timestamp</th>
                </tr>
            </thead>
            <tbody>
            <% int li = 1;
               for (ActivityLog log : recentLogs) { %>
            <tr>
                <td style="color:rgba(255,255,255,0.45)"><%= li++ %></td>
                <td><%= log.getUsername() %></td>
                <td><%= log.getAction() %></td>
                <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= log.getIpAddress() %></td>
                <td style="font-size:12px"><%= log.getRole() %></td>
                <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= log.getActivityTime() %></td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } %>
    </div>

</main>
</body>
</html>
