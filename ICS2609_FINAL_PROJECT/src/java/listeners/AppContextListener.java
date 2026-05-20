package listeners;

import business.AuthService;
import dao.DerbyAuthDAO;
import dao.MySqlBusinessDAO;
import dao.PostgresQLDAO;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());
    public static final String AUTH_SERVICE_KEY    = "authService";
    public static final String MYSQL_DAO_KEY       = "mysqlDAO";
    public static final String ACTIVE_SESSIONS_KEY = "activeSessions";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        LOGGER.info("=== ICS2609 Course Management System — Application Starting ===");

        DerbyAuthDAO   authDAO  = new DerbyAuthDAO(ctx);
        PostgresQLDAO  logDAO   = new PostgresQLDAO(ctx);
        MySqlBusinessDAO mysqlDAO = new MySqlBusinessDAO(ctx);

        ctx.setAttribute(MYSQL_DAO_KEY, mysqlDAO);

        AuthService authService = new AuthService(authDAO, logDAO, mysqlDAO);
        ctx.setAttribute(AUTH_SERVICE_KEY, authService);

        ctx.setAttribute(ACTIVE_SESSIONS_KEY,
            java.util.Collections.synchronizedSet(new java.util.HashSet<String>()));

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