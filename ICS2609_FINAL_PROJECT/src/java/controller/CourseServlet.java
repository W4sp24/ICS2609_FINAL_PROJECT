package controller;

import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import model.Assignment;
import model.Course;
import model.Enrollment;
import model.Module;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CourseServlet", urlPatterns = {"/Course"})
public class CourseServlet extends HttpServlet {

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

        MySqlBusinessDAO dao    = (MySqlBusinessDAO) getServletContext()
                .getAttribute(AppContextListener.MYSQL_DAO_KEY);
        String action           = request.getParameter("action");
        String userId           = (String) session.getAttribute(SessionUtil.ATTR_USER_ID);
        String courseId         = request.getParameter("courseId");
        String redirectCourseId = (courseId != null && !courseId.isEmpty())
                ? "&courseId=" + courseId : "";

        // ── Course CRUD ───────────────────────────────────────────────────
        if ("add".equals(action)) {
            Course c = new Course();
            c.setTeacher_id(userId);
            c.setTitle(request.getParameter("title"));
            c.setDescription(request.getParameter("description"));
            dao.addCourse(c);
            response.sendRedirect(request.getContextPath()
                    + "/AdminDashboard?section=courseManagement");
            return;

        } else if ("edit".equals(action)) {
            Course c = new Course();
            c.setC_id(courseId);
            c.setTitle(request.getParameter("title"));
            c.setDescription(request.getParameter("description"));
            c.setStatus(request.getParameter("status"));
            dao.updateCourse(c);

        } else if ("delete".equals(action)) {
            dao.deleteCourse(courseId);
            response.sendRedirect(request.getContextPath()
                    + "/AdminDashboard?section=courseManagement");
            return;

        } else if ("status".equals(action)) {
            dao.updateCourseStatus(courseId, request.getParameter("status"));

        } else if ("addModule".equals(action)) {
            Module m = new Module();
            m.setCourse_id(courseId);
            m.setTitle(request.getParameter("title"));
            m.setDescription(request.getParameter("description"));
            m.setOrder(parseIntSafe(request.getParameter("order"), 0));
            dao.addModule(m);

        } else if ("editModule".equals(action)) {
            Module m = new Module();
            m.setMod_id(request.getParameter("moduleId"));
            m.setTitle(request.getParameter("title"));
            m.setDescription(request.getParameter("description"));
            m.setOrder(parseIntSafe(request.getParameter("order"), 0));
            dao.updateModule(m);

        } else if ("deleteModule".equals(action)) {
            dao.deleteModule(request.getParameter("moduleId"));

        } else if ("addAssignment".equals(action)) {
            Assignment a = new Assignment();
            a.setModule_id(request.getParameter("moduleId"));
            a.setTitle(request.getParameter("title"));
            a.setInstructions(request.getParameter("instructions"));
            a.setDue_date(emptyToNull(request.getParameter("dueDate")));
            a.setMax_score(parseDoubleSafe(request.getParameter("maxScore"), 0));
            dao.addAssignment(a);

        } else if ("editAssignment".equals(action)) {
            Assignment a = new Assignment();
            a.setA_id(request.getParameter("assignmentId"));
            a.setTitle(request.getParameter("title"));
            a.setInstructions(request.getParameter("instructions"));
            a.setDue_date(emptyToNull(request.getParameter("dueDate")));
            a.setMax_score(parseDoubleSafe(request.getParameter("maxScore"), 0));
            dao.updateAssignment(a);

        } else if ("deleteAssignment".equals(action)) {
            dao.deleteAssignment(request.getParameter("assignmentId"));

        } else if ("enrollStudent".equals(action)) {
            String studentId = request.getParameter("studentId");
            Enrollment existing = dao.getEnrollment(courseId, studentId);
            if (existing != null && "dropped".equalsIgnoreCase(existing.getStatus())) {
                dao.reEnrollStudent(courseId, studentId);
            } else if (existing == null) {
                dao.enrollStudent(courseId, studentId);
            }

        } else if ("dropStudent".equals(action)) {
            dao.dropEnrollment(courseId, request.getParameter("studentId"));
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
