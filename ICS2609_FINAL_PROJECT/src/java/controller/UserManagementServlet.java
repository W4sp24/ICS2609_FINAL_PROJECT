package controller;

import dao.MySqlBusinessDAO;
import listeners.AppContextListener;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "UserManagementServlet", urlPatterns = {"/UserManagement"})
public class UserManagementServlet extends HttpServlet {

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

        response.sendRedirect(request.getContextPath() + "/AdminDashboard?section=users");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
