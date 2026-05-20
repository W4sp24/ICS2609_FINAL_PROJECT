<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Course,java.util.List,java.util.Map"%>
<%
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    Map<String, Integer> enrollmentCounts = (Map<String, Integer>) request.getAttribute("enrollmentCounts");
    String mysqlRole = (String) request.getAttribute("mysqlRole");
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Courses — Admin Panel</title>
    <link rel="stylesheet" href="<%= cp %>/css/adminDashboard.css">
    <style>
        .data-table { width:100%; border-collapse:collapse; margin-top:18px; }
        .data-table th { text-align:left; padding:12px 16px; background:rgba(255,255,255,0.1); font-size:13px; }
        .data-table td { padding:12px 16px; border-bottom:1px solid rgba(255,255,255,0.07); font-size:14px; vertical-align:middle; }
        .data-table tr:hover td { background:rgba(255,255,255,0.04); }
        .badge { border-radius:20px; padding:3px 12px; font-size:12px; font-weight:bold; }
        .badge-published { background:rgba(80,200,120,0.35); }
        .badge-draft     { background:rgba(255,255,255,0.15); }
        .badge-archived  { background:rgba(200,80,80,0.35); }
        .action-btn { border:none; padding:6px 14px; border-radius:12px; cursor:pointer; font-size:13px; background:rgba(255,255,255,0.18); color:white; transition:.2s; margin-right:4px; }
        .action-btn:hover { background:rgba(255,255,255,0.3); }
        .action-btn.danger { background:rgba(200,60,60,0.4); }
        .action-btn.danger:hover { background:rgba(200,60,60,0.6); }
        .modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,0.55); z-index:200; align-items:center; justify-content:center; }
        .modal-overlay.open { display:flex; }
        .modal-box { background:rgba(20,40,60,0.96); backdrop-filter:blur(20px); border-radius:24px; padding:36px; min-width:440px; max-width:520px; color:white; border:1px solid rgba(255,255,255,0.15); }
        .modal-box h2 { font-family:'Daruma',sans-serif; font-size:28px; margin-bottom:22px; }
        .form-group { margin-bottom:16px; }
        .form-group label { display:block; font-size:13px; margin-bottom:6px; color:rgba(255,255,255,0.75); }
        .form-group input, .form-group textarea, .form-group select {
            width:100%; padding:10px 14px; border-radius:12px; border:1px solid rgba(255,255,255,0.2);
            background:rgba(255,255,255,0.1); color:white; font-size:14px; outline:none; }
        .form-group textarea { height:80px; resize:vertical; }
        .form-group select option { background:#1e3a50; }
        .modal-actions { display:flex; gap:12px; margin-top:20px; }
        .btn-submit { flex:1; padding:12px; border:none; border-radius:16px; background:rgba(255,255,255,0.9); color:#2f5f79; font-weight:bold; cursor:pointer; transition:.2s; }
        .btn-submit:hover { background:white; }
        .btn-cancel { padding:12px 20px; border:none; border-radius:16px; background:rgba(255,255,255,0.15); color:white; cursor:pointer; }
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
            <a href="Course" class="active">Courses</a>
            <a href="Grade">Grade Submissions</a>
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
                <div><h4>${sessionScope.username}</h4><p><%= mysqlRole %></p></div>
            </div>
        </div>
    </header>

    <section class="panel" style="margin-top:25px">
        <div class="panel-header">
            <h2>Courses</h2>
            <button class="action-btn" onclick="openModal('addModal')">+ Add Course</button>
        </div>

        <% if (courses == null || courses.isEmpty()) { %>
        <div style="text-align:center;padding:40px;color:rgba(255,255,255,0.5)">No courses found.</div>
        <% } else { %>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Status</th>
                    <th>Enrolled</th>
                    <th>Created</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <% for (Course c : courses) {
                   int enrolled = enrollmentCounts.getOrDefault(c.getC_id(), 0);
                   String badgeCls = "badge-" + c.getStatus();
            %>
            <tr>
                <td><strong><%= c.getTitle() %></strong>
                    <% if (c.getDescription() != null && !c.getDescription().isEmpty()) { %>
                    <br><small style="color:rgba(255,255,255,0.55)"><%= c.getDescription() %></small>
                    <% } %>
                </td>
                <td><span class="badge <%= badgeCls %>"><%= c.getStatus() %></span></td>
                <td><%= enrolled %></td>
                <td style="font-size:12px;color:rgba(255,255,255,0.6)"><%= c.getCreated_at() %></td>
                <td>
                    <button class="action-btn"
                        onclick="openEdit('<%= c.getC_id() %>','<%= c.getTitle().replace("'","\\'") %>','<%= (c.getDescription()!=null?c.getDescription():"").replace("'","\\'") %>','<%= c.getStatus() %>')">
                        Edit
                    </button>
                    <form method="POST" action="Course" style="display:inline"
                          onsubmit="return confirm('Delete this course and all its content?')">
                        <input type="hidden" name="action"   value="delete">
                        <input type="hidden" name="courseId" value="<%= c.getC_id() %>">
                        <button type="submit" class="action-btn danger">Delete</button>
                    </form>
                    <!-- Status quick-change -->
                    <form method="POST" action="Course" style="display:inline">
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
    </section>
</main>

<!-- Add Course Modal -->
<div id="addModal" class="modal-overlay">
    <div class="modal-box">
        <h2>Add Course</h2>
        <form method="POST" action="Course">
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
        <form method="POST" action="Course">
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

<script>
    function openModal(id) { document.getElementById(id).classList.add('open'); }
    function closeModal(id) { document.getElementById(id).classList.remove('open'); }
    function openEdit(id, title, desc, status) {
        document.getElementById('editCourseId').value = id;
        document.getElementById('editTitle').value    = title;
        document.getElementById('editDesc').value     = desc;
        document.getElementById('editStatus').value  = status;
        openModal('editModal');
    }
    window.addEventListener('click', function(e) {
        ['addModal','editModal'].forEach(function(id) {
            var m = document.getElementById(id);
            if (e.target === m) closeModal(id);
        });
    });
</script>
</body>
</html>
