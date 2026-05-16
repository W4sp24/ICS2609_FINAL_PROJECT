package listeners;

import business.AuthService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * FILE: listeners/AppContextListener.java
 *
 * Application lifecycle listener.
 *
 * On startup:
 *   - Stores the shared AuthService instance in the ServletContext so all
 *     servlets and filters can access it without creating new instances.
 *   - Logs startup/shutdown events for operational tracing.
 *
 * On shutdown:
 *   - Allows for graceful resource cleanup if needed.
 *
 * Declared via @WebListener — no web.xml entry required.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    /** ServletContext key for the shared AuthService instance */
    public static final String AUTH_SERVICE_KEY = "authService";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        LOGGER.info("=== ICS2609 Course Management System — Application Starting ===");

        // Initialize the shared AuthService (singleton per application)
        AuthService authService = new AuthService();
        ctx.setAttribute(AUTH_SERVICE_KEY, authService);

        // Log configured reCAPTCHA site key (never log the secret key)
        String siteKey = ctx.getInitParameter("recaptchaSiteKey");
        LOGGER.info("reCAPTCHA site key loaded: "
                + (siteKey != null ? siteKey.substring(0, 8) + "..." : "NOT CONFIGURED"));

        LOGGER.info("=== Application initialized successfully ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("=== ICS2609 Course Management System — Application Shutting Down ===");
        // Remove shared resources from context
        sce.getServletContext().removeAttribute(AUTH_SERVICE_KEY);
    }
}
