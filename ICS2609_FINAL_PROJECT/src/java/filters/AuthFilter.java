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


        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma",        "no-cache");
        response.setDateHeader("Expires",   0);

        AuthService authService = (AuthService) request.getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);

        HttpSession session = request.getSession(false);

        if (session == null) {
            LOGGER.info("No session for request: " + requestUri + " | IP: " + ipAddress);
            if (authService != null) authService.logSessionExpired(ipAddress, requestUri);
            if (!response.isCommitted())
                response.sendRedirect(request.getContextPath() + "/errors/session_expired.jsp");
            return;
        }

        if (!SessionUtil.isAuthenticated(request)) {
            LOGGER.warning("Unauthenticated access attempt: " + requestUri + " | IP: " + ipAddress);
            if (authService != null)
                authService.logUnauthorizedAccess("UNKNOWN", "UNKNOWN", ipAddress, requestUri);
            if (!response.isCommitted())
                response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
            return;
        }

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
                if (!response.isCommitted())
                    response.sendRedirect(request.getContextPath() + "/errors/session_expired.jsp");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOGGER.info("AuthFilter destroyed.");
    }
}
