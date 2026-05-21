<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.ActivityLog,model.Course,model.Enrollment,model.Module,model.Submission,model.Assignment,model.User,java.util.List,java.util.Map,java.util.Set,java.util.HashSet,java.util.Collections,java.util.ArrayList"%>
<%
    // Auth DB + logs
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

    // Course Management
    List<Course>                     courses           = (List<Course>)                     request.getAttribute("courses");
    Map<String, List<Enrollment>>    courseEnrollments = (Map<String, List<Enrollment>>)    request.getAttribute("courseEnrollments");
    Map<String, Integer>          enrollmentCounts  = (Map<String, Integer>)          request.getAttribute("enrollmentCounts");
    Map<String, List<Module>>     courseModules     = (Map<String, List<Module>>)     request.getAttribute("courseModules");
    Map<String, List<Assignment>> moduleAssignments = (Map<String, List<Assignment>>) request.getAttribute("moduleAssignments");
    Map<String, List<Submission>> subsByCourse      = (Map<String, List<Submission>>) request.getAttribute("submissionsByCourse");
    Map<String, Assignment>       assignmentMap     = (Map<String, Assignment>)       request.getAttribute("assignmentMap");
    Map<String, User>             studentMap        = (Map<String, User>)             request.getAttribute("studentMap");
    int totalPending = request.getAttribute("totalPending") != null ? (Integer) request.getAttribute("totalPending") : 0;

    if (courses           == null) courses           = Collections.emptyList();
    if (courseEnrollments == null) courseEnrollments = Collections.emptyMap();
    if (enrollmentCounts  == null) enrollmentCounts  = Collections.emptyMap();
    if (courseModules     == null) courseModules     = Collections.emptyMap();
    if (moduleAssignments == null) moduleAssignments = Collections.emptyMap();
    if (subsByCourse      == null) subsByCourse      = Collections.emptyMap();
    if (assignmentMap     == null) assignmentMap     = Collections.emptyMap();
    if (studentMap        == null) studentMap        = Collections.emptyMap();

    // Users tab
    List<User> students = (List<User>) request.getAttribute("students");
    List<User> teachers = (List<User>) request.getAttribute("teachers");
    if (students == null) students = Collections.emptyList();
    if (teachers == null) teachers = Collections.emptyList();

    String  cp        = request.getContextPath();
    String  mysqlRole = (String) request.getAttribute("mysqlRole");
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

<div id="background">
    <img class="twilight"            src="<%= cp %>/images/Twilight.png">
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
            <a href="javascript:void(0);" data-section="overview"          onclick="showSection('overview')"          class="active">Dashboard</a>
            <a href="javascript:void(0);" data-section="courseManagement"  onclick="showSection('courseManagement')"  >Course Management</a>
            <a href="javascript:void(0);" data-section="users"             onclick="showSection('users')"             >Manage Students</a>
            <a href="javascript:void(0);" data-section="authdb"            onclick="showSection('authdb')"            >Auth Database</a>
            <a href="javascript:void(0);" data-section="logs"              onclick="showSection('logs')"              >Activity Logs</a>
            <a href="javascript:void(0);" data-section="reports"           onclick="showSection('reports')"           >Reports</a>
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
                    <button onclick="showSection('courseManagement')">My Courses</button>
                    <button onclick="showSection('reports')">Reports</button>
                </div>
                <% } else { %>
                <p>Here's an overview of the platform.</p>
                <div class="hero-buttons">
                    <button onclick="showSection('users')">Manage Students</button>
                    <button onclick="showSection('courseManagement')">Manage Courses</button>
                    <button onclick="showSection('reports')">Reports</button>
                </div>
                <% } %>
            </div>
            <img src="<%= cp %>/images/flower-book.png" class="hero-image">
        </section>

        <section class="stats-grid">
        <% if (isTeacher) { %>
            <div class="stat-card"><div class="stat-icon">&#128218;</div><div><h2>${myCourseCount}</h2><p>My Courses</p></div></div>
            <div class="stat-card"><div class="stat-icon">&#128100;</div><div><h2>${myStudentCount}</h2><p>Enrolled Students</p></div></div>
            <div class="stat-card"><div class="stat-icon">&#128203;</div><div><h2>${pendingCount}</h2><p>Pending Grades</p></div></div>
            <div class="stat-card"><div class="stat-icon">&#127891;</div><div><h2>Teacher</h2><p>Your Role</p></div></div>
        <% } else { %>
            <div class="stat-card"><div class="stat-icon">&#128100;</div><div><h2>${totalStudents}</h2><p>Total Students</p></div></div>
            <div class="stat-card"><div class="stat-icon">&#128218;</div><div><h2>${totalCourses}</h2><p>Total Courses</p></div></div>
            <div class="stat-card"><div class="stat-icon">&#127891;</div><div><h2>${totalTeachers}</h2><p>Teachers</p></div></div>
            <div class="stat-card"><div class="stat-icon">&#128196;</div><div><h2><%= mysqlRole %></h2><p>Your Role</p></div></div>
        <% } %>
        </section>

        <section class="lower-grid">
            <div class="panel">
                <div class="panel-header"><h2>Quick Actions</h2></div>
                <div class="quick-actions">
                <% if (isTeacher) { %>
                    <button onclick="showSection('courseManagement')">My Courses</button>
                    <button onclick="showSection('reports')">Reports</button>
                <% } else { %>
                    <button onclick="showSection('users')">Manage Students</button>
                    <button onclick="showSection('courseManagement')">Manage Courses</button>
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
         SECTION: Course Management (master-detail)
    ====================================================================== -->
    <div id="section-courseManagement" class="admin-section" style="display:none;">
        <div class="course-mgmt-layout">

            <!-- Left: Course List -->
            <div class="course-list-panel">
                <div class="course-list-top">
                    <h3>Courses</h3>
                    <button class="action-btn" style="padding:5px 10px;font-size:18px;line-height:1;" onclick="openModal('addModal')">+</button>
                </div>
                <% if (courses.isEmpty()) { %>
                <div class="no-items-msg">No courses yet.</div>
                <% } else { for (Course c : courses) {
                       int enrolled = enrollmentCounts.containsKey(c.getC_id()) ? enrollmentCounts.get(c.getC_id()) : 0;
                %>
                <div class="course-card-item" id="course-card-<%= c.getC_id() %>"
                     onclick="selectCourse('<%= c.getC_id() %>')">
                    <div class="course-card-title"><%= c.getTitle() %></div>
                    <div class="course-card-meta">
                        <span class="badge-<%= c.getStatus() %>"><%= c.getStatus() %></span>
                        <span style="font-size:11px;color:rgba(255,255,255,0.45);"><%= enrolled %> students</span>
                    </div>
                </div>
                <% } } %>
            </div>

            <!-- Right: Course Detail -->
            <div class="course-detail-panel">

                <!-- Empty state -->
                <div id="course-detail-empty" class="course-detail-empty">
                    <div class="empty-icon">&#128218;</div>
                    <p>Select a course to manage modules,<br>assignments and submissions.</p>
                </div>

                <!-- Per-course detail divs -->
                <% for (Course c : courses) {
                       List<Module> mods = courseModules.get(c.getC_id());
                       if (mods == null) mods = new ArrayList<Module>();
                       List<Submission> courseSubs = subsByCourse.get(c.getC_id());
                       if (courseSubs == null) courseSubs = new ArrayList<Submission>();
                       int enrolled = enrollmentCounts.containsKey(c.getC_id()) ? enrollmentCounts.get(c.getC_id()) : 0;
                %>
                <div id="course-detail-<%= c.getC_id() %>" class="course-detail-content" style="display:none;">

                    <!-- Course header -->
                    <div class="course-detail-header">
                        <div class="course-title-block">
                            <h2><%= c.getTitle() %></h2>
                            <% if (c.getDescription() != null && !c.getDescription().isEmpty()) { %>
                            <p class="course-desc"><%= c.getDescription() %></p>
                            <% } %>
                        </div>
                        <div class="course-header-actions">
                            <span class="badge-<%= c.getStatus() %>"><%= c.getStatus() %></span>
                            <span style="font-size:12px;color:rgba(255,255,255,0.45);"><%= enrolled %> enrolled</span>
                            <form method="POST" action="<%= cp %>/Course" style="display:inline">
                                <input type="hidden" name="action"   value="status">
                                <input type="hidden" name="courseId" value="<%= c.getC_id() %>">
                                <select name="status" onchange="this.form.submit()" class="status-select">
                                    <option value="draft"     <%= "draft".equals(c.getStatus())     ? "selected":"" %>>Draft</option>
                                    <option value="published" <%= "published".equals(c.getStatus()) ? "selected":"" %>>Published</option>
                                    <option value="archived"  <%= "archived".equals(c.getStatus())  ? "selected":"" %>>Archived</option>
                                </select>
                            </form>
                            <button class="action-btn" style="font-size:12px;padding:5px 12px;"
                                onclick="openEditCourse('<%= c.getC_id() %>','<%= c.getTitle().replace("'","\\'") %>','<%= (c.getDescription()!=null?c.getDescription():"").replace("'","\\'") %>','<%= c.getStatus() %>')">
                                Edit
                            </button>
                            <form method="POST" action="<%= cp %>/Course" style="display:inline"
                                  onsubmit="return confirm('Delete this course and all its content?')">
                                <input type="hidden" name="action"   value="delete">
                                <input type="hidden" name="courseId" value="<%= c.getC_id() %>">
                                <button type="submit" class="action-btn danger" style="font-size:12px;padding:5px 12px;">Delete</button>
                            </form>
                        </div>
                    </div>

                    <!-- Modules & Assignments -->
                    <div class="detail-section">
                        <div class="detail-section-header">
                            <span class="detail-section-label">Modules &amp; Assignments</span>
                            <button class="action-btn" style="font-size:12px;padding:5px 12px;"
                                    onclick="openAddModule('<%= c.getC_id() %>')">+ Add Module</button>
                        </div>

                        <% if (mods.isEmpty()) { %>
                        <div class="no-items-msg">No modules yet. Add a module to get started.</div>
                        <% } else { for (Module m : mods) {
                               List<Assignment> assigns = moduleAssignments.get(m.getMod_id());
                               if (assigns == null) assigns = new ArrayList<Assignment>();
                        %>
                        <div class="module-accordion">
                            <div class="module-header" onclick="toggleModule('<%= m.getMod_id() %>')">
                                <span class="module-order-badge">#<%= m.getOrder() %></span>
                                <span class="module-title"><%= m.getTitle() %></span>
                                <span class="module-assign-count"><%= assigns.size() %> assignments</span>
                                <button class="action-btn" style="font-size:11px;padding:4px 10px;"
                                        onclick="event.stopPropagation();openEditModule('<%= m.getMod_id() %>','<%= c.getC_id() %>','<%= m.getTitle().replace("'","\\'") %>','<%= (m.getDescription()!=null?m.getDescription():"").replace("'","\\'") %>',<%= m.getOrder() %>)">
                                    Edit
                                </button>
                                <form method="POST" action="<%= cp %>/Course" style="display:inline"
                                      onsubmit="return confirm('Delete this module and all its assignments?')">
                                    <input type="hidden" name="action"   value="deleteModule">
                                    <input type="hidden" name="moduleId" value="<%= m.getMod_id() %>">
                                    <input type="hidden" name="courseId" value="<%= c.getC_id() %>">
                                    <button type="submit" class="action-btn danger" style="font-size:11px;padding:4px 10px;"
                                            onclick="event.stopPropagation()">Delete</button>
                                </form>
                                <span class="module-chevron" id="chevron-<%= m.getMod_id() %>">&#9654;</span>
                            </div>
                            <div class="module-body" id="body-<%= m.getMod_id() %>">
                                <% if (m.getDescription() != null && !m.getDescription().isEmpty()) { %>
                                <p class="module-desc"><%= m.getDescription() %></p>
                                <% } %>

                                <% if (assigns.isEmpty()) { %>
                                <div class="no-items-msg" style="padding:12px;">No assignments in this module.</div>
                                <% } else { for (Assignment a : assigns) {
                                       String dueDisp = (a.getDue_date() != null && !a.getDue_date().isEmpty())
                                               ? a.getDue_date().substring(0, Math.min(10, a.getDue_date().length()))
                                               : "No due date";
                                       String maxDisp = a.getMax_score() > 0
                                               ? String.format("%.0f", a.getMax_score()) + " pts" : "—";
                                %>
                                <div class="assignment-row">
                                    <span class="assign-title"><%= a.getTitle() %></span>
                                    <div class="assign-meta">
                                        <span>Due: <%= dueDisp %></span>
                                        <span>Max: <%= maxDisp %></span>
                                    </div>
                                    <button class="action-btn" style="font-size:11px;padding:4px 10px;"
                                            onclick="openEditAssignment('<%= a.getA_id() %>','<%= c.getC_id() %>','<%= a.getTitle().replace("'","\\'") %>','<%= (a.getInstructions()!=null?a.getInstructions().replace("'","\\'").replace("\n"," "):"") %>','<%= a.getDue_date()!=null?a.getDue_date():"" %>',<%= a.getMax_score() %>)">
                                        Edit
                                    </button>
                                    <form method="POST" action="<%= cp %>/Course" style="display:inline"
                                          onsubmit="return confirm('Delete this assignment?')">
                                        <input type="hidden" name="action"       value="deleteAssignment">
                                        <input type="hidden" name="assignmentId" value="<%= a.getA_id() %>">
                                        <input type="hidden" name="courseId"     value="<%= c.getC_id() %>">
                                        <button type="submit" class="action-btn danger" style="font-size:11px;padding:4px 10px;">Delete</button>
                                    </form>
                                </div>
                                <% } } %>

                                <div class="module-body-footer">
                                    <button class="action-btn" style="font-size:12px;"
                                            onclick="openAddAssignment('<%= m.getMod_id() %>','<%= c.getC_id() %>')">
                                        + Add Assignment
                                    </button>
                                </div>
                            </div>
                        </div>
                        <% } } %>
                    </div>

                    <!-- Class List -->
                    <%
                        List<Enrollment> classEnrollments = courseEnrollments.get(c.getC_id());
                        if (classEnrollments == null) classEnrollments = new ArrayList<Enrollment>();
                        Set<String> enrolledIds = new HashSet<String>();
                        for (Enrollment en : classEnrollments) enrolledIds.add(en.getStudent_id());
                    %>
                    <div class="detail-section" style="margin-top:28px;">
                        <div class="detail-section-header">
                            <span class="detail-section-label">Class List</span>
                            <div style="display:flex;gap:8px;align-items:center;">
                                <span class="count-chip"><%= classEnrollments.size() %> enrolled</span>
                                <button class="action-btn" style="font-size:12px;padding:5px 12px;"
                                        onclick="openEnrollModal('<%= c.getC_id() %>')">
                                    + Enroll Student
                                </button>
                            </div>
                        </div>
                        <% if (classEnrollments.isEmpty()) { %>
                        <div class="no-items-msg">No students enrolled yet.</div>
                        <% } else { %>
                        <table class="data-table" style="margin-top:6px;">
                            <thead>
                                <tr><th>Email</th><th>Name</th><th>Enrolled</th><th>Action</th></tr>
                            </thead>
                            <tbody>
                            <% for (Enrollment en : classEnrollments) {
                                   User stu = studentMap.get(en.getStudent_id());
                                   String stuEmail  = (stu != null) ? stu.getEmail() : en.getStudent_id();
                                   String stuName   = (stu != null) ? stu.getFirstName() + " " + stu.getLastName() : "—";
                                   String enrolledAt = en.getEnrolled_at() != null
                                           ? en.getEnrolled_at().substring(0, Math.min(10, en.getEnrolled_at().length()))
                                           : "—";
                            %>
                            <tr>
                                <td style="font-size:13px"><%= stuEmail %></td>
                                <td style="font-size:13px"><%= stuName %></td>
                                <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= enrolledAt %></td>
                                <td>
                                    <form method="POST" action="<%= cp %>/Course" style="display:inline"
                                          onsubmit="return confirm('Drop <%= stuEmail %> from this course?')">
                                        <input type="hidden" name="action"    value="dropStudent">
                                        <input type="hidden" name="courseId"  value="<%= c.getC_id() %>">
                                        <input type="hidden" name="studentId" value="<%= en.getStudent_id() %>">
                                        <button type="submit" class="action-btn danger" style="font-size:11px;padding:4px 10px;">Drop</button>
                                    </form>
                                </td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                        <% } %>
                    </div>

                    <!-- Student Submissions -->
                    <div class="detail-section" style="margin-top:28px;">
                        <div class="detail-section-header">
                            <span class="detail-section-label">Student Submissions</span>
                            <% if (!courseSubs.isEmpty()) { %>
                            <span class="pending-badge"><%= courseSubs.size() %> pending</span>
                            <% } %>
                        </div>

                        <% if (courseSubs.isEmpty()) { %>
                        <div class="no-items-msg">No pending submissions to grade.</div>
                        <% } else { %>
                        <table class="data-table" style="margin-top:6px;">
                            <thead>
                                <tr><th>Student</th><th>Assignment</th><th>Max</th><th>Submitted</th><th>File</th><th>Action</th></tr>
                            </thead>
                            <tbody>
                            <% for (Submission sub : courseSubs) {
                                   User       stu      = studentMap.get(sub.getStudent_id());
                                   Assignment asgn     = assignmentMap.get(sub.getAssignment_id());
                                   String     stuEmail = (stu  != null) ? stu.getEmail()   : sub.getStudent_id();
                                   String     asgnTtl  = (asgn != null) ? asgn.getTitle()  : "—";
                                   String     maxScore = (asgn != null && asgn.getMax_score() > 0)
                                           ? String.format("%.0f", asgn.getMax_score()) : "—";
                            %>
                            <tr>
                                <td style="font-size:13px"><%= stuEmail %></td>
                                <td style="font-size:13px"><%= asgnTtl %></td>
                                <td style="font-size:13px"><%= maxScore %></td>
                                <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= sub.getSubmitted_at() %></td>
                                <td>
                                    <% if (sub.getFile_url() != null && !sub.getFile_url().isEmpty()) { %>
                                    <a href="<%= sub.getFile_url() %>" target="_blank" style="color:#a8d8f0;font-size:13px">View</a>
                                    <% } else { %>&mdash;<% } %>
                                </td>
                                <td>
                                    <button class="action-btn" style="font-size:12px;padding:5px 12px;"
                                            onclick="openGrade('<%= sub.getS_id() %>','<%= asgnTtl.replace("'","\\'") %>','<%= stuEmail %>','<%= maxScore %>','<%= c.getC_id() %>')">
                                        Grade
                                    </button>
                                </td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    </div>

    <!-- =====================================================================
         SECTION: Manage Students
    ====================================================================== -->
    <div id="section-users" class="admin-section" style="display:none;">
        <section class="lower-grid" style="margin-top:25px">
            <div class="panel">
                <div class="panel-header"><h2>Students <span class="count-chip"><%= students.size() %></span></h2></div>
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
                <div class="panel-header"><h2>Teachers <span class="count-chip"><%= teachers.size() %></span></h2></div>
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
                       boolean isOnline  = activeSessions.contains(u[0]);
                       String  roleBadge = "Admin".equals(u[1]) ? "badge-admin" : "badge-guest";
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
                        <p style="color:rgba(255,255,255,0.65);font-size:13px;margin-bottom:16px;line-height:1.5;">All registered users in the authentication database.</p>
                        <a href="<%= cp %>/ReportServlet?type=all_records" target="_blank"
                           style="display:block;padding:10px;border-radius:10px;background:rgba(255,255,255,0.15);color:white;text-decoration:none;font-size:13px;text-align:center;font-weight:600;">
                            &#128438; Download PDF
                        </a>
                    </div>
                </div>
                <div class="panel">
                    <div class="panel-header"><h2>My Record</h2></div>
                    <div style="padding:16px;">
                        <p style="color:rgba(255,255,255,0.65);font-size:13px;margin-bottom:16px;line-height:1.5;">Your account record and activity history.</p>
                        <a href="<%= cp %>/ReportServlet?type=own_records" target="_blank"
                           style="display:block;padding:10px;border-radius:10px;background:rgba(255,255,255,0.15);color:white;text-decoration:none;font-size:13px;text-align:center;font-weight:600;">
                            &#128438; Download PDF
                        </a>
                    </div>
                </div>
                <div class="panel">
                    <div class="panel-header"><h2>Class List</h2></div>
                    <div style="padding:16px;">
                        <p style="color:rgba(255,255,255,0.65);font-size:13px;margin-bottom:16px;line-height:1.5;">Enrolled students per course.</p>
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
     Modals
========================================================== -->

<!-- Add Course Modal -->
<div id="addModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Add Course</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action" value="add">
            <div class="form-group"><label>Course Title</label><input type="text" name="title" placeholder="e.g. Introduction to Java" required></div>
            <div class="form-group"><label>Description</label><textarea name="description" placeholder="Brief course description..."></textarea></div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('addModal')">Cancel</button>
                <button type="submit" class="btn-submit">Create Course</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Course Modal -->
<div id="editCourseModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Edit Course</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action"   value="edit">
            <input type="hidden" name="courseId" id="ecCourseId">
            <div class="form-group"><label>Title</label><input type="text" name="title" id="ecTitle" required></div>
            <div class="form-group"><label>Description</label><textarea name="description" id="ecDesc"></textarea></div>
            <div class="form-group">
                <label>Status</label>
                <select name="status" id="ecStatus">
                    <option value="draft">Draft</option>
                    <option value="published">Published</option>
                    <option value="archived">Archived</option>
                </select>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('editCourseModal')">Cancel</button>
                <button type="submit" class="btn-submit">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<!-- Add Module Modal -->
<div id="addModuleModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Add Module</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action"   value="addModule">
            <input type="hidden" name="courseId" id="amCourseId">
            <div class="form-group"><label>Module Title</label><input type="text" name="title" placeholder="e.g. Introduction to Variables" required></div>
            <div class="form-group"><label>Description (optional)</label><textarea name="description" placeholder="What this module covers..."></textarea></div>
            <div class="form-group"><label>Order</label><input type="number" name="order" value="1" min="1" style="width:100px;"></div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('addModuleModal')">Cancel</button>
                <button type="submit" class="btn-submit">Add Module</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Module Modal -->
<div id="editModuleModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Edit Module</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action"   value="editModule">
            <input type="hidden" name="moduleId" id="emModuleId">
            <input type="hidden" name="courseId" id="emCourseId">
            <div class="form-group"><label>Module Title</label><input type="text" name="title" id="emTitle" required></div>
            <div class="form-group"><label>Description</label><textarea name="description" id="emDesc"></textarea></div>
            <div class="form-group"><label>Order</label><input type="number" name="order" id="emOrder" min="1" style="width:100px;"></div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('editModuleModal')">Cancel</button>
                <button type="submit" class="btn-submit">Save Module</button>
            </div>
        </form>
    </div>
</div>

<!-- Add Assignment Modal -->
<div id="addAssignmentModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Add Assignment</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action"   value="addAssignment">
            <input type="hidden" name="moduleId" id="aaModuleId">
            <input type="hidden" name="courseId" id="aaCourseId">
            <div class="form-group"><label>Assignment Title</label><input type="text" name="title" placeholder="e.g. Hello World Exercise" required></div>
            <div class="form-group"><label>Instructions</label><textarea name="instructions" placeholder="Describe what students must submit..." required></textarea></div>
            <div class="form-group"><label>Due Date (optional)</label><input type="datetime-local" name="dueDate"></div>
            <div class="form-group"><label>Max Score</label><input type="number" name="maxScore" placeholder="e.g. 100" min="0" step="0.5"></div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('addAssignmentModal')">Cancel</button>
                <button type="submit" class="btn-submit">Add Assignment</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Assignment Modal -->
<div id="editAssignmentModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Edit Assignment</h2>
        <form method="POST" action="<%= cp %>/Course">
            <input type="hidden" name="action"       value="editAssignment">
            <input type="hidden" name="assignmentId" id="eaAssignId">
            <input type="hidden" name="courseId"     id="eaCourseId">
            <div class="form-group"><label>Title</label><input type="text" name="title" id="eaTitle" required></div>
            <div class="form-group"><label>Instructions</label><textarea name="instructions" id="eaInstructions" required></textarea></div>
            <div class="form-group"><label>Due Date</label><input type="datetime-local" name="dueDate" id="eaDueDate"></div>
            <div class="form-group"><label>Max Score</label><input type="number" name="maxScore" id="eaMaxScore" min="0" step="0.5"></div>
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeModal('editAssignmentModal')">Cancel</button>
                <button type="submit" class="btn-submit">Save Assignment</button>
            </div>
        </form>
    </div>
</div>

<!-- Enroll Student Modal -->
<div id="enrollModal" class="modal-overlay">
    <div class="modal-box" style="max-width:580px;width:90%;">
        <h2>Enroll Student</h2>
        <input type="text" id="enrollSearch" placeholder="Search by email or name..."
               oninput="filterEnrollRows()"
               style="width:100%;padding:10px 14px;border-radius:12px;border:1px solid rgba(255,255,255,0.2);
                      background:rgba(255,255,255,0.1);color:white;font-size:14px;outline:none;margin-bottom:14px;">
        <input type="hidden" id="enrollCourseId">
        <div style="max-height:340px;overflow-y:auto;">
            <table class="data-table" id="enrollStudentTable">
                <thead><tr><th>Email</th><th>Name</th><th>Action</th></tr></thead>
                <tbody>
                <%
                    // Pre-render all students; JS controls which are visible per course
                    // Each row stores studentId as data attribute for filtering
                %>
                <% for (User s : students) { %>
                <tr data-uid="<%= s.getU_id() %>"
                    data-email="<%= s.getEmail().toLowerCase() %>"
                    data-name="<%= (s.getFirstName() + " " + s.getLastName()).toLowerCase().trim() %>">
                    <td style="font-size:13px"><%= s.getEmail() %></td>
                    <td style="font-size:13px"><%= s.getFirstName() %> <%= s.getLastName() %></td>
                    <td>
                        <form method="POST" action="<%= cp %>/Course" style="display:inline"
                              class="enroll-form">
                            <input type="hidden" name="action"    value="enrollStudent">
                            <input type="hidden" name="courseId"  class="enroll-course-field" value="">
                            <input type="hidden" name="studentId" value="<%= s.getU_id() %>">
                            <button type="submit" class="enroll-btn action-btn"
                                    style="font-size:11px;padding:4px 12px;">Enroll</button>
                        </form>
                        <span class="already-enrolled-label"
                              style="display:none;font-size:11px;color:rgba(60,200,120,0.85);padding:4px 10px;">&#10003; Enrolled</span>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <div style="display:flex;justify-content:flex-end;margin-top:16px;">
            <button type="button" class="btn-cancel" onclick="closeModal('enrollModal')">Close</button>
        </div>
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
            <input type="hidden" name="courseId"     id="gradeCourseId">
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
    document.querySelectorAll('.admin-section').forEach(function(s) { s.style.display = 'none'; });
    document.getElementById('section-' + name).style.display = 'block';
    document.querySelectorAll('.sidebar-nav a[data-section]').forEach(function(a) { a.classList.remove('active'); });
    var link = document.querySelector('.sidebar-nav a[data-section="' + name + '"]');
    if (link) link.classList.add('active');
}

// Auto-show section + courseId from URL params
(function() {
    var params  = new URLSearchParams(window.location.search);
    var section = params.get('section');
    var cid     = params.get('courseId');
    if (section && document.getElementById('section-' + section)) {
        showSection(section);
    }
    if (cid) selectCourse(cid);
})();

// ── Modal helpers ─────────────────────────────────────────────────────────
function openModal(id)  { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }

window.addEventListener('click', function(e) {
    ['addModal','editCourseModal','addModuleModal','editModuleModal',
     'addAssignmentModal','editAssignmentModal','gradeModal','enrollModal'].forEach(function(id) {
        var m = document.getElementById(id);
        if (e.target === m) closeModal(id);
    });
});

// ── Course Management ─────────────────────────────────────────────────────
function selectCourse(courseId) {
    document.getElementById('course-detail-empty').style.display = 'none';
    document.querySelectorAll('.course-detail-content').forEach(function(d) { d.style.display = 'none'; });
    document.querySelectorAll('.course-card-item').forEach(function(c) { c.classList.remove('active'); });

    var detail = document.getElementById('course-detail-' + courseId);
    var card   = document.getElementById('course-card-'   + courseId);
    if (detail) detail.style.display = 'block';
    if (card)   card.classList.add('active');
}

function toggleModule(modId) {
    var body    = document.getElementById('body-'    + modId);
    var chevron = document.getElementById('chevron-' + modId);
    if (!body) return;
    var isOpen = body.classList.contains('open');
    body.classList.toggle('open');
    if (chevron) chevron.classList.toggle('open');
}

// ── Course modal helpers ──────────────────────────────────────────────────
function openEditCourse(id, title, desc, status) {
    document.getElementById('ecCourseId').value = id;
    document.getElementById('ecTitle').value    = title;
    document.getElementById('ecDesc').value     = desc;
    document.getElementById('ecStatus').value   = status;
    openModal('editCourseModal');
}

// ── Module modal helpers ──────────────────────────────────────────────────
function openAddModule(courseId) {
    document.getElementById('amCourseId').value = courseId;
    openModal('addModuleModal');
}

function openEditModule(modId, courseId, title, desc, order) {
    document.getElementById('emModuleId').value = modId;
    document.getElementById('emCourseId').value = courseId;
    document.getElementById('emTitle').value    = title;
    document.getElementById('emDesc').value     = desc;
    document.getElementById('emOrder').value    = order;
    openModal('editModuleModal');
}

// ── Assignment modal helpers ──────────────────────────────────────────────
function openAddAssignment(moduleId, courseId) {
    document.getElementById('aaModuleId').value = moduleId;
    document.getElementById('aaCourseId').value = courseId;
    openModal('addAssignmentModal');
}

function openEditAssignment(aId, courseId, title, instructions, dueDate, maxScore) {
    document.getElementById('eaAssignId').value      = aId;
    document.getElementById('eaCourseId').value      = courseId;
    document.getElementById('eaTitle').value         = title;
    document.getElementById('eaInstructions').value  = instructions;
    document.getElementById('eaDueDate').value       = dueDate || '';
    document.getElementById('eaMaxScore').value      = maxScore || '';
    openModal('editAssignmentModal');
}

// ── Enroll Student modal ──────────────────────────────────────────────────
// enrolledSets: courseId → Set of enrolled studentIds (populated by selectCourse)
var enrolledSets = {};

function openEnrollModal(courseId) {
    document.getElementById('enrollCourseId').value = courseId;
    document.getElementById('enrollSearch').value   = '';

    // Build enrolled set for this course from the class list table rows
    var detail = document.getElementById('course-detail-' + courseId);
    var enrolled = {};
    if (detail) {
        detail.querySelectorAll('form[action$="/Course"] input[name="studentId"]').forEach(function(inp) {
            // only drop-student forms are in the class list
            var form = inp.closest('form');
            if (form && form.querySelector('input[value="dropStudent"]')) {
                enrolled[inp.value] = true;
            }
        });
    }
    enrolledSets[courseId] = enrolled;

    // Update each row: set courseId field and show Enroll vs Enrolled label
    var rows = document.querySelectorAll('#enrollStudentTable tbody tr');
    rows.forEach(function(row) {
        var uid        = row.getAttribute('data-uid');
        var btn        = row.querySelector('.enroll-btn');
        var label      = row.querySelector('.already-enrolled-label');
        var courseField = row.querySelector('.enroll-course-field');
        if (courseField) courseField.value = courseId;
        if (enrolled[uid]) {
            if (btn)   btn.style.display   = 'none';
            if (label) label.style.display = 'inline';
        } else {
            if (btn)   btn.style.display   = '';
            if (label) label.style.display = 'none';
        }
        row.style.display = '';
    });

    openModal('enrollModal');
}

function filterEnrollRows() {
    var q    = document.getElementById('enrollSearch').value.toLowerCase().trim();
    var rows = document.querySelectorAll('#enrollStudentTable tbody tr');
    rows.forEach(function(row) {
        var email = row.getAttribute('data-email') || '';
        var name  = row.getAttribute('data-name')  || '';
        row.style.display = (!q || email.indexOf(q) !== -1 || name.indexOf(q) !== -1) ? '' : 'none';
    });
}

// ── Grade modal helper ────────────────────────────────────────────────────
function openGrade(subId, asgnTitle, student, maxScore, courseId) {
    document.getElementById('gradeSubId').value    = subId;
    document.getElementById('gradeCourseId').value = courseId || '';
    document.getElementById('gradeInfo').textContent = student + ' — ' + asgnTitle;
    document.getElementById('gradeMax').textContent  = maxScore !== '—' ? '/ ' + maxScore : '';
    document.getElementById('gradeScore').max        = maxScore !== '—' ? maxScore : '';
    openModal('gradeModal');
}
</script>

</body>
</html>
