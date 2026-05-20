<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.User,java.util.List"%>
<%
    List<User> students = (List<User>) request.getAttribute("students");
    List<User> teachers = (List<User>) request.getAttribute("teachers");
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User Management — Admin Panel</title>
    <link rel="stylesheet" href="<%= cp %>/css/adminDashboard.css">
    <style>
        .data-table { width:100%; border-collapse:collapse; margin-top:18px; }
        .data-table th { text-align:left; padding:12px 16px; background:rgba(255,255,255,0.1); font-size:13px; }
        .data-table td { padding:12px 16px; border-bottom:1px solid rgba(255,255,255,0.07); font-size:14px; }
        .data-table tr:hover td { background:rgba(255,255,255,0.04); }
        .badge { border-radius:20px; padding:3px 12px; font-size:12px; font-weight:bold; }
        .badge-student { background:rgba(80,140,200,0.35); }
        .badge-teacher { background:rgba(160,100,220,0.35); }
        .count-chip { background:rgba(255,255,255,0.15); border-radius:20px; padding:2px 10px; font-size:13px; margin-left:8px; }
    </style>
</head>
<body>
<div id="background">
    <img class="twilight" src="<%= cp %>/images/Twilight.png">
    <img class="layer mountain far"  src="<%= cp %>/images/mountain-far.png">
    <img class="layer mountain mid"  src="<%= cp %>/images/mountain-mid.png">
    <img class="layer mountain near" src="<%= cp %>/images/mountain-near.png">
    <img class="layer greenery"      src="<%= cp %>/images/greenery2.png">
</div>
<aside class="sidebar">
    <div>
        <img src="<%= cp %>/images/logo-small.png" class="sidebar-logo">
        <div class="sidebar-title">ADMIN PANEL</div>
        <nav class="sidebar-nav">
            <a href="AdminDashboard">Dashboard</a>
            <a href="Course">Courses</a>
            <a href="Grade">Grade Submissions</a>
            <a href="UserManagement" class="active">Manage Students</a>
            <a href="ReportServlet">Reports</a>
        </nav>
    </div>
    <div class="sidebar-bottom">
        <a href="<%= cp %>/LogoutServlet">Logout</a>
    </div>
</aside>
<main class="main-content">
    <header class="top-bar">
        <div class="search-box"><input type="text" placeholder="Search Here"></div>
        <div class="top-actions">
            <div class="profile-box">
                <div><h4>${sessionScope.username}</h4><p>System Admin</p></div>
            </div>
        </div>
    </header>

    <section class="lower-grid" style="margin-top:25px">
        <!-- Students Panel -->
        <div class="panel">
            <div class="panel-header">
                <h2>Students <span class="count-chip"><%= students != null ? students.size() : 0 %></span></h2>
            </div>
            <% if (students == null || students.isEmpty()) { %>
            <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No students found.</div>
            <% } else { %>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Email</th>
                        <th>Name</th>
                        <th>Joined</th>
                    </tr>
                </thead>
                <tbody>
                <% int si = 1;
                   for (User s : students) { %>
                <tr>
                    <td style="color:rgba(255,255,255,0.45)"><%= si++ %></td>
                    <td><%= s.getEmail() %></td>
                    <td><%= s.getFirstName() %> <%= s.getLastName() %></td>
                    <td style="font-size:12px;color:rgba(255,255,255,0.55)"><%= s.getCreatedAt() %></td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>

        <!-- Teachers Panel -->
        <div class="panel">
            <div class="panel-header">
                <h2>Teachers <span class="count-chip"><%= teachers != null ? teachers.size() : 0 %></span></h2>
            </div>
            <% if (teachers == null || teachers.isEmpty()) { %>
            <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No teachers found.</div>
            <% } else { %>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Email</th>
                        <th>Name</th>
                        <th>Joined</th>
                    </tr>
                </thead>
                <tbody>
                <% int ti = 1;
                   for (User t : teachers) { %>
                <tr>
                    <td style="color:rgba(255,255,255,0.45)"><%= ti++ %></td>
                    <td><%= t.getEmail() %></td>
                    <td><%= t.getFirstName() %> <%= t.getLastName() %></td>
                    <td style="font-size:12px;color:rgba(255,255,255,0.55)"><%= t.getCreatedAt() %></td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>
    </section>
</main>
</body>
</html>
