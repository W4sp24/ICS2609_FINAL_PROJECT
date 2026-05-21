package controller;

import business.AuthService;
import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import model.ActivityLog;
import model.Assignment;
import model.Course;
import model.Enrollment;
import model.Module;
import model.Submission;
import model.User;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/AdminDashboard"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        MySqlBusinessDAO dao  = (MySqlBusinessDAO) getServletContext()
                .getAttribute(AppContextListener.MYSQL_DAO_KEY);
        AuthService      auth = (AuthService) getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);

        // ── Current user identity ─────────────────────────────────────────
        String username  = SessionUtil.getUsername(request);
        String userId    = (String) session.getAttribute(SessionUtil.ATTR_USER_ID);
        User   mysqlUser = dao.getUserByEmail(username);
        String mysqlRole = (mysqlUser != null) ? mysqlUser.getAppRole() : "admin";

        // ── Courses (scoped by role) ──────────────────────────────────────
        List<Course> courses = "teacher".equalsIgnoreCase(mysqlRole) && userId != null
                ? dao.getCoursesByTeacher(userId)
                : dao.getAllCourses();

        Map<String, Integer> enrollmentCounts = new LinkedHashMap<String, Integer>();
        Set<String> enrolledStudentIds = new HashSet<String>();
        for (Course c : courses) {
            List<Enrollment> enrs = dao.getEnrollmentsByCourse(c.getC_id());
            enrollmentCounts.put(c.getC_id(), enrs.size());
            for (Enrollment e : enrs) enrolledStudentIds.add(e.getStudent_id());
        }

        // ── Users (students + teachers) ───────────────────────────────────
        List<User> students = dao.getAllStudents();
        List<User> teachers = dao.getAllTeachers();
        Map<String, User> studentMap = new HashMap<String, User>();
        for (User s : students) studentMap.put(s.getU_id(), s);

        // ── Submissions + assignments (pending grades) ────────────────────
        Map<String, List<Submission>> subsByCourse = new LinkedHashMap<String, List<Submission>>();
        Map<String, Assignment>       assignmentMap = new HashMap<String, Assignment>();
        int totalPending = 0;
        for (Course c : courses) {
            List<Submission> subs = dao.getSubmissionsByCourse(c.getC_id());
            subsByCourse.put(c.getC_id(), subs);
            totalPending += subs.size();
            for (Assignment a : dao.getAssignmentsByCourse(c.getC_id())) {
                assignmentMap.put(a.getA_id(), a);
            }
        }

        // ── Overview stats (role-dependent) ──────────────────────────────
        if ("teacher".equalsIgnoreCase(mysqlRole)) {
            request.setAttribute("myCourseCount",  courses.size());
            request.setAttribute("myStudentCount", enrolledStudentIds.size());
            request.setAttribute("pendingCount",   totalPending);
        } else {
            request.setAttribute("totalStudents", students.size());
            request.setAttribute("totalCourses",  courses.size());
            request.setAttribute("totalTeachers", teachers.size());
        }
        request.setAttribute("mysqlRole", mysqlRole);
        request.setAttribute("userId",    userId);

        // ── Courses tab + Course Management ──────────────────────────────
        request.setAttribute("courses",          courses);
        request.setAttribute("enrollmentCounts", enrollmentCounts);

        Map<String, List<Module>>     courseModules    = new LinkedHashMap<String, List<Module>>();
        Map<String, List<Assignment>> moduleAssignments = new LinkedHashMap<String, List<Assignment>>();
        for (Course c : courses) {
            List<Module> mods = dao.getModulesByCourse(c.getC_id());
            courseModules.put(c.getC_id(), mods);
            for (Module m : mods) {
                moduleAssignments.put(m.getMod_id(), dao.getAssignmentsByModule(m.getMod_id()));
            }
        }
        request.setAttribute("courseModules",     courseModules);
        request.setAttribute("moduleAssignments", moduleAssignments);

        // ── Grades tab ────────────────────────────────────────────────────
        request.setAttribute("submissionsByCourse", subsByCourse);
        request.setAttribute("assignmentMap",       assignmentMap);
        request.setAttribute("studentMap",          studentMap);
        request.setAttribute("totalPending",        totalPending);

        // ── Users tab ─────────────────────────────────────────────────────
        request.setAttribute("students", students);
        request.setAttribute("teachers", teachers);

        // ── Derby + PostgreSQL (Auth DB + Activity Log tabs) ─────────────
        List<String[]>    allUsers      = auth.getAuthDAO().getAllUsers();
        List<ActivityLog> recentLogs    = auth.getLogDAO().getLogs(100);
        Set               activeSessions = (Set) getServletContext()
                .getAttribute(AppContextListener.ACTIVE_SESSIONS_KEY);

        int adminCount = 0, guestCount = 0;
        for (String[] u : allUsers) {
            if ("Admin".equalsIgnoreCase(u[1])) adminCount++;
            else                                guestCount++;
        }

        request.setAttribute("allUsers",       allUsers);
        request.setAttribute("recentLogs",     recentLogs);
        request.setAttribute("activeSessions", activeSessions);
        request.setAttribute("totalUsers",     allUsers.size());
        request.setAttribute("adminCount",     adminCount);
        request.setAttribute("guestCount",     guestCount);
        request.setAttribute("onlineCount",    activeSessions != null ? activeSessions.size() : 0);

        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
