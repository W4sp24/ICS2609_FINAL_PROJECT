package controller;

import business.AuthService;
import business.PdfReportBuilder;
import dao.MySqlBusinessDAO;
import dao.DerbyAuthDAO;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import listeners.AppContextListener;
import model.ActivityLog;
import model.Assignment;
import model.Course;
import model.Enrollment;
import model.Grade;
import model.Submission;
import model.User;
import util.SessionUtil;

public class ReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String loggedInUser = SessionUtil.getUsername(request);
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/errors/session_expired.jsp");
            return;
        }

        String type = request.getParameter("type");
        if (type == null) type = "";

        ServletContext context = getServletContext();
        String pdfHeader = context.getInitParameter("pdfHeader");
        String pdfFooter = context.getInitParameter("pdfFooter");

        MySqlBusinessDAO dao  = (MySqlBusinessDAO) context.getAttribute(AppContextListener.MYSQL_DAO_KEY);
        AuthService      auth = (AuthService)       context.getAttribute(AppContextListener.AUTH_SERVICE_KEY);

        PdfReportBuilder builder      = new PdfReportBuilder();
        String           timestampStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        try {
            switch (type) {

                // ---------------------------------------------------------------
                // Report 1: All Records — Derby users, Admin only
                // ---------------------------------------------------------------
                case "all_records": {
                    if (!SessionUtil.isAdmin(request)) {
                        response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
                        return;
                    }

                    DerbyAuthDAO derbyDAO = auth.getAuthDAO();
                    List<String[]> derbyUsers = derbyDAO.getAllUsers();

                    List<User> userList = new ArrayList<User>();
                    for (String[] u : derbyUsers) {
                        User usr = new User();
                        usr.setEmail(u[0]);
                        usr.setAppRole(u[1]);
                        usr.setCreatedAt(u[2]);
                        userList.add(usr);
                    }

                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition",
                            "attachment; filename=ALL_RECORDS_" + timestampStr + ".pdf");
                    builder.generateUserReport(response.getOutputStream(), userList, loggedInUser,
                            pdfHeader, pdfFooter, "Platform User Registry — All Records");
                    break;
                }

                // ---------------------------------------------------------------
                // Report 2: Own Record — logged-in admin's single Derby record
                // ---------------------------------------------------------------
                case "own_records": {
                    if (!SessionUtil.isAdmin(request)) {
                        response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
                        return;
                    }

                    // Fetch the admin's Derby account record
                    DerbyAuthDAO derbyDAO = auth.getAuthDAO();
                    List<User> userList = new ArrayList<User>();
                    for (String[] u : derbyDAO.getAllUsers()) {
                        if (loggedInUser.equalsIgnoreCase(u[0])) {
                            User usr = new User();
                            usr.setEmail(u[0]);
                            usr.setAppRole(u[1]);
                            usr.setCreatedAt(u[2]);
                            userList.add(usr);
                            break;
                        }
                    }

                    // Fetch this admin's activity history from PostgreSQL
                    List<ActivityLog> userLogs = auth.getLogDAO().getLogsByUser(loggedInUser, 100);

                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition",
                            "attachment; filename=OWN_RECORD_" + timestampStr + ".pdf");
                    builder.generateOwnRecordReport(response.getOutputStream(), userList, userLogs,
                            loggedInUser, pdfHeader, pdfFooter, "Administrator Account Record");
                    break;
                }

 
                case "class_list": {
                    if (!SessionUtil.isAdmin(request)) {
                        response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
                        return;
                    }

                    User teacher = dao.getUserByEmail(loggedInUser);
                    if (teacher == null) {
                        response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
                        return;
                    }

                    // Head admins (MySQL role="admin") see all courses; teachers see only their own
                    String classListRole = teacher.getAppRole();
                    List<Course> courses = "teacher".equalsIgnoreCase(classListRole)
                            ? dao.getCoursesByTeacher(teacher.getU_id())
                            : dao.getAllCourses();

                    Map<String, User> studentMap = new LinkedHashMap<String, User>();
                    for (User s : dao.getAllStudents()) {
                        studentMap.put(s.getU_id(), s);
                    }

                    List<String[]> rows = new ArrayList<String[]>();
                    for (Course course : courses) {
                        List<Enrollment> enrollments = dao.getEnrollmentsByCourse(course.getC_id());
                        for (Enrollment e : enrollments) {
                            User student = studentMap.get(e.getStudent_id());
                            String email = (student != null) ? student.getEmail() : e.getStudent_id();
                            rows.add(new String[]{
                                course.getTitle(),
                                email,
                                e.getEnrolled_at() != null ? e.getEnrolled_at() : "-",
                                e.getStatus()      != null ? e.getStatus()      : "-"
                            });
                        }
                    }

                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition",
                            "attachment; filename=CLASS_LIST_" + timestampStr + ".pdf");
                    builder.generateClassListReport(response.getOutputStream(), rows, loggedInUser,
                            pdfHeader, pdfFooter, "Class Enrollment List by Course");
                    break;
                }

   
                case "time_bound": {
                    if (!SessionUtil.isAdmin(request)) {
                        response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
                        return;
                    }

                    String startStr = request.getParameter("startDate");
                    String endStr   = request.getParameter("endDate");

                    if (startStr == null || startStr.trim().isEmpty()
                            || endStr == null || endStr.trim().isEmpty()) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "Start date and end date are required for the time-bound report.");
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    java.util.Date parsedStart, parsedEnd;
                    try {
                        parsedStart = sdf.parse(startStr.trim());
                        parsedEnd   = sdf.parse(endStr.trim());
                    } catch (Exception ex) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "Invalid date format. Use yyyy-MM-dd.");
                        return;
                    }

                    if (parsedStart.after(parsedEnd)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "Start date must not be after end date.");
                        return;
                    }

                    // End of day: advance end timestamp by one day minus 1 ms
                    Timestamp tsStart = new Timestamp(parsedStart.getTime());
                    Timestamp tsEnd   = new Timestamp(parsedEnd.getTime() + 86400000L - 1L);

                    // Route by MySQL role: "admin" → activity logs, "teacher" → submissions
                    User currentUser = dao.getUserByEmail(loggedInUser);
                    String mysqlRole = (currentUser != null) ? currentUser.getAppRole() : "teacher";

                    if ("admin".equalsIgnoreCase(mysqlRole)) {
                        // Admin (non-teacher): activity logs from PostgreSQL
                        List<ActivityLog> logs = auth.getLogDAO().getLogs(tsStart, tsEnd);

                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition",
                                "attachment; filename=AUDIT_LOGS_" + timestampStr + ".pdf");
                        builder.generateLogReport(response.getOutputStream(), logs, loggedInUser,
                                pdfHeader, pdfFooter,
                                "Activity Audit Log (" + startStr + " to " + endStr + ")");
                    } else {
                        // Teacher: submissions across their courses in the date range
                        User teacher = currentUser;
                        if (teacher == null) {
                            response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
                            return;
                        }

                        List<Course>  courses    = dao.getCoursesByTeacher(teacher.getU_id());
                        Map<String, User>       studentMap    = new LinkedHashMap<String, User>();
                        Map<String, Assignment> assignmentMap = new LinkedHashMap<String, Assignment>();
                        Map<String, String>     courseTitle   = new LinkedHashMap<String, String>();

                        for (User s : dao.getAllStudents()) studentMap.put(s.getU_id(), s);
                        for (Course c : courses)           courseTitle.put(c.getC_id(), c.getTitle());

                        List<String[]> rows = new ArrayList<String[]>();
                        for (Course course : courses) {
                            List<Submission> subs = dao.getAllSubmissionsByCourse(course.getC_id());
                            for (Submission sub : subs) {
                                String submittedAt = sub.getSubmitted_at();
                                if (submittedAt == null) continue;
                                // String prefix comparison works for yyyy-MM-dd HH:mm:ss format
                                String datePrefix = submittedAt.length() >= 10
                                        ? submittedAt.substring(0, 10) : submittedAt;
                                if (datePrefix.compareTo(startStr) < 0
                                        || datePrefix.compareTo(endStr) > 0) continue;

                                User student = studentMap.get(sub.getStudent_id());
                                String studentEmail = (student != null) ? student.getEmail() : sub.getStudent_id();

                                Assignment a = assignmentMap.get(sub.getAssignment_id());
                                if (a == null) {
                                    a = dao.getAssignmentById(sub.getAssignment_id());
                                    if (a != null) assignmentMap.put(sub.getAssignment_id(), a);
                                }
                                String assignTitle = (a != null) ? a.getTitle() : sub.getAssignment_id();

                                String score = "-";
                                if ("graded".equalsIgnoreCase(sub.getStatus())) {
                                    Grade g = dao.getGradeBySubmission(sub.getS_id());
                                    if (g != null) score = String.valueOf(g.getScore());
                                }

                                rows.add(new String[]{
                                    submittedAt,
                                    course.getTitle(),
                                    studentEmail,
                                    assignTitle,
                                    sub.getStatus(),
                                    score
                                });
                            }
                        }

                        // sort rows chronologically by submitted_at (column 0)
                        java.util.Collections.sort(rows, new java.util.Comparator<String[]>() {
                            public int compare(String[] a, String[] b) {
                                return a[0].compareTo(b[0]);
                            }
                        });

                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition",
                                "attachment; filename=COURSE_ACTIVITY_" + timestampStr + ".pdf");
                        builder.generateSubmissionReport(response.getOutputStream(), rows, loggedInUser,
                                pdfHeader, pdfFooter,
                                "Course Submission Activity (" + startStr + " to " + endStr + ")");
                    }
                    break;
                }

          
                case "grades": {
                    if (!SessionUtil.isAuthenticated(request) || SessionUtil.isAdmin(request)) {
                        response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
                        return;
                    }

                    User student = dao.getUserByEmail(loggedInUser);
                    if (student == null) {
                        response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
                        return;
                    }

                    List<Course> courses = dao.getEnrolledCourses(student.getU_id());

                    // Build submission and grade lookup maps once
                    List<Submission> allSubs = dao.getSubmissionsByStudent(student.getU_id());
                    Map<String, Submission> subByAssignment = new LinkedHashMap<String, Submission>();
                    Map<String, Grade>      gradeBySubmission = new LinkedHashMap<String, Grade>();
                    for (Submission s : allSubs) {
                        subByAssignment.put(s.getAssignment_id(), s);
                        Grade g = dao.getGradeBySubmission(s.getS_id());
                        if (g != null) gradeBySubmission.put(s.getS_id(), g);
                    }

                    List<String[]> rows = new ArrayList<String[]>();
                    for (Course course : courses) {
                        List<Assignment> assignments = dao.getAssignmentsByCourse(course.getC_id());
                        for (Assignment a : assignments) {
                            Submission sub = subByAssignment.get(a.getA_id());
                            String status   = "Not Submitted";
                            String score    = "-";
                            String feedback = "-";

                            if (sub != null) {
                                status = "Submitted";
                                if ("graded".equalsIgnoreCase(sub.getStatus())) {
                                    status = "Graded";
                                    Grade g = gradeBySubmission.get(sub.getS_id());
                                    if (g != null) {
                                        score    = String.valueOf(g.getScore());
                                        feedback = g.getFeedback() != null ? g.getFeedback() : "-";
                                    }
                                }
                            }

                            rows.add(new String[]{
                                course.getTitle(),
                                a.getTitle(),
                                String.valueOf(a.getMax_score()),
                                status,
                                score,
                                feedback
                            });
                        }
                    }

                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition",
                            "attachment; filename=GRADE_REPORT_" + timestampStr + ".pdf");
                    builder.generateGradeReport(response.getOutputStream(), rows, loggedInUser,
                            pdfHeader, pdfFooter, "Student Grade Report");
                    break;
                }

                default:
                    response.sendRedirect(request.getContextPath() + "/errors/error_404.jsp");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            if (!response.isCommitted()) {
                response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "PDF Report Generation Servlet";
    }
}
