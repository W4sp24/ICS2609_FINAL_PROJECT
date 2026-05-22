<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.*,java.util.*,util.SecurityUtil"%>
<%
    List<Course>                 enrolledCourses      = (List<Course>)                 request.getAttribute("enrolledCourses");
    Map<String,List<Module>>     courseModules        = (Map<String,List<Module>>)     request.getAttribute("courseModules");
    Map<String,List<Material>>   moduleMaterials      = (Map<String,List<Material>>)   request.getAttribute("moduleMaterials");
    Map<String,List<Assignment>> moduleAssignments    = (Map<String,List<Assignment>>) request.getAttribute("moduleAssignments");
    Map<String,Submission>       submissionMap        = (Map<String,Submission>)       request.getAttribute("submissionMap");
    Map<String,Grade>            gradeMap             = (Map<String,Grade>)            request.getAttribute("gradeMap");
    Map<String,int[]>            perCourseStats       = (Map<String,int[]>)            request.getAttribute("perCourseStats");
    List<Assignment>             pendingAssignments   = (List<Assignment>)             request.getAttribute("pendingAssignments");
    Map<String,String>           assignmentCourseTitle= (Map<String,String>)           request.getAttribute("assignmentCourseTitle");
    String cp = request.getContextPath();

    if (enrolledCourses       == null) enrolledCourses       = Collections.<Course>emptyList();
    if (pendingAssignments    == null) pendingAssignments    = Collections.<Assignment>emptyList();
    if (perCourseStats        == null) perCourseStats        = Collections.<String,int[]>emptyMap();
    if (assignmentCourseTitle == null) assignmentCourseTitle = Collections.<String,String>emptyMap();
    if (submissionMap         == null) submissionMap         = Collections.<String,Submission>emptyMap();
    if (gradeMap              == null) gradeMap              = Collections.<String,Grade>emptyMap();
    if (courseModules         == null) courseModules         = Collections.<String,List<Module>>emptyMap();
    if (moduleMaterials       == null) moduleMaterials       = Collections.<String,List<Material>>emptyMap();
    if (moduleAssignments     == null) moduleAssignments     = Collections.<String,List<Assignment>>emptyMap();
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Student Dashboard</title>
    <link rel="stylesheet" href="<%= cp %>/css/adminDashboard.css">
    <link rel="stylesheet" href="<%= cp %>/css/studentDashboard.css">
</head>
<body>

<!-- Flash toast notification -->
<div id="flash-toast"></div>

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
        <div class="sidebar-title">STUDENT PANEL</div>
        <nav class="sidebar-nav">
            <a href="#" data-section="dashboard" onclick="showSection('dashboard')" class="active">Dashboard</a>
            <a href="#" data-section="courses"   onclick="showSection('courses')">My Courses</a>
            <a href="#" data-section="grades"    onclick="showSection('grades')">My Grades</a>
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
                    <p>Student</p>
                </div>
            </div>
        </div>
    </header>

    <!-- ═══════════════════════════════════════════════════════
         DASHBOARD SECTION
         ═══════════════════════════════════════════════════════ -->
    <div id="section-dashboard">
        <section class="hero-card">
            <div class="hero-content">
                <h1>Welcome Back,<br>${sessionScope.username}</h1>
                <p>Here's your learning overview for today.</p>
                <div class="hero-buttons">
                    <button onclick="showSection('courses')">My Courses</button>
                    <button onclick="showSection('grades')">My Grades</button>
                </div>
            </div>
            <img src="<%= cp %>/images/flower-book.png" class="hero-image">
        </section>

        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">&#128218;</div>
                <div><h2>${enrolledCount}</h2><p>Enrolled Courses</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128221;</div>
                <div><h2>${submissionCount}</h2><p>Submissions Made</p></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#11088;</div>
                <div><h2>${gradeCount}</h2><p>Grades Received</p></div>
            </div>
        </section>

        <section class="lower-grid">
            <!-- Course preview (up to 4 cards) -->
            <div class="panel">
                <div class="panel-header">
                    <h2>My Courses</h2>
                    <button onclick="showSection('courses')">View All</button>
                </div>
                <% if (enrolledCourses.isEmpty()) { %>
                    <div class="empty-state">You are not enrolled in any courses yet.</div>
                <% } else { %>
                    <div class="course-card-grid-full">
                    <%
                        int previewCount = 0;
                        for (Course c : enrolledCourses) {
                            if (previewCount >= 4) break;
                            int[] stats = perCourseStats.getOrDefault(c.getC_id(), new int[]{0,0,0});
                            int pct = (stats[0] > 0) ? (int)((stats[1] * 100.0) / stats[0]) : 0;
                            previewCount++;
                    %>
                    <div class="course-card-full">
                        <div class="course-accent"></div>
                        <div class="course-card-body">
                            <h4><%= SecurityUtil.sanitizeHtml(c.getTitle()) %></h4>
                            <p class="course-card-desc"><%= SecurityUtil.sanitizeHtml(c.getDescription() != null ? c.getDescription() : "") %></p>
                            <div class="progress-bar"><div class="progress-fill" style="width:<%= pct %>%"></div></div>
                            <div class="progress-label"><%= stats[1] %> / <%= stats[0] %> submitted</div>
                            <div class="course-card-footer">
                                <span class="badge badge-<%= c.getStatus() %>"><%= c.getStatus() %></span>
                                <button class="btn-enter-course" onclick="showCourse('<%= c.getC_id() %>')">Enter &#8594;</button>
                            </div>
                        </div>
                    </div>
                    <% } %>
                    </div>
                    <% if (enrolledCourses.size() > 4) { %>
                        <button class="view-all-link" onclick="showSection('courses')">View all <%= enrolledCourses.size() %> courses &#8594;</button>
                    <% } %>
                <% } %>
            </div>

            <!-- To Do panel -->
            <div class="panel">
                <div class="panel-header"><h2>To Do</h2></div>
                <% if (pendingAssignments.isEmpty()) { %>
                    <div class="todo-empty">
                        <div style="font-size:36px;margin-bottom:10px">&#9989;</div>
                        All caught up! No pending assignments.
                    </div>
                <% } else {
                    int todoShown = 0;
                    for (Assignment a : pendingAssignments) {
                        if (todoShown >= 8) break;
                        String courseTitle = assignmentCourseTitle.getOrDefault(a.getA_id(), "");
                        todoShown++;
                %>
                <div class="todo-item">
                    <span style="font-size:20px">&#128203;</span>
                    <div class="todo-text">
                        <strong><%= SecurityUtil.sanitizeHtml(a.getTitle()) %></strong>
                        <div class="todo-course-tag"><%= courseTitle %></div>
                    </div>
                    <% if (a.getDue_date() != null && !a.getDue_date().isEmpty()) { %>
                        <span class="due-chip"><%= a.getDue_date() %></span>
                    <% } %>
                </div>
                <% } } %>
            </div>
        </section>
    </div><!-- /section-dashboard -->

    <!-- ═══════════════════════════════════════════════════════
         MY COURSES SECTION
         ═══════════════════════════════════════════════════════ -->
    <div id="section-courses" style="display:none">
        <div class="section-title">My Courses</div>
        <% if (enrolledCourses.isEmpty()) { %>
            <div class="panel"><div class="empty-state">You are not enrolled in any courses yet.</div></div>
        <% } else { %>
            <div class="course-card-grid-full">
            <% for (Course c : enrolledCourses) {
                   int[] stats = perCourseStats.getOrDefault(c.getC_id(), new int[]{0,0,0});
                   int pct = (stats[0] > 0) ? (int)((stats[1] * 100.0) / stats[0]) : 0;
            %>
            <div class="course-card-full">
                <div class="course-accent"></div>
                <div class="course-card-body">
                    <h4><%= SecurityUtil.sanitizeHtml(c.getTitle()) %></h4>
                    <p class="course-card-desc"><%= SecurityUtil.sanitizeHtml(c.getDescription() != null ? c.getDescription() : "") %></p>
                    <div class="progress-bar"><div class="progress-fill" style="width:<%= pct %>%"></div></div>
                    <div class="progress-label"><%= stats[1] %> / <%= stats[0] %> submitted &nbsp;&bull;&nbsp; <%= stats[2] %> graded</div>
                    <div class="course-card-footer">
                        <span class="badge badge-<%= c.getStatus() %>"><%= c.getStatus() %></span>
                        <button class="btn-enter-course" onclick="showCourse('<%= c.getC_id() %>')">Enter Course &#8594;</button>
                    </div>
                </div>
            </div>
            <% } %>
            </div>
        <% } %>
    </div><!-- /section-courses -->

    <!-- ═══════════════════════════════════════════════════════
         COURSE DETAIL SECTIONS (one per enrolled course)
         ═══════════════════════════════════════════════════════ -->
    <% for (Course c : enrolledCourses) {
           List<Module> mods = courseModules.getOrDefault(c.getC_id(), Collections.<Module>emptyList());
    %>
    <div id="section-course-<%= c.getC_id() %>" style="display:none">

        <button class="course-detail-back" onclick="showSection('courses')">&#8592; My Courses</button>

        <div class="course-detail-header">
            <div class="section-title"><%= SecurityUtil.sanitizeHtml(c.getTitle()) %></div>
            <span class="badge badge-<%= c.getStatus() %>"><%= c.getStatus() %></span>
        </div>
        <% if (c.getDescription() != null && !c.getDescription().isEmpty()) { %>
            <p class="course-detail-desc"><%= SecurityUtil.sanitizeHtml(c.getDescription()) %></p>
        <% } %>

        <div class="tab-bar">
            <button id="ctabBtn-<%= c.getC_id() %>-modules" class="tab-btn active"
                    onclick="showCourseSub('<%= c.getC_id() %>','modules')">Modules</button>
            <button id="ctabBtn-<%= c.getC_id() %>-grades" class="tab-btn"
                    onclick="showCourseSub('<%= c.getC_id() %>','grades')">My Grades</button>
        </div>

        <!-- Modules tab -->
        <div id="ctab-<%= c.getC_id() %>-modules">
            <% if (mods.isEmpty()) { %>
                <div class="panel" style="margin-top:16px">
                    <div class="empty-state">No modules have been added to this course yet.</div>
                </div>
            <% } else {
                for (Module m : mods) {
                    List<Material>   mats  = moduleMaterials.getOrDefault(m.getMod_id(), Collections.<Material>emptyList());
                    List<Assignment> asgns = moduleAssignments.getOrDefault(m.getMod_id(), Collections.<Assignment>emptyList());
            %>
            <div class="module-card">
                <div class="module-card-header">Module <%= m.getOrder() %>: <%= m.getTitle() %></div>
                <% if (m.getDescription() != null && !m.getDescription().isEmpty()) { %>
                    <p class="module-card-subdesc"><%= m.getDescription() %></p>
                <% } %>

                <div class="module-items">
                    <div class="sublist-label">Materials</div>
                    <% if (mats.isEmpty()) { %>
                        <p style="font-size:13px;color:rgba(255,255,255,0.4)">No materials yet.</p>
                    <% } else { %>
                        <div class="material-pills-row">
                        <% for (Material mat : mats) {
                               String icon = "video".equalsIgnoreCase(mat.getType()) ? "&#127916;" :
                                             "link".equalsIgnoreCase(mat.getType())  ? "&#128279;" : "&#128196;";
                               boolean hasUrl = mat.getUrl() != null && !mat.getUrl().isEmpty();
                        %>
                            <% if (hasUrl) { %>
                                <a href="<%= mat.getUrl() %>" target="_blank" class="material-pill"><%= icon %> <%= mat.getTitle() %></a>
                            <% } else { %>
                                <span class="material-pill"><%= icon %> <%= mat.getTitle() %></span>
                            <% } %>
                        <% } %>
                        </div>
                    <% } %>

                    <div class="sublist-label">Assignments</div>
                    <% if (asgns.isEmpty()) { %>
                        <p style="font-size:13px;color:rgba(255,255,255,0.4)">No assignments yet.</p>
                    <% } else {
                        for (Assignment a : asgns) {
                            Submission sub = submissionMap.get(a.getA_id());
                            Grade g = (sub != null) ? gradeMap.get(sub.getS_id()) : null;
                            String statusLabel = (g != null) ? "Graded" : (sub != null) ? "Submitted" : "Not Submitted";
                            String badgeCls    = (g != null) ? "badge-graded" : (sub != null) ? "badge-submitted" : "badge-pending";
                    %>
                    <div class="assignment-row">
                        <span style="font-size:18px">&#128203;</span>
                        <div class="assignment-info">
                            <strong><%= SecurityUtil.sanitizeHtml(a.getTitle()) %></strong>
                            <% if (a.getDue_date() != null && !a.getDue_date().isEmpty()) { %>
                                <div class="assignment-due">Due: <%= a.getDue_date() %></div>
                            <% } %>
                        </div>
                        <span class="badge <%= badgeCls %>"><%= statusLabel %></span>
                        <% if (g != null) { %>
                            <span class="assignment-score"><%= String.format("%.0f", g.getScore()) %>/<%= String.format("%.0f", a.getMax_score()) %></span>
                        <% } %>
                    </div>
                    <% } } %>
                </div>
            </div>
            <% } } %>
        </div><!-- /ctab-modules -->

        <!-- My Grades tab (scoped to this course) -->
        <div id="ctab-<%= c.getC_id() %>-grades" style="display:none">
            <div class="panel" style="margin-top:16px">
                <div class="panel-header"><h2>My Grades</h2></div>
                <%
                    boolean hasCourseAsgn = false;
                    double cScore = 0, cMax = 0;
                    int cGraded = 0;
                    for (Module m : mods) {
                        if (!moduleAssignments.getOrDefault(m.getMod_id(), Collections.<Assignment>emptyList()).isEmpty()) {
                            hasCourseAsgn = true; break;
                        }
                    }
                %>
                <% if (!hasCourseAsgn) { %>
                    <div class="empty-state">No assignments in this course yet.</div>
                <% } else { %>
                <table class="grade-table">
                    <thead>
                        <tr>
                            <th>Assignment</th>
                            <th>Status</th>
                            <th>Score</th>
                            <th>Max</th>
                            <th>%</th>
                            <th>Feedback</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Module m : mods) {
                           List<Assignment> asgns = moduleAssignments.getOrDefault(m.getMod_id(), Collections.<Assignment>emptyList());
                           for (Assignment a : asgns) {
                               Submission sub = submissionMap.get(a.getA_id());
                               Grade g = (sub != null) ? gradeMap.get(sub.getS_id()) : null;
                               String statusLabel = (g != null) ? "Graded" : (sub != null) ? "Submitted" : "Not Submitted";
                               String badgeCls    = (g != null) ? "badge-graded" : (sub != null) ? "badge-submitted" : "badge-pending";
                               double pctVal = 0; String pctCls = "";
                               if (g != null && a.getMax_score() > 0) {
                                   pctVal = (g.getScore() / a.getMax_score()) * 100;
                                   pctCls = pctVal >= 80 ? "grade-pct-green" : pctVal >= 60 ? "grade-pct-yellow" : "grade-pct-red";
                                   cScore += g.getScore(); cMax += a.getMax_score(); cGraded++;
                               }
                    %>
                    <tr>
                        <td><%= SecurityUtil.sanitizeHtml(a.getTitle()) %></td>
                        <td><span class="badge <%= badgeCls %>"><%= statusLabel %></span></td>
                        <td><%= (g != null) ? String.format("%.1f", g.getScore()) : "&#8212;" %></td>
                        <td><%= (a.getMax_score() > 0) ? String.format("%.0f", a.getMax_score()) : "&#8212;" %></td>
                        <td><% if (g != null && a.getMax_score() > 0) { %><span class="<%= pctCls %>"><%= String.format("%.1f%%", pctVal) %></span><% } else { %>&#8212;<% } %></td>
                        <td style="color:rgba(255,255,255,0.7)"><%= (g != null && g.getFeedback() != null && !g.getFeedback().isEmpty()) ? SecurityUtil.sanitizeHtml(g.getFeedback()) : "&#8212;" %></td>
                    </tr>
                    <% } } %>
                    </tbody>
                    <% if (cGraded > 0) {
                           double avgPct = (cMax > 0) ? (cScore / cMax) * 100 : 0;
                           String avgCls = avgPct >= 80 ? "grade-pct-green" : avgPct >= 60 ? "grade-pct-yellow" : "grade-pct-red";
                    %>
                    <tfoot>
                        <tr class="grade-avg-row">
                            <td colspan="4">Course Average (<%= cGraded %> graded)</td>
                            <td colspan="2"><span class="<%= avgCls %>"><%= String.format("%.1f%%", avgPct) %></span></td>
                        </tr>
                    </tfoot>
                    <% } %>
                </table>
                <% } %>
            </div>
        </div><!-- /ctab-grades -->

    </div><!-- /section-course-{id} -->
    <% } %>

    <!-- ═══════════════════════════════════════════════════════
         MY GRADES SECTION (global, all courses)
         ═══════════════════════════════════════════════════════ -->
    <div id="section-grades" style="display:none">
        <div class="panel">
            <div class="panel-header">
                <h2>My Grades</h2>
                <button onclick="location.href='<%= cp %>/ReportServlet?type=grades'">Generate Report</button>
            </div>

            <% if (enrolledCourses.isEmpty()) { %>
                <div class="empty-state">No grades available — you are not enrolled in any courses.</div>
            <% } else {
                boolean anyGrade = false;
                for (Course c : enrolledCourses) {
                    List<Module> mods = courseModules.getOrDefault(c.getC_id(), Collections.<Module>emptyList());
                    double cTotalScore = 0, cTotalMax = 0;
                    int cGradedCount = 0;
                    boolean courseHasAsgn = false;
                    for (Module m : mods) {
                        List<Assignment> asgns = moduleAssignments.getOrDefault(m.getMod_id(), Collections.<Assignment>emptyList());
                        if (!asgns.isEmpty()) courseHasAsgn = true;
                        for (Assignment a : asgns) {
                            Submission sub = submissionMap.get(a.getA_id());
                            Grade g = (sub != null) ? gradeMap.get(sub.getS_id()) : null;
                            if (g != null && a.getMax_score() > 0) {
                                cTotalScore += g.getScore(); cTotalMax += a.getMax_score(); cGradedCount++;
                            }
                        }
                    }
                    if (!courseHasAsgn) continue;
                    anyGrade = true;
            %>
                <div class="course-group-header"><%= SecurityUtil.sanitizeHtml(c.getTitle()) %></div>
                <table class="grade-table">
                    <thead>
                        <tr>
                            <th>Assignment</th>
                            <th>Status</th>
                            <th>Score</th>
                            <th>Max</th>
                            <th>%</th>
                            <th>Feedback</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Module m : mods) {
                           List<Assignment> asgns = moduleAssignments.getOrDefault(m.getMod_id(), Collections.<Assignment>emptyList());
                           for (Assignment a : asgns) {
                               Submission sub = submissionMap.get(a.getA_id());
                               Grade g = (sub != null) ? gradeMap.get(sub.getS_id()) : null;
                               String statusLabel = (g != null) ? "Graded" : (sub != null) ? "Submitted" : "Not Submitted";
                               String badgeCls    = (g != null) ? "badge-graded" : (sub != null) ? "badge-submitted" : "badge-pending";
                               double pctVal = 0; String pctCls = "";
                               if (g != null && a.getMax_score() > 0) {
                                   pctVal = (g.getScore() / a.getMax_score()) * 100;
                                   pctCls = pctVal >= 80 ? "grade-pct-green" : pctVal >= 60 ? "grade-pct-yellow" : "grade-pct-red";
                               }
                    %>
                    <tr>
                        <td><%= SecurityUtil.sanitizeHtml(a.getTitle()) %></td>
                        <td><span class="badge <%= badgeCls %>"><%= statusLabel %></span></td>
                        <td><%= (g != null) ? String.format("%.1f", g.getScore()) : "&#8212;" %></td>
                        <td><%= (a.getMax_score() > 0) ? String.format("%.0f", a.getMax_score()) : "&#8212;" %></td>
                        <td><% if (g != null && a.getMax_score() > 0) { %><span class="<%= pctCls %>"><%= String.format("%.1f%%", pctVal) %></span><% } else { %>&#8212;<% } %></td>
                        <td style="color:rgba(255,255,255,0.7)"><%= (g != null && g.getFeedback() != null && !g.getFeedback().isEmpty()) ? SecurityUtil.sanitizeHtml(g.getFeedback()) : "&#8212;" %></td>
                    </tr>
                    <% } } %>
                    </tbody>
                    <% if (cGradedCount > 0) {
                           double avgPct = (cTotalMax > 0) ? (cTotalScore / cTotalMax) * 100 : 0;
                           String avgCls = avgPct >= 80 ? "grade-pct-green" : avgPct >= 60 ? "grade-pct-yellow" : "grade-pct-red";
                    %>
                    <tfoot>
                        <tr class="grade-avg-row">
                            <td colspan="4">Average (<%= cGradedCount %> graded)</td>
                            <td colspan="2"><span class="<%= avgCls %>"><%= String.format("%.1f%%", avgPct) %></span></td>
                        </tr>
                    </tfoot>
                    <% } %>
                </table>
            <% } %>
            <% if (!anyGrade) { %>
                <div class="empty-state">No assignments found in your enrolled courses yet.</div>
            <% } %>
            <% } %>
        </div>
    </div><!-- /section-grades -->

</main>

<script>
    function showSection(name) {
        document.querySelectorAll('[id^="section-"]').forEach(function(el) {
            el.style.display = 'none';
        });
        document.getElementById('section-' + name).style.display = 'block';
        document.querySelectorAll('.sidebar-nav a').forEach(function(a) {
            a.classList.toggle('active', a.dataset.section === name);
        });
    }

    function showCourse(courseId) {
        document.querySelectorAll('[id^="section-"]').forEach(function(el) {
            el.style.display = 'none';
        });
        document.getElementById('section-course-' + courseId).style.display = 'block';
        document.querySelectorAll('.sidebar-nav a').forEach(function(a) {
            a.classList.remove('active');
        });
    }

    function showCourseSub(courseId, tab) {
        ['modules', 'grades'].forEach(function(t) {
            document.getElementById('ctab-'    + courseId + '-' + t).style.display = t === tab ? 'block' : 'none';
            document.getElementById('ctabBtn-' + courseId + '-' + t).classList.toggle('active', t === tab);
        });
    }

    // ── Flash toast ───────────────────────────────────────────────
    (function() {
        var params = new URLSearchParams(window.location.search);
        var msg  = params.get('flash');
        var type = params.get('flashType') || 'success';
        if (!msg) return;
        var toast = document.getElementById('flash-toast');
        if (!toast) return;
        toast.className = 'show toast-' + type;
        toast.innerHTML = '<span>' + msg + '</span>'
            + '<button class="toast-close" onclick="this.parentElement.classList.remove(\'show\')">&#x2715;</button>';
        setTimeout(function() { toast.classList.remove('show'); }, 4500);
        var url = new URL(window.location.href);
        url.searchParams.delete('flash');
        url.searchParams.delete('flashType');
        window.history.replaceState({}, '', url.toString());
    })();
</script>
</body>
</html>
