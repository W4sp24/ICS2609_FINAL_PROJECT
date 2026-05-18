package business;

import dao.DerbyAuthDAO;
import dao.PostgresQLDAO;
import model.User;
import util.SecurityUtil;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final DerbyAuthDAO  authDAO;
    private final PostgresQLDAO logDAO;

    public AuthService(DerbyAuthDAO authDAO, PostgresQLDAO logDAO) {
        this.authDAO = authDAO;
        this.logDAO  = logDAO;
    }

   public User login(String username, String plainPassword, String ipAddress) {
    if (SecurityUtil.isBlank(username) || SecurityUtil.isBlank(plainPassword)) {
        LOGGER.warning("Login attempt with blank credentials from IP: " + ipAddress);
        logDAO.log(username, "LOGIN_FAIL_BLANK_INPUT", ipAddress, "UNKNOWN", "AuthService");
        return null;
    }

    String sanitizedUsername = SecurityUtil.sanitizeUsername(username);
    String role = authDAO.validateLogin(sanitizedUsername, plainPassword);

    if (role != null) {
        User user = new User();
        user.setU_id(sanitizedUsername);
        user.setAppRole(role);
        LOGGER.info("Login SUCCESS: " + sanitizedUsername + " [" + role + "] from " + ipAddress);
        logDAO.log(sanitizedUsername, "LOGIN_SUCCESS", ipAddress, role, "AuthService");
        return user;
    } else {
        LOGGER.warning("Login FAILED for username: " + sanitizedUsername + " from IP: " + ipAddress);
        logDAO.log(sanitizedUsername, "LOGIN_FAIL", ipAddress, "UNKNOWN", "AuthService");
        return null;
    }
}
    public void logout(String username, String role, String ipAddress) {
        LOGGER.info("Logout: " + username + " [" + role + "] from " + ipAddress);
        logDAO.log(username, "LOGOUT", ipAddress, role, "AuthService");
    }

    public void logUnauthorizedAccess(String username, String role, String ipAddress, String requestedUrl) {
        String activity = "UNAUTHORIZED_ACCESS: " + requestedUrl;
        LOGGER.warning("Unauthorized access attempt by [" + username + "/" + role
                + "] to [" + requestedUrl + "] from " + ipAddress);
        logDAO.log(username, activity, ipAddress, role, "AuthFilter/RoleFilter");
    }

    public void logCaptchaFailure(String ipAddress, int attemptNumber) {
        String activity = "CAPTCHA_FAIL (attempt " + attemptNumber + ")";
        LOGGER.warning("CAPTCHA failure #" + attemptNumber + " from IP: " + ipAddress);
        logDAO.log("UNKNOWN", activity, ipAddress, "UNKNOWN", "CaptchaServlet");
    }

    public void logCaptchaLockout(String ipAddress) {
        LOGGER.warning("CAPTCHA LOCKOUT triggered from IP: " + ipAddress);
        logDAO.log("UNKNOWN", "CAPTCHA_LOCKOUT", ipAddress, "UNKNOWN", "CaptchaServlet");
    }

    public void logCaptchaSuccess(String ipAddress) {
        LOGGER.info("CAPTCHA verified successfully from IP: " + ipAddress);
        logDAO.log("UNKNOWN", "CAPTCHA_SUCCESS", ipAddress, "UNKNOWN", "CaptchaServlet");
    }

    public void logSessionExpired(String ipAddress, String requestedUrl) {
        String activity = "SESSION_EXPIRED accessing: " + requestedUrl;
        logDAO.log("UNKNOWN", activity, ipAddress, "UNKNOWN", "AuthFilter");
    }

    public DerbyAuthDAO getAuthDAO() { return authDAO; }
    public PostgresQLDAO getLogDAO() { return logDAO; }
}