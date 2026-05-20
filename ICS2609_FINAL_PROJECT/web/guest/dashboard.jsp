<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.*,java.util.*"%>
<%
    List<Course>                enrolledCourses   = (List<Course>)                request.getAttribute("enrolledCourses");
    Map<String,List<Module>>    courseModules     = (Map<String,List<Module>>)    request.getAttribute("courseModules");
    Map<String,List<Material>>  moduleMaterials   = (Map<String,List<Material>>)  request.getAttribute("moduleMaterials");
    Map<String,List<Assignment>> moduleAssignments = (Map<String,List<Assignment>>) request.getAttribute("moduleAssignments");
    Map<String,Submission>      submissionMap     = (Map<String,Submission>)      request.getAttribute("submissionMap");
    Map<String,Grade>           gradeMap          = (Map<String,Grade>)           request.getAttribute("gradeMap");
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Student Dashboard</title>
    <link rel="stylesheet" href="<%= cp %>/css/adminDashboard.css">
    <style>
        details {
            background: rgba(255,255,255,0.08);
            border-radius: 16px;
            padding: 14px 18px;
            margin-top: 10px;
            cursor: pointer;
        }
        details summary {
            font-size: 15px;
            font-weight: bold;
            list-style: none;
            outline: none;
        }
        details summary::-webkit-details-marker { display: none; }
        details summary::before { content: '▸ '; }
        details[open] summary::before { content: '▾ '; }
        details .detail-body {
            margin-top: 12px;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        .material-item, .assignment-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 14px;
            background: rgba(255,255,255,0.06);
            border-radius: 12px;
            font-size: 14px;
        }
        .material-item a {
            color: #a8d8f0;
            text-decoration: none;
        }
        .material-item a:hover { text-decoration: underline; }
        .badge {
            border-radius: 20px;
            padding: 3px 12px;
            font-size: 12px;
            font-weight: bold;
        }
        .badge-graded    { background: rgba(80,200,120,0.35); }
        .badge-submitted { background: rgba(255,200,60,0.35); }
        .badge-pending   { background: rgba(255,255,255,0.15); }
        .grade-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 18px;
            font-size: 14px;
        }
        .grade-table th {
            text-align: left;
            padding: 10px 14px;
            background: rgba(255,255,255,0.1);
            border-radius: 8px;
        }
        .grade-table td {
            padding: 10px 14px;
            border-bottom: 1px solid rgba(255,255,255,0.07);
            vertical-align: middle;
        }
        .grade-table tr:hover td { background: rgba(255,255,255,0.04); }
        .course-header {
            margin-top: 22px;
            margin-bottom: 6px;
            font-family: 'Daruma', sans-serif;
            font-size: 22px;
            padding: 8px 0 4px 0;
            border-bottom: 1px solid rgba(255,255,255,0.15);
        }
        .empty-state {
            margin-top: 28px;
            text-align: center;
            color: rgba(255,255,255,0.55);
            font-size: 15px;
            padding: 30px;
        }
        .course-card-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px,1fr));
            gap: 16px;
            margin-top: 18px;
        }
        .course-card {
            background: rgba(255,255,255,0.08);
            border-radius: 20px;
            padding: 18px;
            cursor: pointer;
            transition: .2s;
        }
        .course-card:hover {
            background: rgba(255,255,255,0.16);
            transform: translateY(-2px);
        }
        .course-card h4 { font-size: 15px; margin-bottom: 6px; }
        .course-card p  { font-size: 13px; color: rgba(255,255,255,0.7); }
        .section-title {
            font-family: 'Daruma', sans-serif;
            font-size: 38px;
            margin-bottom: 20px;
        }
        .sublist-label {
            font-size: 12px;
            color: rgba(255,255,255,0.55);
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-top: 12px;
            margin-bottom: 4px;
        }
    </style>
</head>
<body>
    <!-- Parallax background -->
    <div id="background">
        <img class="twilight" src="<%= cp %>/images/Twilight.png">
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
        <!-- Top bar -->
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

        <!-- ===================== DASHBOARD SECTION ===================== -->
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
                    <div class="stat-icon">📚</div>
                    <div>
                        <h2>${enrolledCount}</h2>
                        <p>Enrolled Courses</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">📝</div>
                    <div>
                        <h2>${submissionCount}</h2>
                        <p>Submissions Made</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">⭐</div>
                    <div>
                        <h2>${gradeCount}</h2>
                        <p>Grades Received</p>
                    </div>
                </div>
            </section>

            <section class="lower-grid">
                <div class="panel">
                    <div class="panel-header">
                        <h2>My Courses</h2>
                        <button onclick="showSection('courses')">View All</button>
                    </div>
                    <% if (enrolledCourses == null || enrolledCourses.isEmpty()) { %>
                        <div class="empty-state">You are not enrolled in any courses yet.</div>
                    <% } else { %>
                        <div class="course-card-grid">
                            <% for (Course c : enrolledCourses) { %>
                            <div class="course-card" onclick="showSection('courses')">
                                <h4><%= c.getTitle() %></h4>
                                <p><%= c.getDescription() != null ? c.getDescription() : "" %></p>
                            </div>
                            <% } %>
                        </div>
                    <% } %>
                </div>
                <div class="panel">
                    <div class="panel-header">
                        <h2>Quick Actions</h2>
                    </div>
                    <div class="quick-actions">
                        <button onclick="showSection('courses')">Browse Courses</button>
                        <button onclick="showSection('grades')">View My Grades</button>
                        <button onclick="location.href='<%= cp %>/ReportServlet?type=grades'">Generate Grade Report</button>
                    </div>
                </div>
            </section>
        </div><!-- /section-dashboard -->

        <!-- ===================== COURSES SECTION ===================== -->
        <div id="section-courses" style="display:none">
            <div class="section-title">My Courses</div>
            <% if (enrolledCourses == null || enrolledCourses.isEmpty()) { %>
                <div class="panel">
                    <div class="empty-state">You are not enrolled in any courses yet.</div>
                </div>
            <% } else {
                for (Course c : enrolledCourses) {
                    List<Module> mods = courseModules.get(c.getC_id());
            %>
            <div class="panel" style="margin-bottom:18px">
                <div class="panel-header">
                    <h2><%= c.getTitle() %></h2>
                    <span class="badge <%= "published".equals(c.getStatus()) ? "badge-graded" : "badge-pending" %>">
                        <%= c.getStatus() %>
                    </span>
                </div>
                <% if (c.getDescription() != null && !c.getDescription().isEmpty()) { %>
                    <p style="margin-top:10px;color:rgba(255,255,255,0.75);font-size:14px"><%= c.getDescription() %></p>
                <% } %>

                <% if (mods == null || mods.isEmpty()) { %>
                    <div class="empty-state" style="padding:16px">No modules added yet.</div>
                <% } else {
                    for (Module m : mods) {
                        List<Material>   mats  = moduleMaterials.get(m.getMod_id());
                        List<Assignment> asgns = moduleAssignments.get(m.getMod_id());
                %>
                <details>
                    <summary>Module <%= m.getOrder() %>: <%= m.getTitle() %></summary>
                    <div class="detail-body">
                        <% if (m.getDescription() != null && !m.getDescription().isEmpty()) { %>
                            <p style="font-size:13px;color:rgba(255,255,255,0.65)"><%= m.getDescription() %></p>
                        <% } %>

                        <!-- Materials -->
                        <div class="sublist-label">Materials</div>
                        <% if (mats == null || mats.isEmpty()) { %>
                            <div style="font-size:13px;color:rgba(255,255,255,0.45)">No materials yet.</div>
                        <% } else {
                            for (Material mat : mats) { %>
                            <div class="material-item">
                                <span>
                                    <% if ("video".equalsIgnoreCase(mat.getType()))       { %>🎬
                                    <% } else if ("link".equalsIgnoreCase(mat.getType())) { %>🔗
                                    <% } else { %>📄<% } %>
                                </span>
                                <% if (mat.getUrl() != null && !mat.getUrl().isEmpty()) { %>
                                    <a href="<%= mat.getUrl() %>" target="_blank"><%= mat.getTitle() %></a>
                                <% } else { %>
                                    <span><%= mat.getTitle() %></span>
                                <% } %>
                                <span style="margin-left:auto;font-size:12px;color:rgba(255,255,255,0.45)"><%= mat.getType() %></span>
                            </div>
                        <% } } %>

                        <!-- Assignments -->
                        <div class="sublist-label">Assignments</div>
                        <% if (asgns == null || asgns.isEmpty()) { %>
                            <div style="font-size:13px;color:rgba(255,255,255,0.45)">No assignments yet.</div>
                        <% } else {
                            for (Assignment a : asgns) {
                                Submission sub = submissionMap.get(a.getA_id());
                                Grade g = (sub != null) ? gradeMap.get(sub.getS_id()) : null;
                                String statusLabel = (g != null) ? "Graded" : (sub != null) ? "Submitted" : "Not Submitted";
                                String badgeCls    = (g != null) ? "badge-graded" : (sub != null) ? "badge-submitted" : "badge-pending";
                        %>
                        <div class="assignment-item">
                            <span>📋</span>
                            <div style="flex:1">
                                <div style="font-weight:bold"><%= a.getTitle() %></div>
                                <% if (a.getDue_date() != null && !a.getDue_date().isEmpty()) { %>
                                    <div style="font-size:12px;color:rgba(255,255,255,0.55)">Due: <%= a.getDue_date() %></div>
                                <% } %>
                            </div>
                            <span class="badge <%= badgeCls %>"><%= statusLabel %></span>
                            <% if (g != null) { %>
                                <span style="font-size:13px;font-weight:bold">
                                    <%= String.format("%.0f", g.getScore()) %>/<%= String.format("%.0f", a.getMax_score()) %>
                                </span>
                            <% } %>
                        </div>
                        <% } } %>
                    </div>
                </details>
                <%  } // end module loop
                } %>
            </div>
            <% } // end course loop
            } %>
        </div><!-- /section-courses -->

        <!-- ===================== GRADES SECTION ===================== -->
        <div id="section-grades" style="display:none">
            <div class="panel">
                <div class="panel-header">
                    <h2>My Grades</h2>
                    <button onclick="location.href='<%= cp %>/ReportServlet?type=grades'">Generate Grade Report</button>
                </div>

                <% if (enrolledCourses == null || enrolledCourses.isEmpty()) { %>
                    <div class="empty-state">No grades available — you are not enrolled in any courses.</div>
                <% } else {
                    boolean hasAnyAssignment = false;
                    for (Course c : enrolledCourses) {
                        List<Module> mods = courseModules.get(c.getC_id());
                        if (mods == null) continue;
                        for (Module m : mods) {
                            List<Assignment> asgns = moduleAssignments.get(m.getMod_id());
                            if (asgns != null && !asgns.isEmpty()) { hasAnyAssignment = true; break; }
                        }
                        if (hasAnyAssignment) break;
                    }
                    if (!hasAnyAssignment) { %>
                        <div class="empty-state">No assignments found in your courses yet.</div>
                    <% } else { %>
                    <table class="grade-table">
                        <thead>
                            <tr>
                                <th>Course</th>
                                <th>Assignment</th>
                                <th>Status</th>
                                <th>Score</th>
                                <th>Max</th>
                                <th>%</th>
                                <th>Feedback</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% for (Course c : enrolledCourses) {
                               List<Module> mods = courseModules.get(c.getC_id());
                               if (mods == null) continue;
                               for (Module m : mods) {
                                   List<Assignment> asgns = moduleAssignments.get(m.getMod_id());
                                   if (asgns == null) continue;
                                   for (Assignment a : asgns) {
                                       Submission sub = submissionMap.get(a.getA_id());
                                       Grade g = (sub != null) ? gradeMap.get(sub.getS_id()) : null;
                                       String statusLabel = (g != null) ? "Graded" : (sub != null) ? "Submitted" : "Not Submitted";
                                       String badgeCls    = (g != null) ? "badge-graded" : (sub != null) ? "badge-submitted" : "badge-pending";
                        %>
                        <tr>
                            <td><%= c.getTitle() %></td>
                            <td><%= a.getTitle() %></td>
                            <td><span class="badge <%= badgeCls %>"><%= statusLabel %></span></td>
                            <td><%= (g != null) ? String.format("%.1f", g.getScore()) : "—" %></td>
                            <td><%= (a.getMax_score() > 0) ? String.format("%.0f", a.getMax_score()) : "—" %></td>
                            <td><%
                                if (g != null && a.getMax_score() > 0) {
                                    out.print(String.format("%.1f%%", (g.getScore() / a.getMax_score()) * 100));
                                } else { out.print("—"); }
                            %></td>
                            <td style="color:rgba(255,255,255,0.7)"><%= (g != null && g.getFeedback() != null) ? g.getFeedback() : "—" %></td>
                        </tr>
                        <%      }
                               }
                           } %>
                        </tbody>
                    </table>
                    <% } %>
                <% } %>
            </div>
        </div><!-- /section-grades -->

    </main>

    <script>
        function showSection(name) {
            ['dashboard', 'courses', 'grades'].forEach(function(s) {
                document.getElementById('section-' + s).style.display = (s === name) ? 'block' : 'none';
            });
            document.querySelectorAll('.sidebar-nav a').forEach(function(a) {
                a.classList.toggle('active', a.dataset.section === name);
            });
        }
    </script>
</body>
</html>
