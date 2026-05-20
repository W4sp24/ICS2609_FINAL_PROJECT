package util;

import model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

public class SessionUtil {

    private static final Logger LOGGER = Logger.getLogger(SessionUtil.class.getName());

    public static final String ATTR_USER_ID        = "userId";
    public static final String ATTR_USERNAME       = "username";
    public static final String ATTR_ROLE           = "userRole";
    public static final String ATTR_LOGIN_TIME     = "loginTimestamp";
    public static final String ATTR_CAPTCHA_DONE   = "captchaVerified";
    public static final String ATTR_CAPTCHA_TRIES  = "captchaAttempts";
    public static final String ATTR_CAPTCHA_LOCK   = "captchaLockTimestamp";

    public static final int SESSION_TIMEOUT_SECONDS = 300;
    public static final long CAPTCHA_LOCK_DURATION_MS = 5L * 60L * 1000L;
    public static final int MAX_CAPTCHA_ATTEMPTS = 3;

    private SessionUtil() {}

    public static HttpSession createAuthenticatedSession(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         User user) {
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);

        session.setAttribute(ATTR_USER_ID,    user.getU_id());
        session.setAttribute(ATTR_USERNAME,   user.getEmail());
        session.setAttribute(ATTR_ROLE,       user.getAppRole());
        session.setAttribute(ATTR_LOGIN_TIME, System.currentTimeMillis());

        String cookieHeader = "JSESSIONID=" + session.getId()
                + "; Path=/; HttpOnly; SameSite=Strict";
        response.setHeader("Set-Cookie", cookieHeader);

        LOGGER.info("Authenticated session created for: " + user.getEmail()
                + " | role: " + user.getAppRole()
                + " | sessionId: " + session.getId());

        return session;
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        return session.getAttribute(ATTR_USERNAME) != null;
    }

    public static String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute(ATTR_ROLE);
    }

    public static String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute(ATTR_USERNAME);
    }

    public static boolean isAdmin(HttpServletRequest request) {
        String role = getRole(request);
        return "Admin".equalsIgnoreCase(role) || "SysAdmin".equalsIgnoreCase(role);
    }

    public static boolean isSysAdmin(HttpServletRequest request) {
        return "SysAdmin".equalsIgnoreCase(getRole(request));
    }

    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute(ATTR_USERNAME);
            LOGGER.info("Invalidating session for: " + username
                    + " | sessionId: " + session.getId());
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // Already invalidated — harmless
            }
        }
    }

    public static int getCaptchaAttempts(HttpSession session) {
        Object val = session.getAttribute(ATTR_CAPTCHA_TRIES);
        return (val instanceof Integer) ? (Integer) val : 0;
    }

    public static int incrementCaptchaAttempts(HttpSession session) {
        int attempts = getCaptchaAttempts(session) + 1;
        session.setAttribute(ATTR_CAPTCHA_TRIES, attempts);
        return attempts;
    }

    public static void setCaptchaLockTimestamp(HttpSession session) {
        session.setAttribute(ATTR_CAPTCHA_LOCK, System.currentTimeMillis());
    }

    public static long getCaptchaLockTimestamp(HttpSession session) {
        Object val = session.getAttribute(ATTR_CAPTCHA_LOCK);
        return (val instanceof Long) ? (Long) val : 0L;
    }

    public static boolean isCaptchaLockExpired(HttpSession session) {
        long lockTime = getCaptchaLockTimestamp(session);
        if (lockTime == 0L) return true;
        return (System.currentTimeMillis() - lockTime) >= CAPTCHA_LOCK_DURATION_MS;
    }

    public static void resetCaptchaState(HttpSession session) {
        session.removeAttribute(ATTR_CAPTCHA_TRIES);
        session.removeAttribute(ATTR_CAPTCHA_LOCK);
        session.setAttribute(ATTR_CAPTCHA_DONE, true);
    }

    public static boolean isCaptchaVerified(HttpSession session) {
        Object val = session.getAttribute(ATTR_CAPTCHA_DONE);
        return Boolean.TRUE.equals(val);
    }
}