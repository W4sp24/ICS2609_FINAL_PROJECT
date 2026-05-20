package filters;

import business.AuthService;
import listeners.AppContextListener;
import util.SecurityUtil;
import util.SessionUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * FILE: filters/AuthFilter.java
 *
 * Servlet filter that protects all pages requiring authentication.
 *
 * Applied to: /admin/*, /guest/*, /ReportServlet, and other protected resources.
 * (Configured in web.xml — see below.)
 *
 * Checks performed (in order):
 *   1. Is the session null?          → redirect to session_expired.jsp
 *   2. Is the user authenticated?    → if not, redirect to unauthorized.jsp
 *   3. Has the session timed out?    → invalidate + redirect to session_expired.jsp
 *   4. All checks pass               → chain.doFilter() (allow through)
 *
 * NOTE: Tomcat's session timeout (web.xml <session-timeout>5</session-timeout>)
 * automatically invalidates sessions server-side after 5 minutes of inactivity.
 * The manual timeout check here is a belt-and-suspenders guard for edge cases
 * (e.g., clock skew, Tomcat restart) and provides a user-friendly redirect.
 *
 * web.xml configuration (add inside <web-app>):
 * --------------------------------------------------
 * <filter>
 *     <filter-name>AuthFilter</filter-name>
 *     <filter-class>filters.AuthFilter</filter-class>
 * </filter>
 * <filter-mapping>
 *     <filter-name>AuthFilter</filter-name>
 *     <url-pattern>/admin/*</url-pattern>
 * </filter-mapping>
 * <filter-mapping>
 *     <filter-name>AuthFilter</filter-name>
 *     <url-pattern>/guest/*</url-pattern>
 * </filter-mapping>
 * <filter-mapping>
 *     <filter-name>AuthFilter</filter-name>
 *     <url-pattern>/ReportServlet</url-pattern>
 * </filter-mapping>
 * --------------------------------------------------
 */
public class AuthFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestUri = request.getRequestURI();
        String ipAddress  = SecurityUtil.getClientIp(request);

        // ── Apply no-cache headers to all protected responses ─────────────────
        // This ensures authenticated pages are not served from browser cache
        // after the session expires or the user logs out.
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma",        "no-cache");
        response.setDateHeader("Expires",   0);

        AuthService authService = (AuthService) request.getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);

        // ── Check 1: Does a session exist? ────────────────────────────────────
        HttpSession session = request.getSession(false);

        if (session == null) {
            LOGGER.info("No session for request: " + requestUri + " | IP: " + ipAddress);
            if (authService != null) authService.logSessionExpired(ipAddress, requestUri);
            response.sendRedirect(request.getContextPath() + "/errors/session_expired.jsp");
            return;
        }

        // ── Check 2: Is the user authenticated? ───────────────────────────────
        if (!SessionUtil.isAuthenticated(request)) {
            LOGGER.warning("Unauthenticated access attempt: " + requestUri + " | IP: " + ipAddress);
            if (authService != null)
                authService.logUnauthorizedAccess("UNKNOWN", "UNKNOWN", ipAddress, requestUri);
            response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
            return;
        }

        // ── Check 3: Has the session timed out based on login timestamp? ──────
        Long loginTimestamp = (Long) session.getAttribute(SessionUtil.ATTR_LOGIN_TIME);
        if (loginTimestamp != null) {
            long sessionAgeMs = System.currentTimeMillis() - loginTimestamp;
            long maxAgeMs     = (long) SessionUtil.SESSION_TIMEOUT_SECONDS * 1000;

            if (sessionAgeMs > maxAgeMs) {
                String timedOutUser = SessionUtil.getUsername(request);
                LOGGER.info("Session expired (age " + (sessionAgeMs / 1000) + "s) for: "
                        + timedOutUser + " | IP: " + ipAddress);
                if (authService != null) authService.logSessionExpired(ipAddress, requestUri);
                java.util.Set activeSessions = (java.util.Set)
                    request.getServletContext().getAttribute(AppContextListener.ACTIVE_SESSIONS_KEY);
                if (activeSessions != null && timedOutUser != null) activeSessions.remove(timedOutUser);
                SessionUtil.invalidateSession(request);
                response.sendRedirect(request.getContextPath() + "/errors/session_expired.jsp");
                return;
            }
        }

        // ── All checks passed — allow the request through ─────────────────────
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOGGER.info("AuthFilter destroyed.");
    }
}
