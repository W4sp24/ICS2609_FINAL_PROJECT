<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List,java.util.Set,java.util.Collections,java.text.SimpleDateFormat,java.util.Date"%>
<%
    List<String[]> allUsers      = (List<String[]>) request.getAttribute("allUsers");
    Set            activeSessions= (Set)            request.getAttribute("activeSessions");
    String cp = request.getContextPath();

    if (allUsers       == null) allUsers       = Collections.emptyList();
    if (activeSessions == null) activeSessions = Collections.emptySet();

    String generatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    int sysAdminCount = 0, adminCount = 0, guestCount = 0, onlineCount = 0;
    for (String[] u : allUsers) {
        if ("SysAdmin".equals(u[1]))   sysAdminCount++;
        else if ("Admin".equals(u[1])) adminCount++;
        else                           guestCount++;
        if (activeSessions.contains(u[0])) onlineCount++;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User Registry Report — Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= cp %>/css/adminDashboard.css">
    <link rel="stylesheet" href="<%= cp %>/css/sysadminDashboard.css">
</head>
<body>

<div id="background">
    <img class="twilight"            src="<%= cp %>/images/Twilight.png">
    <img class="layer mountain far"  src="<%= cp %>/images/mountain-far.png">
    <img class="layer mountain mid"  src="<%= cp %>/images/mountain-mid.png">
    <img class="layer mountain near" src="<%= cp %>/images/mountain-near.png">
    <img class="layer greenery"      src="<%= cp %>/images/greenery2.png">
</div>

<aside class="sidebar no-print">
    <div>
        <img src="<%= cp %>/images/logo-small.png" class="sidebar-logo">
        <div class="sidebar-title">SYSADMIN</div>
        <nav class="sidebar-nav">
            <a href="<%= cp %>/SysAdminDashboard">Dashboard</a>
            <a href="<%= cp %>/SysAdminDashboard?action=report" class="active">Report</a>
        </nav>
    </div>
    <div class="sidebar-bottom">
        <a href="<%= cp %>/LogoutServlet">Logout</a>
    </div>
</aside>

<main class="main-content">

    <!-- Report header (hidden on print) -->
    <div class="report-header no-print">
        <div>
            <div class="report-title">User Registry Report</div>
            <div class="report-meta">Generated: <%= generatedAt %> &nbsp;|&nbsp; Total: <%= allUsers.size() %> users &nbsp;|&nbsp; Online: <%= onlineCount %></div>
        </div>
        <div style="display:flex;gap:12px;align-items:center">
            <button class="btn-print" onclick="window.print()">&#128438; Print / Save PDF</button>
            <button class="btn-back" onclick="window.history.back()">&#8592; Back</button>
        </div>
    </div>

    <!-- Print-only header -->
    <div style="display:none" class="print-only">
        <h2 style="font-size:20px;margin-bottom:4px">Active Learning, Inc. — User Registry Report</h2>
        <p style="font-size:12px;margin-bottom:16px">Generated: <%= generatedAt %> &nbsp;|&nbsp; Total: <%= allUsers.size() %> users &nbsp;|&nbsp; Currently Online: <%= onlineCount %></p>
    </div>

    <!-- Summary panel -->
    <section class="stats-grid no-print">
        <div class="stat-card">
            <div class="stat-icon">&#128100;</div>
            <div><h2><%= allUsers.size() %></h2><p>Total Users</p></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">&#128994;</div>
            <div><h2><%= onlineCount %></h2><p>Currently Online</p></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">&#128274;</div>
            <div><h2><%= sysAdminCount + adminCount %></h2><p>Staff Accounts</p></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">&#127891;</div>
            <div><h2><%= guestCount %></h2><p>Student Accounts</p></div>
        </div>
    </section>

    <!-- User Registry Table -->
    <div class="panel" style="margin-top:22px">
        <div class="panel-header no-print">
            <h2>Platform User Registry</h2>
            <span style="font-size:13px;color:rgba(255,255,255,0.55)">
                Users currently online are shown as <strong>(username)</strong>
            </span>
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
                        <span class="badge-online">Online</span>
                    <% } else { %>
                        <span style="color:rgba(255,255,255,0.35);font-size:13px">Offline</span>
                    <% } %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } %>

        <div style="margin-top:24px;font-size:12px;color:rgba(255,255,255,0.45);text-align:center">
            Generated by Active Learning, Inc. Course Management System &mdash; SysAdmin Report &mdash; <%= generatedAt %>
        </div>
    </div>

</main>

<style>
    @media screen {
        .print-only { display: none !important; }
    }
    @media print {
        .print-only { display: block !important; }
    }
</style>

</body>
</html>
