package controller;

import business.AuthService;
import dao.DerbyAuthDAO;
import dao.MySqlBusinessDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import listeners.AppContextListener;
import util.SecurityUtil;
import util.SessionUtil;

@WebServlet(name = "DerbyUserServlet", urlPatterns = {"/DerbyUser"})
public class DerbyUserServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DerbyUserServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
            return;
        }

        AuthService  auth      = (AuthService) getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);
        DerbyAuthDAO derbyDAO  = auth.getAuthDAO();
        String loggedInUser    = SessionUtil.getUsername(request);
        String ip              = request.getRemoteAddr();
        String role            = SessionUtil.getRole(request);

        String action = request.getParameter("action");
        String base   = request.getContextPath() + "/AdminDashboard?section=authdb";

        try {
            if ("add".equals(action)) {
                String username  = request.getParameter("newUsername");
                String password  = request.getParameter("newPassword");
                String derbyRole = request.getParameter("newRole");
                String firstName = request.getParameter("newFirstName");
                String lastName  = request.getParameter("newLastName");

                if (SecurityUtil.isBlank(username) || SecurityUtil.isBlank(password)
                        || SecurityUtil.isBlank(derbyRole)
                        || SecurityUtil.isBlank(firstName) || SecurityUtil.isBlank(lastName)) {
                    response.sendRedirect(base + "&flash=All+fields+required&flashType=error");
                    return;
                }
                if (!"Admin".equals(derbyRole) && !"Guest".equals(derbyRole)) {
                    response.sendRedirect(base + "&flash=Invalid+role&flashType=error");
                    return;
                }

                derbyDAO.addUser(username, password, derbyRole);

                String mysqlRole = "Guest".equals(derbyRole) ? "student" : "admin";
                MySqlBusinessDAO mysqlDAO = (MySqlBusinessDAO) getServletContext()
                        .getAttribute(AppContextListener.MYSQL_DAO_KEY);
                try {
                    mysqlDAO.addUser(username, firstName, lastName, mysqlRole);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Derby user added but MySQL sync failed for " + username, e);
                    auth.getLogDAO().log(loggedInUser, "DERBY_USER_ADDED(" + username + ")", ip, role, "DerbyUserServlet");
                    response.sendRedirect(base + "&flash=User+added+to+Auth+DB+but+failed+to+sync+to+app+database&flashType=info");
                    return;
                }

                auth.getLogDAO().log(loggedInUser, "DERBY_USER_ADDED(" + username + ")", ip, role, "DerbyUserServlet");
                response.sendRedirect(base + "&flash=User+added+successfully&flashType=success");

            } else if ("update".equals(action)) {
                String username  = request.getParameter("editUsername");
                String password  = request.getParameter("editPassword");
                String derbyRole = request.getParameter("editRole");

                if (SecurityUtil.isBlank(username) || SecurityUtil.isBlank(derbyRole)) {
                    response.sendRedirect(base + "&flash=Username+and+role+required&flashType=error");
                    return;
                }
                if (!"Admin".equals(derbyRole) && !"Guest".equals(derbyRole)) {
                    response.sendRedirect(base + "&flash=Invalid+role&flashType=error");
                    return;
                }
                derbyDAO.updateUser(username, password, derbyRole);
                auth.getLogDAO().log(loggedInUser, "DERBY_USER_UPDATED(" + username + ")", ip, role, "DerbyUserServlet");
                response.sendRedirect(base + "&flash=User+updated&flashType=success");

            } else if ("delete".equals(action)) {
                String username = request.getParameter("delUsername");
                if (SecurityUtil.isBlank(username)) {
                    response.sendRedirect(base + "&flash=No+user+specified&flashType=error");
                    return;
                }
                if (username.equalsIgnoreCase(loggedInUser)) {
                    response.sendRedirect(base + "&flash=Cannot+delete+your+own+account&flashType=error");
                    return;
                }
                derbyDAO.deleteUser(username);
                auth.getLogDAO().log(loggedInUser, "DERBY_USER_DELETED(" + username + ")", ip, role, "DerbyUserServlet");
                response.sendRedirect(base + "&flash=User+deleted&flashType=success");

            } else {
                response.sendRedirect(base + "&flash=Unknown+action&flashType=error");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DerbyUser action=" + action + " failed", e);
            if (!response.isCommitted())
                response.sendRedirect(request.getContextPath() + "/errors/error_500.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/AdminDashboard?section=authdb");
    }
}
