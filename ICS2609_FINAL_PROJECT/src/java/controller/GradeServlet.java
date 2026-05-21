package controller;

import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import model.*;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "GradeServlet", urlPatterns = {"/Grade"})
public class GradeServlet extends HttpServlet {

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

        // pending submissions per course
        Map<String, List<Submission>> submissionsByCourse = new LinkedHashMap<>();
        // assignment lookup: assignmentId -> Assignment
        Map<String, Assignment> assignmentMap = new HashMap<>();
        // student lookup: studentId -> User
        Map<String, User> studentMap = new HashMap<>();

        for (User s : dao.getAllStudents()) {
            studentMap.put(s.getU_id(), s);
        }

        int totalPending = 0;
        for (Course c : courses) {
            List<Submission> subs = dao.getSubmissionsByCourse(c.getC_id());
            submissionsByCourse.put(c.getC_id(), subs);
            totalPending += subs.size();

            for (Assignment a : dao.getAssignmentsByCourse(c.getC_id())) {
                assignmentMap.put(a.getA_id(), a);
            }
        }

        request.setAttribute("courses",             courses);
        request.setAttribute("submissionsByCourse", submissionsByCourse);
        request.setAttribute("assignmentMap",       assignmentMap);
        request.setAttribute("studentMap",          studentMap);
        request.setAttribute("totalPending",        totalPending);
        request.setAttribute("mysqlRole",           mysqlRole);

        response.sendRedirect(request.getContextPath() + "/AdminDashboard?section=grades");
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

        String action = request.getParameter("action");
        String userId = (String) session.getAttribute(SessionUtil.ATTR_USER_ID);

        if ("grade".equals(action)) {
            Grade grade = new Grade();
            grade.setSubmission_id(request.getParameter("submissionId"));
            grade.setGraded_by(userId);
            grade.setScore(Double.parseDouble(request.getParameter("score")));
            grade.setFeedback(request.getParameter("feedback"));
            dao.addGrade(grade);

        } else if ("update".equals(action)) {
            Grade grade = new Grade();
            grade.setG_id(request.getParameter("gradeId"));
            grade.setScore(Double.parseDouble(request.getParameter("score")));
            grade.setFeedback(request.getParameter("feedback"));
            dao.updateGrade(grade);
        }

        response.sendRedirect(request.getContextPath() + "/AdminDashboard?section=grades");
    }
}
