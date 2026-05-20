package controller;

import business.AuthService;
import dao.DerbyAuthDAO;
import listeners.AppContextListener;
import model.ActivityLog;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "SysAdminDashboardServlet", urlPatterns = {"/SysAdminDashboard"})
public class SysAdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.isSysAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
            return;
        }

        AuthService auth = (AuthService) getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);
        DerbyAuthDAO derbyDAO = auth.getAuthDAO();

        Set activeSessions = (Set) getServletContext()
                .getAttribute(AppContextListener.ACTIVE_SESSIONS_KEY);

        List<String[]> allUsers     = derbyDAO.getAllUsers();
        List<ActivityLog> recentLogs = auth.getLogDAO().getLogs(100);

        int sysAdminCount = 0, adminCount = 0, guestCount = 0;
        for (String[] u : allUsers) {
            if ("SysAdmin".equals(u[1]))   sysAdminCount++;
            else if ("Admin".equals(u[1])) adminCount++;
            else                           guestCount++;
        }

        request.setAttribute("allUsers",      allUsers);
        request.setAttribute("recentLogs",    recentLogs);
        request.setAttribute("activeSessions", activeSessions);
        request.setAttribute("totalUsers",    allUsers.size());
        request.setAttribute("sysAdminCount", sysAdminCount);
        request.setAttribute("adminCount",    adminCount);
        request.setAttribute("guestCount",    guestCount);
        request.setAttribute("onlineCount",   activeSessions != null ? activeSessions.size() : 0);

        String action = request.getParameter("action");

        String intervalStr = getServletContext().getInitParameter("sysAdminRefreshInterval");
        int refreshInterval = 30;
        try { refreshInterval = Integer.parseInt(intervalStr); } catch (Exception ignored) {}

        if (!"report".equals(action)) {
            response.setIntHeader("Refresh", refreshInterval);
        }

        if ("report".equals(action)) {
            request.getRequestDispatcher("/sysadmin/report.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/sysadmin/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
