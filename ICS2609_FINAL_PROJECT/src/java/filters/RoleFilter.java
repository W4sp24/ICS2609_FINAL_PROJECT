package filters;

import business.AuthService;
import listeners.AppContextListener;
import util.SecurityUtil;
import util.SessionUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * FILE: filters/RoleFilter.java
 *
 * Servlet filter enforcing role-based access control on admin-only resources.
 *
 * Applied AFTER AuthFilter (Tomcat applies filters in declaration order).
 * AuthFilter guarantees the user is authenticated; RoleFilter then checks
 * whether the authenticated user has the Admin role.
 *
 * Protected patterns:
 *   /admin/*       — entire admin section
 *   /ReportServlet — report generation (admin only per SRS FR-REP-001)
 *
 * Non-admin users (Guests) attempting to access these URLs are redirected to
 * unauthorized.jsp with a clear error message.
 *
 * web.xml configuration (add AFTER AuthFilter mappings):
 * --------------------------------------------------
 * <filter>
 *     <filter-name>RoleFilter</filter-name>
 *     <filter-class>filters.RoleFilter</filter-class>
 * </filter>
 * <filter-mapping>
 *     <filter-name>RoleFilter</filter-name>
 *     <url-pattern>/admin/*</url-pattern>
 * </filter-mapping>
 * <filter-mapping>
 *     <filter-name>RoleFilter</filter-name>
 *     <url-pattern>/ReportServlet</url-pattern>
 * </filter-mapping>
 * --------------------------------------------------
 */
public class RoleFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(RoleFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("RoleFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestUri = request.getRequestURI();
        String ipAddress  = SecurityUtil.getClientIp(request);
        String username   = SessionUtil.getUsername(request);
        String role       = SessionUtil.getRole(request);

        // ── Role Check: Admin required ────────────────────────────────────────
        if (!SessionUtil.isAdmin(request)) {
            LOGGER.warning("Role DENIED: user [" + username + "/" + role
                    + "] attempted to access admin resource: " + requestUri
                    + " | IP: " + ipAddress);

            // Log unauthorized access attempt
            AuthService authService = (AuthService) request.getServletContext()
                    .getAttribute(AppContextListener.AUTH_SERVICE_KEY);
            if (authService != null) {
                authService.logUnauthorizedAccess(username, role, ipAddress, requestUri);
            }

            // Set a meaningful error attribute for the unauthorized page
            request.getSession(false)
                   .setAttribute("authError", "Access denied. Admin privileges are required.");

            response.sendRedirect(request.getContextPath() + "/errors/unauthorized.jsp");
            return;
        }

        // Admin confirmed — allow through
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOGGER.info("RoleFilter destroyed.");
    }
}
