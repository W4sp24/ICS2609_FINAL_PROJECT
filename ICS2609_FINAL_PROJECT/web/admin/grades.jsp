<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.*,java.util.List,java.util.Map"%>
<%
    List<Course>                courses            = (List<Course>)                request.getAttribute("courses");
    Map<String,List<Submission>> submissionsByCourse = (Map<String,List<Submission>>) request.getAttribute("submissionsByCourse");
    Map<String,Assignment>      assignmentMap      = (Map<String,Assignment>)      request.getAttribute("assignmentMap");
    Map<String,User>            studentMap         = (Map<String,User>)            request.getAttribute("studentMap");
    int totalPending = (Integer) request.getAttribute("totalPending");
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Grade Submissions — Admin Panel</title>
    <link rel="stylesheet" href="<%= cp %>/css/adminDashboard.css">
    <style>
        .data-table { width:100%; border-collapse:collapse; margin-top:18px; }
        .data-table th { text-align:left; padding:12px 16px; background:rgba(255,255,255,0.1); font-size:13px; }
        .data-table td { padding:12px 16px; border-bottom:1px solid rgba(255,255,255,0.07); font-size:14px; vertical-align:middle; }
        .data-table tr:hover td { background:rgba(255,255,255,0.04); }
        .action-btn { border:none; padding:7px 16px; border-radius:12px; cursor:pointer; font-size:13px; background:rgba(255,255,255,0.18); color:white; transition:.2s; }
        .action-btn:hover { background:rgba(255,255,255,0.32); }
        .course-section { margin-top:22px; }
        .modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,0.55); z-index:200; align-items:center; justify-content:center; }
        .modal-overlay.open { display:flex; }
        .modal-box { background:rgba(20,40,60,0.96); backdrop-filter:blur(20px); border-radius:24px; padding:36px; min-width:440px; color:white; border:1px solid rgba(255,255,255,0.15); }
        .modal-box h2 { font-family:'Daruma',sans-serif; font-size:28px; margin-bottom:22px; }
        .form-group { margin-bottom:16px; }
        .form-group label { display:block; font-size:13px; margin-bottom:6px; color:rgba(255,255,255,0.75); }
        .form-group input, .form-group textarea {
            width:100%; padding:10px 14px; border-radius:12px; border:1px solid rgba(255,255,255,0.2);
            background:rgba(255,255,255,0.1); color:white; font-size:14px; outline:none; }
        .form-group textarea { height:80px; resize:vertical; }
        .modal-actions { display:flex; gap:12px; margin-top:20px; }
        .btn-submit { flex:1; padding:12px; border:none; border-radius:16px; background:rgba(255,255,255,0.9); color:#2f5f79; font-weight:bold; cursor:pointer; }
        .btn-cancel { padding:12px 20px; border:none; border-radius:16px; background:rgba(255,255,255,0.15); color:white; cursor:pointer; }
        .pending-badge { background:rgba(255,180,50,0.35); border-radius:20px; padding:3px 12px; font-size:12px; font-weight:bold; margin-left:8px; }
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
            <a href="Grade" class="active">Grade Submissions</a>
            <a href="UserManagement">Manage Students</a>
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
                <div><h4>${sessionScope.username}</h4><p>Admin / Teacher</p></div>
            </div>
        </div>
    </header>

    <!-- Summary stat -->
    <section class="stats-grid" style="margin-top:25px;grid-template-columns:repeat(3,1fr)">
        <div class="stat-card">
            <div class="stat-icon">📝</div>
            <div><h2><%= totalPending %></h2><p>Pending Submissions</p></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">📚</div>
            <div><h2><%= courses != null ? courses.size() : 0 %></h2><p>Courses</p></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">✅</div>
            <div><h2><%= totalPending == 0 ? "All Done" : "Pending" %></h2><p>Status</p></div>
        </div>
    </section>

    <% if (courses == null || courses.isEmpty()) { %>
    <div class="panel" style="margin-top:22px;text-align:center;padding:40px;color:rgba(255,255,255,0.5)">
        No courses assigned.
    </div>
    <% } else if (totalPending == 0) { %>
    <div class="panel" style="margin-top:22px;text-align:center;padding:40px">
        <div style="font-size:48px;margin-bottom:12px">🎉</div>
        <h2 style="font-family:'Daruma',sans-serif;font-size:28px">All caught up!</h2>
        <p style="margin-top:10px;color:rgba(255,255,255,0.65)">No pending submissions to grade.</p>
    </div>
    <% } else {
        for (Course c : courses) {
            List<Submission> subs = submissionsByCourse.get(c.getC_id());
            if (subs == null || subs.isEmpty()) continue;
    %>
    <div class="panel course-section">
        <div class="panel-header">
            <h2><%= c.getTitle() %> <span class="pending-badge"><%= subs.size() %> pending</span></h2>
        </div>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Student</th>
                    <th>Assignment</th>
                    <th>Max Score</th>
                    <th>Submitted At</th>
                    <th>File</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            <% for (Submission sub : subs) {
                   User student    = studentMap.get(sub.getStudent_id());
                   Assignment asgn = assignmentMap.get(sub.getAssignment_id());
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
                    <a href="<%= sub.getFile_url() %>" target="_blank"
                       style="color:#a8d8f0;font-size:13px">View File</a>
                    <% } else { %> — <% } %>
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
</main>

<!-- Grade Modal -->
<div id="gradeModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Grade Submission</h2>
        <p id="gradeInfo" style="color:rgba(255,255,255,0.7);margin-bottom:18px;font-size:14px"></p>
        <form method="POST" action="Grade">
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
                <button type="button" class="btn-cancel" onclick="closeGrade()">Cancel</button>
                <button type="submit" class="btn-submit">Submit Grade</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openGrade(subId, asgnTitle, student, maxScore) {
        document.getElementById('gradeSubId').value  = subId;
        document.getElementById('gradeInfo').textContent = student + ' — ' + asgnTitle;
        document.getElementById('gradeMax').textContent  = maxScore !== '—' ? '/ ' + maxScore : '';
        document.getElementById('gradeScore').max        = maxScore !== '—' ? maxScore : '';
        document.getElementById('gradeModal').classList.add('open');
    }
    function closeGrade() {
        document.getElementById('gradeModal').classList.remove('open');
    }
    window.addEventListener('click', function(e) {
        var m = document.getElementById('gradeModal');
        if (e.target === m) closeGrade();
    });
</script>
</body>
</html>
