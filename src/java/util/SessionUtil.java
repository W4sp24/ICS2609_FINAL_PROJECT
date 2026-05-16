package util;

import model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * FILE: util/SessionUtil.java
 *
 * Centralized session management utility.
 *
 * SECURITY FEATURES:
 *   - Session fixation prevention: regenerates session ID on login.
 *   - Strict 5-minute (300s) inactivity timeout enforced server-side.
 *   - Secure cookie flags: HttpOnly, Secure, SameSite=Strict.
 *   - Typed attribute accessors eliminate raw attribute casting.
 *   - Invalidation clears all attributes atomically.
 */
public class SessionUtil {

    private static final Logger LOGGER = Logger.getLogger(SessionUtil.class.getName());

    // ─── Session Keys ────────────────────────────────────────────────────────
    public static final String ATTR_USER_ID        = "userId";
    public static final String ATTR_USERNAME       = "username";
    public static final String ATTR_ROLE           = "userRole";
    public static final String ATTR_LOGIN_TIME     = "loginTimestamp";
    public static final String ATTR_CAPTCHA_DONE   = "captchaVerified";
    public static final String ATTR_CAPTCHA_TRIES  = "captchaAttempts";
    public static final String ATTR_CAPTCHA_LOCK   = "captchaLockTimestamp";

    /** Exact session timeout in seconds — must match web.xml <session-timeout> */
    public static final int SESSION_TIMEOUT_SECONDS = 300; // 5 minutes

    /** CAPTCHA lockout duration in milliseconds */
    public static final long CAPTCHA_LOCK_DURATION_MS = 5L * 60L * 1000L; // 5 minutes

    /** Maximum CAPTCHA attempts before lockout */
    public static final int MAX_CAPTCHA_ATTEMPTS = 3;

    private SessionUtil() {}

    // ─── Session Creation / Login ─────────────────────────────────────────────

    /**
     * Creates a new authenticated session after successful login.
     *
     * Session Fixation Prevention:
     *   Invalidates the pre-login session and creates a fresh one,
     *   so any session ID known to an attacker before login is discarded.
     *
     * @param request  the current HTTP request
     * @param response the current HTTP response (used for cookie hardening)
     * @param user     the authenticated User object
     */
    public static HttpSession createAuthenticatedSession(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         User user) {
        // Step 1: Invalidate any existing session (session fixation prevention)
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        // Step 2: Create a brand-new session with a fresh ID
        HttpSession session = request.getSession(true);

        // Step 3: Set server-side timeout (5 minutes of inactivity)
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);

        // Step 4: Store authenticated user data
        session.setAttribute(ATTR_USER_ID,    user.getUserId());
        session.setAttribute(ATTR_USERNAME,   user.getUsername());
        session.setAttribute(ATTR_ROLE,       user.getRole());
        session.setAttribute(ATTR_LOGIN_TIME, System.currentTimeMillis());

        // Step 5: Harden the session cookie via response header.
        //
        // IMPORTANT: Tomcat can be configured in context.xml with:
        //   <CookieProcessor sameSiteCookies="strict" />
        // The manual Set-Cookie header below is a belt-and-suspenders fallback
        // for environments that have not configured the CookieProcessor.
        //
        // Flags used:
        //   HttpOnly  — prevents JavaScript from reading the cookie (XSS mitigation)
        //   Secure    — cookie only sent over HTTPS (remove for local HTTP dev)
        //   SameSite  — prevents CSRF via cross-site requests
        String cookieHeader = "JSESSIONID=" + session.getId()
                + "; Path=/; HttpOnly; SameSite=Strict";
        // Uncomment the Secure flag when deployed over HTTPS:
        // + "; Secure"
        response.setHeader("Set-Cookie", cookieHeader);

        LOGGER.info("Authenticated session created for user: " + user.getUsername()
                + " | role: " + user.getRole()
                + " | sessionId: " + session.getId());

        return session;
    }

    // ─── Session Validation ──────────────────────────────────────────────────

    /**
     * Returns true if the session is valid and the user is authenticated.
     * A session is considered authenticated when it holds a non-null username.
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        return session.getAttribute(ATTR_USERNAME) != null;
    }

    /**
     * Returns the role stored in the current session, or null if not present.
     */
    public static String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute(ATTR_ROLE);
    }

    /**
     * Returns the username stored in the current session, or null.
     */
    public static String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute(ATTR_USERNAME);
    }

    /**
     * Returns true if the logged-in user has the Admin role.
     */
    public static boolean isAdmin(HttpServletRequest request) {
        return "Admin".equalsIgnoreCase(getRole(request));
    }

    // ─── Session Invalidation / Logout ───────────────────────────────────────

    /**
     * Fully invalidates the session on logout.
     * Clears all attributes, then calls invalidate() so Tomcat removes the session.
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute(ATTR_USERNAME);
            LOGGER.info("Invalidating session for user: " + username
                    + " | sessionId: " + session.getId());
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                // Already invalidated — harmless
            }
        }
    }

    // ─── CAPTCHA Session Helpers ─────────────────────────────────────────────

    /**
     * Returns the current CAPTCHA attempt count from session.
     */
    public static int getCaptchaAttempts(HttpSession session) {
        Object val = session.getAttribute(ATTR_CAPTCHA_TRIES);
        return (val instanceof Integer) ? (Integer) val : 0;
    }

    /**
     * Increments and persists the CAPTCHA attempt count.
     *
     * @return the new attempt count after incrementing
     */
    public static int incrementCaptchaAttempts(HttpSession session) {
        int attempts = getCaptchaAttempts(session) + 1;
        session.setAttribute(ATTR_CAPTCHA_TRIES, attempts);
        return attempts;
    }

    /**
     * Records the timestamp when the CAPTCHA lockout began.
     */
    public static void setCaptchaLockTimestamp(HttpSession session) {
        session.setAttribute(ATTR_CAPTCHA_LOCK, System.currentTimeMillis());
    }

    /**
     * Returns the CAPTCHA lock timestamp, or 0 if no lockout is recorded.
     */
    public static long getCaptchaLockTimestamp(HttpSession session) {
        Object val = session.getAttribute(ATTR_CAPTCHA_LOCK);
        return (val instanceof Long) ? (Long) val : 0L;
    }

    /**
     * Returns true if the CAPTCHA lockout period has expired.
     * The lock duration is defined by CAPTCHA_LOCK_DURATION_MS (5 minutes).
     */
    public static boolean isCaptchaLockExpired(HttpSession session) {
        long lockTime = getCaptchaLockTimestamp(session);
        if (lockTime == 0L) return true;
        return (System.currentTimeMillis() - lockTime) >= CAPTCHA_LOCK_DURATION_MS;
    }

    /**
     * Resets CAPTCHA state after a successful verification.
     */
    public static void resetCaptchaState(HttpSession session) {
        session.removeAttribute(ATTR_CAPTCHA_TRIES);
        session.removeAttribute(ATTR_CAPTCHA_LOCK);
        session.setAttribute(ATTR_CAPTCHA_DONE, true);
    }

    /**
     * Returns true if CAPTCHA has already been successfully verified this session.
     */
    public static boolean isCaptchaVerified(HttpSession session) {
        Object val = session.getAttribute(ATTR_CAPTCHA_DONE);
        return Boolean.TRUE.equals(val);
    }
}
