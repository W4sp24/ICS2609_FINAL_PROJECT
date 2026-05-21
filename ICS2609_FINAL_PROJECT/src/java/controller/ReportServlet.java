package controller;

import business.AuthService;
import business.PdfReportBuilder;
import dao.MySqlBusinessDAO;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import listeners.AppContextListener;
import model.ActivityLog;
import model.User;
import util.SessionUtil;

public class ReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String loggedInAdmin = SessionUtil.getUsername(request);
        if (loggedInAdmin == null) {
            response.sendRedirect(request.getContextPath() + "/errors/session_expired.jsp");
            return;
        }

        String type = request.getParameter("type");
        if (type == null) {
            type = "all_records";
        }

        ServletContext context = getServletContext();
        String pdfHeader = context.getInitParameter("pdfHeader");
        String pdfFooter = context.getInitParameter("pdfFooter");

        MySqlBusinessDAO dao = (MySqlBusinessDAO) context.getAttribute(AppContextListener.MYSQL_DAO_KEY);
        AuthService auth = (AuthService) context.getAttribute(AppContextListener.AUTH_SERVICE_KEY);

        PdfReportBuilder builder = new PdfReportBuilder();
        String timestampStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        try {
            if (type.equalsIgnoreCase("all_records")) {
                List<User> usersToReport = new ArrayList<User>();
                List<User> students = dao.getAllStudents();
                List<User> teachers = dao.getAllTeachers();
                if (students != null) usersToReport.addAll(students);
                if (teachers != null) usersToReport.addAll(teachers);

                String filename = "USER_REPORT_" + timestampStr + ".pdf";
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                builder.generateUserReport(response.getOutputStream(), usersToReport, loggedInAdmin,
                        pdfHeader, pdfFooter, "System Enrollment Directory - All Records");

            } else if (type.equalsIgnoreCase("logged_in_admin")) {
                List<User> usersToReport = new ArrayList<User>();
                User adminUser = dao.getUserByEmail(loggedInAdmin);
                if (adminUser == null) {
                    adminUser = new User();
                    adminUser.setEmail(loggedInAdmin);
                    adminUser.setAppRole("admin");
                }
                usersToReport.add(adminUser);

                String filename = "LOGGED_IN_ADMIN_" + timestampStr + ".pdf";
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                builder.generateUserReport(response.getOutputStream(), usersToReport, loggedInAdmin,
                        pdfHeader, pdfFooter, "Administrative Identity Verification Log");

            } else if (type.equalsIgnoreCase("time_bound")) {
                List<ActivityLog> logs = auth.getLogDAO().getLogs(100);

                String filename = "AUDIT_LOGS_" + timestampStr + ".pdf";
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                builder.generateLogReport(response.getOutputStream(), logs, loggedInAdmin,
                        pdfHeader, pdfFooter, "System Security and Operational Audit Trails");

            } else {
                response.sendRedirect(request.getContextPath() + "/errors/error_404.jsp");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
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
