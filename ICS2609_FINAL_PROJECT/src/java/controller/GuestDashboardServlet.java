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

@WebServlet(name = "GuestDashboardServlet", urlPatterns = {"/GuestDashboard"})
public class GuestDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || SessionUtil.isAdmin(request) || !SessionUtil.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        MySqlBusinessDAO dao = (MySqlBusinessDAO) getServletContext()
                .getAttribute(AppContextListener.MYSQL_DAO_KEY);

        String username = SessionUtil.getUsername(request);

        User mysqlUser = dao.getUserByEmail(username);
        if (mysqlUser == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        String studentId = mysqlUser.getU_id();

        List<Course> enrolledCourses = dao.getEnrolledCourses(studentId);

        Map<String, List<Module>>     courseModules     = new LinkedHashMap<>();
        Map<String, List<Material>>   moduleMaterials   = new LinkedHashMap<>();
        Map<String, List<Assignment>> moduleAssignments = new LinkedHashMap<>();

        for (Course c : enrolledCourses) {
            List<Module> mods = dao.getModulesByCourse(c.getC_id());
            courseModules.put(c.getC_id(), mods);
            for (Module m : mods) {
                moduleMaterials.put(m.getMod_id(),   dao.getMaterialsByModule(m.getMod_id()));
                moduleAssignments.put(m.getMod_id(), dao.getAssignmentsByModule(m.getMod_id()));
            }
        }

        List<Submission> submissions = dao.getSubmissionsByStudent(studentId);
        Map<String, Submission> submissionMap = new LinkedHashMap<>();
        for (Submission s : submissions) {
            submissionMap.put(s.getAssignment_id(), s);
        }

        List<Grade> grades = dao.getGradesByStudent(studentId);
        Map<String, Grade> gradeMap = new LinkedHashMap<>();
        for (Grade g : grades) {
            gradeMap.put(g.getSubmission_id(), g);
        }

        // perCourseStats: courseId → [totalAssignments, submitted, graded]
        Map<String, int[]> perCourseStats = new LinkedHashMap<>();
        for (Course c : enrolledCourses) {
            int total = 0, submitted = 0, graded = 0;
            for (Module m : courseModules.getOrDefault(c.getC_id(), Collections.emptyList())) {
                for (Assignment a : moduleAssignments.getOrDefault(m.getMod_id(), Collections.emptyList())) {
                    total++;
                    Submission sub = submissionMap.get(a.getA_id());
                    if (sub != null) {
                        submitted++;
                        if (gradeMap.containsKey(sub.getS_id())) graded++;
                    }
                }
            }
            perCourseStats.put(c.getC_id(), new int[]{total, submitted, graded});
        }

        // pendingAssignments: unsubmitted assignments sorted by due_date ascending
        List<Assignment> pendingAssignments = new ArrayList<>();
        Map<String, String> assignmentCourseTitle = new LinkedHashMap<>();
        for (Course c : enrolledCourses) {
            for (Module m : courseModules.getOrDefault(c.getC_id(), Collections.emptyList())) {
                for (Assignment a : moduleAssignments.getOrDefault(m.getMod_id(), Collections.emptyList())) {
                    if (!submissionMap.containsKey(a.getA_id())) {
                        pendingAssignments.add(a);
                        assignmentCourseTitle.put(a.getA_id(), c.getTitle());
                    }
                }
            }
        }
        pendingAssignments.sort((a1, a2) -> {
            if (a1.getDue_date() == null) return 1;
            if (a2.getDue_date() == null) return -1;
            return a1.getDue_date().compareTo(a2.getDue_date());
        });

        request.setAttribute("enrolledCourses",   enrolledCourses);
        request.setAttribute("courseModules",     courseModules);
        request.setAttribute("moduleMaterials",   moduleMaterials);
        request.setAttribute("moduleAssignments", moduleAssignments);
        request.setAttribute("submissionMap",     submissionMap);
        request.setAttribute("gradeMap",          gradeMap);
        request.setAttribute("enrolledCount",          enrolledCourses.size());
        request.setAttribute("submissionCount",        submissions.size());
        request.setAttribute("gradeCount",             grades.size());
        request.setAttribute("perCourseStats",         perCourseStats);
        request.setAttribute("pendingAssignments",     pendingAssignments);
        request.setAttribute("assignmentCourseTitle",  assignmentCourseTitle);

        request.getRequestDispatcher("/guest/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
