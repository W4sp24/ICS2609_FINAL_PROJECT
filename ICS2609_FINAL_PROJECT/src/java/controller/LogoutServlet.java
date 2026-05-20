package controller;

import business.AuthService;
import listeners.AppContextListener;
import util.SecurityUtil;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * FILE: controller/LogoutServlet.java
 *
 * Handles user logout by invalidating the current session and
 * redirecting to the login page.
 *
 * SECURITY NOTES:
 *   - Session is fully invalidated (not just cleared) so Tomcat removes it.
 *   - The logout event is recorded in the PostgreSQL activity log.
 *   - After logout, the response includes Cache-Control headers to prevent
 *     the browser from caching the previous authenticated pages.
 *   - Redirect (not forward) is used so the browser issues a new GET to
 *     the login page — pressing Back after logout will not re-submit login.
 */
public class LogoutServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    private void performLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        AuthService authService = (AuthService) getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);

        String ipAddress = SecurityUtil.getClientIp(request);

        // Retrieve user info BEFORE invalidating the session
        String username = SessionUtil.getUsername(request);
        String role     = SessionUtil.getRole(request);

        if (username == null) {
            username = "UNKNOWN";
            role     = "UNKNOWN";
        }

        // Log the logout event (before session is gone)
        if (authService != null) {
            authService.logout(username, role, ipAddress);
        }

        LOGGER.info("Logout: " + username + " [" + role + "] from " + ipAddress);

        java.util.Set activeSessions = (java.util.Set)
            getServletContext().getAttribute(AppContextListener.ACTIVE_SESSIONS_KEY);
        if (activeSessions != null && username != null) activeSessions.remove(username);

        // Invalidate the session — removes it from Tomcat's session store
        SessionUtil.invalidateSession(request);

        // ── Prevent caching of authenticated pages ────────────────────────────
        // After logout, clicking Back should NOT restore a protected page.
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma",        "no-cache");
        response.setDateHeader("Expires",   0);

        // Redirect to login page with a logout confirmation message
        response.sendRedirect(request.getContextPath() + "/index.jsp?loggedOut=true");
    }
}
