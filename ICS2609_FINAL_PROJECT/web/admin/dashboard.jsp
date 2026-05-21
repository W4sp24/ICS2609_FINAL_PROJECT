<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.ActivityLog,model.Course,model.Submission,model.Assignment,model.User,java.util.List,java.util.Map,java.util.Set,java.util.Collections"%>
<%
    // Auth DB + logs tab
    List<String[]>    allUsers       = (List<String[]>)    request.getAttribute("allUsers");
    List<ActivityLog> recentLogs     = (List<ActivityLog>) request.getAttribute("recentLogs");
    Set               activeSessions = (Set)               request.getAttribute("activeSessions");
    if (allUsers       == null) allUsers       = Collections.emptyList();
    if (recentLogs     == null) recentLogs     = Collections.emptyList();
    if (activeSessions == null) activeSessions = Collections.emptySet();

    int adminCount  = request.getAttribute("adminCount")  != null ? (Integer) request.getAttribute("adminCount")  : 0;
    int guestCount  = request.getAttribute("guestCount")  != null ? (Integer) request.getAttribute("guestCount")  : 0;
    int onlineCount = request.getAttribute("onlineCount") != null ? (Integer) request.getAttribute("onlineCount") : 0;
    int totalUsers  = request.getAttribute("totalUsers")  != null ? (Integer) request.getAttribute("totalUsers")  : 0;

    // Courses tab
    List<Course>              courses          = (List<Course>)              request.getAttribute("courses");
    Map<String, Integer>      enrollmentCounts = (Map<String, Integer>)      request.getAttribute("enrollmentCounts");
    if (courses          == null) courses          = Collections.emptyList();
    if (enrollmentCounts == null) enrollmentCounts = Collections.emptyMap();

    // Grades tab
    Map<String, List<Submission>> subsByCourse  = (Map<String, List<Submission>>) request.getAttribute("submissionsByCourse");
    Map<String, Assignment>       assignmentMap  = (Map<String, Assignment>)       request.getAttribute("assignmentMap");
    Map<String, User>             studentMap     = (Map<String, User>)             request.getAttribute("studentMap");
    int totalPending = request.getAttribute("totalPending") != null ? (Integer) request.getAttribute("totalPending") : 0;
    if (subsByCourse  == null) subsByCourse  = Collections.emptyMap();
    if (assignmentMap == null) assignmentMap = Collections.emptyMap();
    if (studentMap    == null) studentMap    = Collections.emptyMap();

    // Users tab
    List<User> students = (List<User>) request.getAttribute("students");
    List<User> teachers = (List<User>) request.getAttribute("teachers");
    if (students == null) students = Collections.emptyList();
    if (teachers == null) teachers = Collections.emptyList();

    String cp        = request.getContextPath();
    String mysqlRole = (String) request.getAttribute("mysqlRole");
    boolean isTeacher = "teacher".equalsIgnoreCase(mysqlRole);
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin Dashboard</title>
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
        <div class="sidebar-title">ADMIN PANEL</div>
        <nav class="sidebar-nav">
            <a href="javascript:void(0);" data-section="overview"  onclick="showSection('overview')"  class="active">Dashboard</a>
            <a href="javascript:void(0);" data-section="courses"   onclick="showSection('courses')"  >Courses</a>
            <a href="javascript:void(0);" data-section="grades"    onclick="showSection('grades')"   >Grade Submissions</a>
            <a href="javascript:void(0);" data-section="users"     onclick="showSection('users')"    >Manage Students</a>
            <a href="javascript:void(0);" data-section="authdb"    onclick="showSection('authdb')"   >Auth Database</a>
            <a href="javascript:void(0);" data-section="logs"      onclick="showSection('logs')"     >Activity Logs</a>
            <a href="javascript:void(0);" data-section="reports"   onclick="showSection('reports')"  >Reports</a>
        </nav>
    </div>
    <div class="sidebar-bottom">
        <a href="<%= cp %>/LogoutServlet">Logout</a>
    </div>
</aside>

<!-- Main content -->
<main class="main-content">

    <!-- Top bar -->
    <header class="top-bar">
        <div class="search-box"><input type="text" placeholder="Search Here"></div>
        <div class="top-actions">
            <div class="profile-box">
                <div>
                    <h4>${sessionScope.username}</h4>
                    <p><%= mysqlRole %></p>
                </div>
            </div>
        </div>
    </header>

    <!-- =====================================================================
         SECTION: Dashboard Overview
    ====================================================================== -->
    <div id="section-overview" class="admin-section">
        <section class="hero-card">
            <div class="hero-content">
                <h1>Welcome Back,<br>${sessionScope.username}</h1>
                <% if (isTeacher) { %>
                <p>Here are your courses and pending tasks.</p>
                <div class="hero-buttons">
                    <button onclick="showSection('courses')">My Courses</button>
                    <button onclick="showSection('grades')">Grade Submissions</button>
                    <button onclick="showSection('reports')">Reports</button>
                </div>
                <% } else { %>
                <p>Here's an overview of the platform.</p>
                <div class="hero-buttons">
                    <button onclick="showSection('users')">Manage Students</button>
                    <button onclick="showSection('courses')">Manage Courses</button>
                    <button onclick="showSection('reports')">Reports</button>
                </div>
                <% } %>
            </div>
            <img src="<%= cp %>/images/flower-book.png" class="hero-image">
        </section>

        <section class="stats-grid">
        <% if (isTeacher) { %>
            <div class="stat-card">
                <div class="stat-icon">&#128218;</div>
                <div><h2>${myCourseCount}</h2><p>My Courses</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128100;</div>
                <div><h2>${myStudentCount}</h2><p>Enrolled Students</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128203;</div>
                <div><h2>${pendingCount}</h2><p>Pending Grades</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#127891;</div>
                <div><h2>Teacher</h2><p>Your Role</p></div>
            </div>
        <% } else { %>
            <div class="stat-card">
                <div class="stat-icon">&#128100;</div>
                <div><h2>${totalStudents}</h2><p>Total Students</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128218;</div>
                <div><h2>${totalCourses}</h2><p>Total Courses</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#127891;</div>
                <div><h2>${totalTeachers}</h2><p>Teachers</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128196;</div>
                <div><h2><%= mysqlRole %></h2><p>Your Role</p></div>
            </div>
        <% } %>
        </section>

        <section class="lower-grid">
            <div class="panel">
                <div class="panel-header"><h2>Quick Actions</h2></div>
                <div class="quick-actions">
                <% if (isTeacher) { %>
                    <button onclick="showSection('courses')">My Courses</button>
                    <button onclick="showSection('grades')">Grade Submissions</button>
                    <button onclick="showSection('reports')">Reports</button>
                <% } else { %>
                    <button onclick="showSection('users')">Manage Students</button>
                    <button onclick="showSection('courses')">Manage Courses</button>
                    <button onclick="showSection('grades')">Grade Submissions</button>
                <% } %>
                </div>
            </div>
            <div class="panel">
                <div class="panel-header"><h2>Auth Database</h2></div>
                <div style="padding:16px;display:flex;flex-direction:column;gap:10px;">
                    <div style="display:flex;gap:14px;flex-wrap:wrap;">
                        <div style="flex:1;text-align:center;padding:10px;background:rgba(255,255,255,0.07);border-radius:10px;">
                            <div style="font-size:20px;font-weight:700;color:white;"><%= totalUsers %></div>
                            <div style="font-size:12px;color:rgba(255,255,255,0.55);">Total Users</div>
                        </div>
                        <div style="flex:1;text-align:center;padding:10px;background:rgba(255,255,255,0.07);border-radius:10px;">
                            <div style="font-size:20px;font-weight:700;color:rgba(60,200,120,0.9);"><%= onlineCount %></div>
                            <div style="font-size:12px;color:rgba(255,255,255,0.55);">Online</div>
                        </div>
                    </div>
                    <a href="javascript:void(0);" onclick="showSection('authdb')"
                       style="display:block;padding:9px 16px;border-radius:10px;background:rgba(255,255,255,0.15);color:white;text-decoration:none;font-size:13px;text-align:center;">
                        &#128100; View Auth Database
                    </a>
                </div>
            </div>
        </section>
    </div>

    <!-- =====================================================================
         SECTION: Courses
    ====================================================================== -->
    <div id="section-courses" class="admin-section" style="display:none;">
        <div class="panel" style="margin-top:25px;">
            <div class="panel-header">
                <h2>Courses</h2>
                <button class="action-btn" onclick="openModal('addModal')">+ Add Course</button>
            </div>
            <% if (courses.isEmpty()) { %>
            <div style="text-align:center;padding:40px;color:rgba(255,255,255,0.5)">No courses found.</div>
            <% } else { %>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Title</th><th>Status</th><th>Enrolled</th><th>Created</th><th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Course c : courses) {
                       int enrolled = enrollmentCounts.containsKey(c.getC_id()) ? enrollmentCounts.get(c.getC_id()) : 0;
                       String badgeCls = "badge-" + c.getStatus();
                %>
                <tr>
                    <td><strong><%= c.getTitle() %></strong>
                        <% if (c.getDescription() != null && !c.getDescription().isEmpty()) { %>
                        <br><small style="color:rgba(255,255,255,0.55)"><%= c.getDescription() %></small>
                        <% } %>
                    </td>
                    <td><span class="<%= badgeCls %>"><%= c.getStatus() %></span></td>
                    <td><%= enrolled %></td>
                    <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= c.getCreated_at() %></td>
                    <td>
                        <button class="action-btn"
                            onclick="openEdit('<%= c.getC_id() %>','<%= c.getTitle().replace("'","\\'") %>','<%= (c.getDescription()!=null?c.getDescription():"").replace("'","\\'") %>','<%= c.getStatus() %>')">
                            Edit
                        </button>
                        <form method="POST" action="<%= cp %>/Course" style="display:inline"
                              onsubmit="return confirm('Delete this course and all its content?')">
                            <input type="hidden" name="action"   value="delete">
                            <input type="hidden" name="courseId" value="<%= c.getC_id() %>">
                            <button type="submit" class="action-btn danger">Delete</button>
                        </form>
                        <form method="POST" action="<%= cp %>/Course" style="display:inline">
                            <input type="hidden" name="action"   value="status">
                            <input type="hidden" name="courseId" value="<%= c.getC_id() %>">
                            <select name="status" onchange="this.form.submit()"
                                    style="background:rgba(255,255,255,0.12);border:none;color:white;border-radius:12px;padding:4px 8px;cursor:pointer;font-size:12px">
                                <option value="draft"     <%= "draft".equals(c.getStatus())     ? "selected" : "" %>>Draft</option>
                                <option value="published" <%= "published".equals(c.getStatus()) ? "selected" : "" %>>Published</option>
                                <option value="archived"  <%= "archived".equals(c.getStatus())  ? "selected" : "" %>>Archived</option>
                            </select>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>
    </div>

    <!-- =====================================================================
         SECTION: Grade Submissions
    ====================================================================== -->
    <div id="section-grades" class="admin-section" style="display:none;">
        <section class="stats-grid" style="margin-top:25px;grid-template-columns:repeat(3,1fr)">
            <div class="stat-card">
                <div class="stat-icon">&#128221;</div>
                <div><h2><%= totalPending %></h2><p>Pending Submissions</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128218;</div>
                <div><h2><%= courses.size() %></h2><p>Courses</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#9989;</div>
                <div><h2><%= totalPending == 0 ? "All Done" : "Pending" %></h2><p>Status</p></div>
            </div>
        </section>

        <% if (courses.isEmpty()) { %>
        <div class="panel" style="margin-top:22px;text-align:center;padding:40px;color:rgba(255,255,255,0.5)">
            No courses assigned.
        </div>
        <% } else if (totalPending == 0) { %>
        <div class="panel" style="margin-top:22px;text-align:center;padding:40px">
            <div style="font-size:48px;margin-bottom:12px">&#127881;</div>
            <h2 style="font-family:'Daruma',sans-serif;font-size:28px">All caught up!</h2>
            <p style="margin-top:10px;color:rgba(255,255,255,0.65)">No pending submissions to grade.</p>
        </div>
        <% } else {
               for (Course c : courses) {
                   List<Submission> subs = subsByCourse.get(c.getC_id());
                   if (subs == null || subs.isEmpty()) continue;
        %>
        <div class="panel course-section">
            <div class="panel-header">
                <h2><%= c.getTitle() %> <span class="pending-badge"><%= subs.size() %> pending</span></h2>
            </div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Student</th><th>Assignment</th><th>Max Score</th><th>Submitted At</th><th>File</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Submission sub : subs) {
                       User       student = studentMap.get(sub.getStudent_id());
                       Assignment asgn    = assignmentMap.get(sub.getAssignment_id());
                       String studentEmail = (student != null) ? student.getEmail() : sub.getStudent_id();
                       String asgnTitle    = (asgn != null) ? asgn.getTitle() : "—";
                       String maxScore     = (asgn != null && asgn.getMax_score() > 0) ? String.format("%.0f", asgn.getMax_score()) : "—";
                %>
                <tr>
                    <td><%= studentEmail %></td>
                    <td><%= asgnTitle %></td>
                    <td><%= maxScore %></td>
                    <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= sub.getSubmitted_at() %></td>
                    <td>
                        <% if (sub.getFile_url() != null && !sub.getFile_url().isEmpty()) { %>
                        <a href="<%= sub.getFile_url() %>" target="_blank" style="color:#a8d8f0;font-size:13px">View File</a>
                        <% } else { %>&mdash;<% } %>
                    </td>
                    <td>
                        <button class="action-btn"
                            onclick="openGrade('<%= sub.getS_id() %>','<%= asgnTitle.replace("'","\\'") %>','<%= studentEmail %>','<%= maxScore %>')">
                            Grade
                        </button>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <%  }
           } %>
    </div>

    <!-- =====================================================================
         SECTION: Manage Students / Users
    ====================================================================== -->
    <div id="section-users" class="admin-section" style="display:none;">
        <section class="lower-grid" style="margin-top:25px">
            <div class="panel">
                <div class="panel-header">
                    <h2>Students <span class="count-chip"><%= students.size() %></span></h2>
                </div>
                <% if (students.isEmpty()) { %>
                <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No students found.</div>
                <% } else { %>
                <table class="data-table">
                    <thead><tr><th>#</th><th>Email</th><th>Name</th><th>Joined</th></tr></thead>
                    <tbody>
                    <% int si = 1; for (User s : students) { %>
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
            <div class="panel">
                <div class="panel-header">
                    <h2>Teachers <span class="count-chip"><%= teachers.size() %></span></h2>
                </div>
                <% if (teachers.isEmpty()) { %>
                <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No teachers found.</div>
                <% } else { %>
                <table class="data-table">
                    <thead><tr><th>#</th><th>Email</th><th>Name</th><th>Joined</th></tr></thead>
                    <tbody>
                    <% int ti = 1; for (User t : teachers) { %>
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
    </div>

    <!-- =====================================================================
         SECTION: Auth Database
    ====================================================================== -->
    <div id="section-authdb" class="admin-section" style="display:none;">
        <div class="panel" style="margin-top:28px;">
            <div class="panel-header">
                <h2>Authentication Database</h2>
                <div style="display:flex;gap:10px;align-items:center;">
                    <span style="font-size:13px;color:rgba(255,255,255,0.55);"><%= totalUsers %> users &bull; <%= onlineCount %> online</span>
                    <a href="<%= cp %>/ReportServlet?type=all_records" target="_blank">
                        <button style="border:none;padding:7px 16px;border-radius:10px;background:rgba(255,255,255,0.18);color:white;cursor:pointer;font-size:12px;">
                            &#128438; All Records PDF
                        </button>
                    </a>
                </div>
            </div>
            <section class="stats-grid" style="margin:16px 0 8px;">
                <div class="stat-card"><div class="stat-icon">&#128100;</div><div><h2><%= totalUsers %></h2><p>Total Users</p></div></div>
                <div class="stat-card"><div class="stat-icon">&#128994;</div><div><h2><%= onlineCount %></h2><p>Currently Online</p></div></div>
                <div class="stat-card"><div class="stat-icon">&#128274;</div><div><h2><%= adminCount %></h2><p>Admins / Teachers</p></div></div>
                <div class="stat-card"><div class="stat-icon">&#127891;</div><div><h2><%= guestCount %></h2><p>Students</p></div></div>
            </section>
            <% if (allUsers.isEmpty()) { %>
                <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No users found.</div>
            <% } else { %>
            <table class="data-table">
                <thead><tr><th>#</th><th>Username</th><th>Derby Role</th><th>Registered</th><th>Status</th></tr></thead>
                <tbody>
                <% int idx = 1; for (String[] u : allUsers) {
                       boolean isOnline = activeSessions.contains(u[0]);
                       String roleBadge = "Admin".equals(u[1]) ? "badge-admin" : "badge-guest";
                %>
                <tr class="<%= isOnline ? "row-online" : "" %>">
                    <td style="color:rgba(255,255,255,0.45)"><%= idx++ %></td>
                    <td><strong><%= isOnline ? "(" + u[0] + ")" : u[0] %></strong></td>
                    <td><span class="<%= roleBadge %>"><%= u[1] %></span></td>
                    <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= u[2] %></td>
                    <td><% if (isOnline) { %><span class="badge-online">&#128994; Online</span><% } else { %><span style="color:rgba(255,255,255,0.35);font-size:13px">&#8212;</span><% } %></td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>
    </div>

    <!-- =====================================================================
         SECTION: Activity Logs
    ====================================================================== -->
    <div id="section-logs" class="admin-section" style="display:none;">
        <div class="panel" style="margin-top:28px;">
            <div class="panel-header">
                <h2>Activity Log</h2>
                <form method="get" action="<%= cp %>/ReportServlet" target="_blank"
                      style="display:inline-flex;gap:8px;align-items:center;flex-wrap:wrap;">
                    <input type="hidden" name="type" value="time_bound">
                    <input type="date" name="startDate" required style="padding:5px 8px;border-radius:8px;border:none;font-size:12px;">
                    <input type="date" name="endDate"   required style="padding:5px 8px;border-radius:8px;border:none;font-size:12px;">
                    <button type="submit" style="padding:5px 14px;border-radius:8px;border:none;background:rgba(255,255,255,0.2);color:white;cursor:pointer;font-size:12px;">
                        &#128438; Time-Bound PDF
                    </button>
                </form>
            </div>
            <% if (recentLogs.isEmpty()) { %>
                <div style="text-align:center;padding:30px;color:rgba(255,255,255,0.5)">No activity logged yet.</div>
            <% } else { %>
            <table class="data-table">
                <thead><tr><th>#</th><th>Username</th><th>Action</th><th>IP Address</th><th>Role</th><th>Timestamp</th></tr></thead>
                <tbody>
                <% int li = 1; for (ActivityLog log : recentLogs) { %>
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
    </div>

    <!-- =====================================================================
         SECTION: Reports
    ====================================================================== -->
    <div id="section-reports" class="admin-section" style="display:none;">
        <div style="margin-top:28px;">
            <div class="panel-header" style="margin-bottom:20px;">
                <h2 style="color:white;font-size:22px;">Report Generation</h2>
                <span style="font-size:13px;color:rgba(255,255,255,0.55);">Download PDF reports from the authentication and course databases</span>
            </div>
            <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px;margin-bottom:16px;">
                <div class="panel">
                    <div class="panel-header"><h2>All Records</h2></div>
                    <div style="padding:16px;">
                        <p style="color:rgba(255,255,255,0.65);font-size:13px;margin-bottom:16px;line-height:1.5;">All registered users in the authentication database with their roles and registration dates.</p>
                        <a href="<%= cp %>/ReportServlet?type=all_records" target="_blank"
                           style="display:block;padding:10px;border-radius:10px;background:rgba(255,255,255,0.15);color:white;text-decoration:none;font-size:13px;text-align:center;font-weight:600;">
                            &#128438; Download PDF
                        </a>
                    </div>
                </div>
                <div class="panel">
                    <div class="panel-header"><h2>My Record</h2></div>
                    <div style="padding:16px;">
                        <p style="color:rgba(255,255,255,0.65);font-size:13px;margin-bottom:16px;line-height:1.5;">Your own administrator account record from the authentication database.</p>
                        <a href="<%= cp %>/ReportServlet?type=own_records" target="_blank"
                           style="display:block;padding:10px;border-radius:10px;background:rgba(255,255,255,0.15);color:white;text-decoration:none;font-size:13px;text-align:center;font-weight:600;">
                            &#128438; Download PDF
                        </a>
                    </div>
                </div>
                <div class="panel">
                    <div class="panel-header"><h2>Class List</h2></div>
                    <div style="padding:16px;">
                        <p style="color:rgba(255,255,255,0.65);font-size:13px;margin-bottom:16px;line-height:1.5;">Enrolled students per course from the course management database.</p>
                        <a href="<%= cp %>/ReportServlet?type=class_list" target="_blank"
                           style="display:block;padding:10px;border-radius:10px;background:rgba(255,255,255,0.15);color:white;text-decoration:none;font-size:13px;text-align:center;font-weight:600;">
                            &#128438; Download PDF
                        </a>
                    </div>
                </div>
            </div>
            <div class="panel">
                <div class="panel-header">
                    <h2>Time-Bound Activity Report</h2>
                    <span style="font-size:13px;color:rgba(255,255,255,0.55);">Filter by date range</span>
                </div>
                <div style="padding:20px;">
                    <p style="color:rgba(255,255,255,0.65);font-size:13px;margin-bottom:16px;">Generates a PDF of activity logs or course submissions within a selected date range.</p>
                    <form method="get" action="<%= cp %>/ReportServlet" target="_blank"
                          style="display:flex;gap:12px;align-items:center;flex-wrap:wrap;">
                        <input type="hidden" name="type" value="time_bound">
                        <div style="display:flex;flex-direction:column;gap:4px;">
                            <label style="font-size:12px;color:rgba(255,255,255,0.6);">Start Date</label>
                            <input type="date" name="startDate" required style="padding:7px 10px;border-radius:8px;border:none;font-size:13px;min-width:140px;">
                        </div>
                        <div style="display:flex;flex-direction:column;gap:4px;">
                            <label style="font-size:12px;color:rgba(255,255,255,0.6);">End Date</label>
                            <input type="date" name="endDate" required style="padding:7px 10px;border-radius:8px;border:none;font-size:13px;min-width:140px;">
                        </div>
                        <div style="margin-top:18px;">
                            <button type="submit" style="padding:8px 20px;border-radius:10px;border:none;background:rgba(255,255,255,0.2);color:white;cursor:pointer;font-size:13px;font-weight:600;">
                                &#128438; Download PDF
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

</main>

<!-- =========================================================
     Modals (outside main for correct z-index stacking)
========================================================== -->

<!-- Add Course Modal -->
<div id="addModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Add Course</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action" value="add">
            <div class="form-group">
                <label>Course Title</label>
                <input type="text" name="title" placeholder="e.g. Introduction to Java" required>
            </div>
            <div class="form-group">
                <label>Description</label>
                <textarea name="description" placeholder="Brief course description..."></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('addModal')">Cancel</button>
                <button type="submit" class="btn-submit">Create Course</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Course Modal -->
<div id="editModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Edit Course</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action"   value="edit">
            <input type="hidden" name="courseId" id="editCourseId">
            <div class="form-group">
                <label>Course Title</label>
                <input type="text" name="title" id="editTitle" required>
            </div>
            <div class="form-group">
                <label>Description</label>
                <textarea name="description" id="editDesc"></textarea>
            </div>
            <div class="form-group">
                <label>Status</label>
                <select name="status" id="editStatus">
                    <option value="draft">Draft</option>
                    <option value="published">Published</option>
                    <option value="archived">Archived</option>
                </select>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('editModal')">Cancel</button>
                <button type="submit" class="btn-submit">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<!-- Grade Modal -->
<div id="gradeModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Grade Submission</h2>
        <p id="gradeInfo" style="color:rgba(255,255,255,0.7);margin-bottom:18px;font-size:14px"></p>
        <form method="POST" action="<%= cp %>/Grade">
            <input type="hidden" name="action"       value="grade">
            <input type="hidden" name="submissionId" id="gradeSubId">
            <div class="form-group">
                <label>Score <span id="gradeMax" style="color:rgba(255,255,255,0.5)"></span></label>
                <input type="number" name="score" id="gradeScore" min="0" step="0.5" required placeholder="0">
            </div>
            <div class="form-group">
                <label>Feedback (optional)</label>
                <textarea name="feedback" placeholder="Write feedback for the student..."></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('gradeModal')">Cancel</button>
                <button type="submit" class="btn-submit">Submit Grade</button>
            </div>
        </form>
    </div>
</div>

<script>
// ── Section switching ─────────────────────────────────────────────────────
function showSection(name) {
    document.querySelectorAll('.admin-section').forEach(function(s) {
        s.style.display = 'none';
    });
    document.getElementById('section-' + name).style.display = 'block';
    document.querySelectorAll('.sidebar-nav a[data-section]').forEach(function(a) {
        a.classList.remove('active');
    });
    var link = document.querySelector('.sidebar-nav a[data-section="' + name + '"]');
    if (link) link.classList.add('active');
}

// Auto-show section from ?section= URL param
(function() {
    var params = new URLSearchParams(window.location.search);
    var section = params.get('section');
    if (section && document.getElementById('section-' + section)) {
        showSection(section);
    }
})();

// ── Modal helpers ─────────────────────────────────────────────────────────
function openModal(id)  { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }

function openEdit(id, title, desc, status) {
    document.getElementById('editCourseId').value = id;
    document.getElementById('editTitle').value    = title;
    document.getElementById('editDesc').value     = desc;
    document.getElementById('editStatus').value   = status;
    openModal('editModal');
}

function openGrade(subId, asgnTitle, student, maxScore) {
    document.getElementById('gradeSubId').value           = subId;
    document.getElementById('gradeInfo').textContent      = student + ' — ' + asgnTitle;
    document.getElementById('gradeMax').textContent       = maxScore !== '—' ? '/ ' + maxScore : '';
    document.getElementById('gradeScore').max             = maxScore !== '—' ? maxScore : '';
    openModal('gradeModal');
}

// Close modal on backdrop click
window.addEventListener('click', function(e) {
    ['addModal','editModal','gradeModal'].forEach(function(id) {
        var m = document.getElementById(id);
        if (e.target === m) closeModal(id);
    });
});
</script>

</body>
</html>
