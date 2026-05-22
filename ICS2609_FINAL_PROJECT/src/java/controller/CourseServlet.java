package controller;

import business.AuthService;
import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import model.Assignment;
import model.Course;
import model.Enrollment;
import model.Module;
import util.SecurityUtil;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CourseServlet", urlPatterns = {"/Course"})
public class CourseServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CourseServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/AdminDashboard?section=courseManagement");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        String action           = request.getParameter("action");
        String username         = SessionUtil.getUsername(request);
        String role             = SessionUtil.getRole(request);
        String ip               = SecurityUtil.getClientIp(request);
        String userId           = (String) session.getAttribute(SessionUtil.ATTR_USER_ID);
        String courseId         = request.getParameter("courseId");
        String redirectCourseId = (courseId != null && !courseId.isEmpty())
                ? "&courseId=" + courseId : "";

        try {
            // ── Course CRUD ───────────────────────────────────────────────────
            if ("add".equals(action)) {
                Course c = new Course();
                c.setTeacher_id(userId);
                c.setTitle(request.getParameter("title"));
                c.setDescription(request.getParameter("description"));
                dao.addCourse(c);
                auth.getLogDAO().log(username, "COURSE_ADDED: " + c.getTitle(), ip, role, "CourseServlet");
                response.sendRedirect(request.getContextPath()
                        + "/AdminDashboard?section=courseManagement&flash=Course+added&flashType=success");
                return;

            } else if ("edit".equals(action)) {
                Course c = new Course();
                c.setC_id(courseId);
                c.setTitle(request.getParameter("title"));
                c.setDescription(request.getParameter("description"));
                c.setStatus(request.getParameter("status"));
                dao.updateCourse(c);
                auth.getLogDAO().log(username, "COURSE_UPDATED: " + courseId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Course+updated&flashType=success";

            } else if ("delete".equals(action)) {
                dao.deleteCourse(courseId);
                auth.getLogDAO().log(username, "COURSE_DELETED: " + courseId, ip, role, "CourseServlet");
                response.sendRedirect(request.getContextPath()
                        + "/AdminDashboard?section=courseManagement&flash=Course+deleted&flashType=info");
                return;

            } else if ("status".equals(action)) {
                String newStatus = request.getParameter("status");
                dao.updateCourseStatus(courseId, newStatus);
                auth.getLogDAO().log(username, "COURSE_STATUS_CHANGED: " + courseId + " -> " + newStatus, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Status+changed+to+" + newStatus + "&flashType=info";

            } else if ("addModule".equals(action)) {
                Module m = new Module();
                m.setCourse_id(courseId);
                m.setTitle(request.getParameter("title"));
                m.setDescription(request.getParameter("description"));
                m.setOrder(parseIntSafe(request.getParameter("order"), 0));
                dao.addModule(m);
                auth.getLogDAO().log(username, "MODULE_ADDED: " + m.getTitle() + " in course " + courseId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Module+added&flashType=success";

            } else if ("editModule".equals(action)) {
                String moduleId = request.getParameter("moduleId");
                Module m = new Module();
                m.setMod_id(moduleId);
                m.setTitle(request.getParameter("title"));
                m.setDescription(request.getParameter("description"));
                m.setOrder(parseIntSafe(request.getParameter("order"), 0));
                dao.updateModule(m);
                auth.getLogDAO().log(username, "MODULE_UPDATED: " + moduleId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Module+updated&flashType=success";

            } else if ("deleteModule".equals(action)) {
                String moduleId = request.getParameter("moduleId");
                dao.deleteModule(moduleId);
                auth.getLogDAO().log(username, "MODULE_DELETED: " + moduleId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Module+deleted&flashType=info";

            } else if ("addAssignment".equals(action)) {
                Assignment a = new Assignment();
                a.setModule_id(request.getParameter("moduleId"));
                a.setTitle(request.getParameter("title"));
                a.setInstructions(request.getParameter("instructions"));
                a.setDue_date(emptyToNull(request.getParameter("dueDate")));
                a.setMax_score(parseDoubleSafe(request.getParameter("maxScore"), 0));
                dao.addAssignment(a);
                auth.getLogDAO().log(username, "ASSIGNMENT_ADDED: " + a.getTitle() + " in course " + courseId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Assignment+added&flashType=success";

            } else if ("editAssignment".equals(action)) {
                String assignmentId = request.getParameter("assignmentId");
                Assignment a = new Assignment();
                a.setA_id(assignmentId);
                a.setTitle(request.getParameter("title"));
                a.setInstructions(request.getParameter("instructions"));
                a.setDue_date(emptyToNull(request.getParameter("dueDate")));
                a.setMax_score(parseDoubleSafe(request.getParameter("maxScore"), 0));
                dao.updateAssignment(a);
                auth.getLogDAO().log(username, "ASSIGNMENT_UPDATED: " + assignmentId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Assignment+updated&flashType=success";

            } else if ("deleteAssignment".equals(action)) {
                String assignmentId = request.getParameter("assignmentId");
                dao.deleteAssignment(assignmentId);
                auth.getLogDAO().log(username, "ASSIGNMENT_DELETED: " + assignmentId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Assignment+deleted&flashType=info";

            } else if ("enrollStudent".equals(action)) {
                String studentId = request.getParameter("studentId");
                Enrollment existing = dao.getEnrollment(courseId, studentId);
                if (existing != null && "dropped".equalsIgnoreCase(existing.getStatus())) {
                    dao.reEnrollStudent(courseId, studentId);
                    auth.getLogDAO().log(username, "STUDENT_REENROLLED: " + studentId + " in course " + courseId, ip, role, "CourseServlet");
                    redirectCourseId += "&flash=Student+re-enrolled&flashType=success";
                } else if (existing == null) {
                    dao.enrollStudent(courseId, studentId);
                    auth.getLogDAO().log(username, "STUDENT_ENROLLED: " + studentId + " in course " + courseId, ip, role, "CourseServlet");
                    redirectCourseId += "&flash=Student+enrolled&flashType=success";
                }

            } else if ("dropStudent".equals(action)) {
                String studentId = request.getParameter("studentId");
                dao.dropEnrollment(courseId, studentId);
                auth.getLogDAO().log(username, "STUDENT_DROPPED: " + studentId + " from course " + courseId, ip, role, "CourseServlet");
                redirectCourseId += "&flash=Student+dropped&flashType=info";
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "CourseServlet DB error for action=" + action, e);
            response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/AdminDashboard?section=courseManagement" + redirectCourseId);
    }

    private int parseIntSafe(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try { return Integer.parseInt(val.trim()); } catch (NumberFormatException e) { return def; }
    }

    private double parseDoubleSafe(String val, double def) {
        if (val == null || val.trim().isEmpty()) return def;
        try { return Double.parseDouble(val.trim()); } catch (NumberFormatException e) { return def; }
    }

    private String emptyToNull(String val) {
        return (val == null || val.trim().isEmpty()) ? null : val.trim();
    }
}
