package controller;

import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import model.Course;
import model.Enrollment;
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
import java.util.List;
import java.util.Map;

@WebServlet(name = "CourseServlet", urlPatterns = {"/Course"})
public class CourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        MySqlBusinessDAO dao = (MySqlBusinessDAO) getServletContext()
                .getAttribute(AppContextListener.MYSQL_DAO_KEY);

        String username  = SessionUtil.getUsername(request);
        String userId    = (String) session.getAttribute(SessionUtil.ATTR_USER_ID);
        User   mysqlUser = dao.getUserByEmail(username);
        String mysqlRole = (mysqlUser != null) ? mysqlUser.getAppRole() : "admin";

        List<Course> courses = "teacher".equalsIgnoreCase(mysqlRole)
                ? dao.getCoursesByTeacher(userId)
                : dao.getAllCourses();

        Map<String, Integer> enrollmentCounts = new HashMap<>();
        for (Course c : courses) {
            List<Enrollment> enrollments = dao.getEnrollmentsByCourse(c.getC_id());
            enrollmentCounts.put(c.getC_id(), enrollments.size());
        }

        request.setAttribute("courses",          courses);
        request.setAttribute("enrollmentCounts", enrollmentCounts);
        request.setAttribute("mysqlRole",        mysqlRole);
        request.setAttribute("userId",           userId);

        request.getRequestDispatcher("/admin/courses.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        MySqlBusinessDAO dao = (MySqlBusinessDAO) getServletContext()
                .getAttribute(AppContextListener.MYSQL_DAO_KEY);

        String action   = request.getParameter("action");
        String userId   = (String) session.getAttribute(SessionUtil.ATTR_USER_ID);

        if ("add".equals(action)) {
            Course course = new Course();
            course.setTeacher_id(userId);
            course.setTitle(request.getParameter("title"));
            course.setDescription(request.getParameter("description"));
            dao.addCourse(course);

        } else if ("edit".equals(action)) {
            Course course = new Course();
            course.setC_id(request.getParameter("courseId"));
            course.setTitle(request.getParameter("title"));
            course.setDescription(request.getParameter("description"));
            course.setStatus(request.getParameter("status"));
            dao.updateCourse(course);

        } else if ("delete".equals(action)) {
            dao.deleteCourse(request.getParameter("courseId"));

        } else if ("status".equals(action)) {
            dao.updateCourseStatus(
                request.getParameter("courseId"),
                request.getParameter("status")
            );
        }

        response.sendRedirect(request.getContextPath() + "/Course");
    }
}
