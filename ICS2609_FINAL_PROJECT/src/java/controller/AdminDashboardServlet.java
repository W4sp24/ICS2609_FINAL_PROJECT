package controller;

import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import model.User;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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

        MySqlBusinessDAO dao = (MySqlBusinessDAO) getServletContext()
                .getAttribute(AppContextListener.MYSQL_DAO_KEY);

        String username  = SessionUtil.getUsername(request);
        User mysqlUser   = dao.getUserByEmail(username);
        String mysqlRole = (mysqlUser != null) ? mysqlUser.getAppRole() : "admin";

        request.setAttribute("totalStudents", dao.getAllStudents().size());
        request.setAttribute("totalCourses",  dao.getAllCourses().size());
        request.setAttribute("totalTeachers", dao.getAllTeachers().size());
        request.setAttribute("mysqlRole",     mysqlRole);

        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
