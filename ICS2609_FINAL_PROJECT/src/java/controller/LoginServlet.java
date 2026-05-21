package controller;

import business.AuthService;
import listeners.AppContextListener;
import model.User;
import util.SecurityUtil;
import util.SessionUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AuthService authService = (AuthService) getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);
        String ipAddress = SecurityUtil.getClientIp(request);

        // DEBUG: CAPTCHA guard temporarily disabled for direct login testing
        // HttpSession preLoginSession = request.getSession(false);
        // if (preLoginSession == null || !SessionUtil.isCaptchaVerified(preLoginSession)) {
        //     LOGGER.warning("Direct LoginServlet access without CAPTCHA from IP: " + ipAddress);
        //     response.sendRedirect(request.getContextPath() + "/index.jsp");
        //     return;
        // }

        String rawUsername = request.getParameter("username");
        String rawPassword = request.getParameter("password");
        String username = SecurityUtil.sanitizeUsername(rawUsername);

        if (SecurityUtil.isBlank(username)) {
            LOGGER.warning("Login attempt with blank username from IP: " + ipAddress);
            request.setAttribute("errorMessage", "Username is required.");
            request.getRequestDispatcher("/errors/incorrect_username.jsp").forward(request, response);
            return;
        }

        if (SecurityUtil.isBlank(rawPassword)) {
            LOGGER.warning("Login attempt with blank password for user: " + username);
            request.setAttribute("errorMessage", "Password is required.");
            request.getRequestDispatcher("/errors/incorrect_password.jsp").forward(request, response);
            return;
        }

        User user = authService.login(username, rawPassword, ipAddress);

        if (user == null) {
            request.setAttribute("errorMessage", "Invalid username or password. Please try again.");
            request.setAttribute("username", SecurityUtil.sanitizeHtml(username));
            request.getRequestDispatcher("/errors/incorrect_password.jsp").forward(request, response);
            return;
        }

        HttpSession session = SessionUtil.createAuthenticatedSession(request, response, user);

        LOGGER.info("Login SUCCESS: " + user.getEmail()
                + " [" + user.getAppRole() + "] from " + ipAddress
                + " | new sessionId: " + session.getId());

        java.util.Set activeSessions = (java.util.Set)
            getServletContext().getAttribute(AppContextListener.ACTIVE_SESSIONS_KEY);
        if (activeSessions != null) activeSessions.add(user.getEmail());

        String derbyRole = user.getAppRole();
        if ("Admin".equalsIgnoreCase(derbyRole)) {
            response.sendRedirect(request.getContextPath() + "/AdminDashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/GuestDashboard");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}