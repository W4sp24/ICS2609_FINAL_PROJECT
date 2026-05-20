package controller;

import business.AuthService;
import listeners.AppContextListener;
import util.CaptchaUtil;
import util.SecurityUtil;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

public class CaptchaServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CaptchaServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AuthService authService = (AuthService) getServletContext()
                .getAttribute(AppContextListener.AUTH_SERVICE_KEY);

        String ipAddress  = SecurityUtil.getClientIp(request);
        String secretKey  = getServletContext().getInitParameter("recaptchaSecretKey");
        String recaptchaToken = request.getParameter("g-recaptcha-response");

        HttpSession session = request.getSession(true);

        int attempts = SessionUtil.getCaptchaAttempts(session);

        if (attempts >= SessionUtil.MAX_CAPTCHA_ATTEMPTS) {
            if (SessionUtil.isCaptchaLockExpired(session)) {
                LOGGER.info("CAPTCHA lock expired for IP: " + ipAddress + " — resetting.");
                session.removeAttribute(SessionUtil.ATTR_CAPTCHA_TRIES);
                session.removeAttribute(SessionUtil.ATTR_CAPTCHA_LOCK);
                attempts = 0;
            } else {
                long lockTime   = SessionUtil.getCaptchaLockTimestamp(session);
                long elapsed    = System.currentTimeMillis() - lockTime;
                long remaining  = (SessionUtil.CAPTCHA_LOCK_DURATION_MS - elapsed) / 1000;

                LOGGER.warning("CAPTCHA locked IP: " + ipAddress
                        + " | remaining wait: " + remaining + "s");

                request.setAttribute("remainingSeconds", remaining);
                request.setAttribute("errorMessage",
                        "Too many failed CAPTCHA attempts. Please wait "
                        + remaining + " second(s) before trying again.");
                request.getRequestDispatcher("/errors/captcha_error.jsp")
                        .forward(request, response);
                return;
            }
        }

        if (SecurityUtil.isBlank(recaptchaToken)) {
            handleCaptchaFailure(request, response, session, authService, ipAddress,
                    "Please complete the CAPTCHA before logging in.");
            return;
        }

        boolean captchaOk = CaptchaUtil.verify(secretKey, recaptchaToken);

        if (!captchaOk) {
            handleCaptchaFailure(request, response, session, authService, ipAddress,
                    "CAPTCHA verification failed. Please try again.");
            return;
        }

        LOGGER.info("CAPTCHA verified successfully for IP: " + ipAddress);
        SessionUtil.resetCaptchaState(session);

        if (authService != null) {
            authService.logCaptchaSuccess(ipAddress);
        }

        request.getRequestDispatcher("/LoginServlet").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    private void handleCaptchaFailure(HttpServletRequest request, HttpServletResponse response,
                                       HttpSession session, AuthService authService,
                                       String ipAddress, String errorMessage)
            throws ServletException, IOException {

        int attempts = SessionUtil.incrementCaptchaAttempts(session);
        LOGGER.warning("CAPTCHA failure. Attempts: " + attempts + " | IP: " + ipAddress);

        if (authService != null) authService.logCaptchaFailure(ipAddress, attempts);

        if (attempts >= SessionUtil.MAX_CAPTCHA_ATTEMPTS) {
            SessionUtil.setCaptchaLockTimestamp(session);
            if (authService != null) authService.logCaptchaLockout(ipAddress);
            request.setAttribute("errorMessage",
                    "Maximum CAPTCHA attempts reached. Please wait 5 minutes before trying again.");
            request.getRequestDispatcher("/errors/captcha_error.jsp").forward(request, response);
            return;
        }

        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("attemptsLeft", SessionUtil.MAX_CAPTCHA_ATTEMPTS - attempts);
        request.getRequestDispatcher("/errors/captcha_failed.jsp").forward(request, response);
    }
}
