package controller;

import business.AuthService;
import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import model.*;
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

@WebServlet(name = "GradeServlet", urlPatterns = {"/Grade"})
public class GradeServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GradeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

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

        String action   = request.getParameter("action");
        String username = SessionUtil.getUsername(request);
        String role     = SessionUtil.getRole(request);
        String ip       = SecurityUtil.getClientIp(request);
        String userId   = (String) session.getAttribute(SessionUtil.ATTR_USER_ID);
        String courseId = request.getParameter("courseId");
        String redirect = "/AdminDashboard?section=courseManagement"
                + (courseId != null && !courseId.isEmpty() ? "&courseId=" + courseId : "");

        try {
            if ("grade".equals(action)) {
                String submissionId = request.getParameter("submissionId");
                double score = Double.parseDouble(request.getParameter("score"));
                String maxScoreParam = request.getParameter("maxScore");
                if (score < 0) {
                    response.sendRedirect(request.getContextPath() + redirect
                            + "&flash=Score+cannot+be+negative&flashType=error");
                    return;
                }
                if (maxScoreParam != null && !maxScoreParam.isEmpty()) {
                    double maxScore = Double.parseDouble(maxScoreParam);
                    if (score > maxScore) {
                        response.sendRedirect(request.getContextPath() + redirect
                                + "&flash=Score+exceeds+maximum+allowed&flashType=error");
                        return;
                    }
                }
                Grade grade = new Grade();
                grade.setSubmission_id(submissionId);
                grade.setGraded_by(userId);
                grade.setScore(score);
                grade.setFeedback(request.getParameter("feedback"));
                dao.addGrade(grade);
                String assignTitle = request.getParameter("assignTitle");
                if (assignTitle == null || assignTitle.trim().isEmpty()) assignTitle = submissionId;
                auth.getLogDAO().log(username, "GRADE_ADDED(" + assignTitle + ")", ip, role, "GradeServlet");
                redirect += "&flash=Grade+saved&flashType=success";

            } else if ("update".equals(action)) {
                String gradeId = request.getParameter("gradeId");
                Grade grade = new Grade();
                grade.setG_id(gradeId);
                grade.setScore(Double.parseDouble(request.getParameter("score")));
                grade.setFeedback(request.getParameter("feedback"));
                dao.updateGrade(grade);
                auth.getLogDAO().log(username, "GRADE_UPDATED", ip, role, "GradeServlet");
                redirect += "&flash=Grade+updated&flashType=success";
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "GradeServlet invalid score format", e);
            response.sendRedirect(request.getContextPath() + redirect + "&flash=Invalid+score+value&flashType=error");
            return;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "GradeServlet DB error for action=" + action, e);
            response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
            return;
        }

        response.sendRedirect(request.getContextPath() + redirect);
    }
}
