package listeners;

import business.AuthService;
import dao.DerbyAuthDAO;
import dao.PostgresQLDAO;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());
    public static final String AUTH_SERVICE_KEY = "authService";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        LOGGER.info("=== ICS2609 Course Management System — Application Starting ===");

        DerbyAuthDAO authDAO = new DerbyAuthDAO(ctx);   
        PostgresQLDAO logDAO = new PostgresQLDAO(ctx);

        AuthService authService = new AuthService(authDAO, logDAO);
        ctx.setAttribute(AUTH_SERVICE_KEY, authService);

        String siteKey = ctx.getInitParameter("recaptchaSiteKey");
        LOGGER.info("reCAPTCHA site key loaded: "
                + (siteKey != null ? siteKey.substring(0, 8) + "..." : "NOT CONFIGURED"));
        LOGGER.info("=== Application initialized successfully ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("=== ICS2609 Course Management System — Application Shutting Down ===");
        sce.getServletContext().removeAttribute(AUTH_SERVICE_KEY);
    }
}