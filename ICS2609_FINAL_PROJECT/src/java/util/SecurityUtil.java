package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class SecurityUtil {

    private static final Logger LOGGER =
            Logger.getLogger(SecurityUtil.class.getName());

    private SecurityUtil() {
    }
    public static String hashPassword(String plainPassword) {

        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password must not be null or empty.");
        }

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes =
                    md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();

            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {

            LOGGER.severe("SHA-256 algorithm not found: " + e.getMessage());

            throw new RuntimeException("Hashing error", e);
        }
    }

    public static boolean verifyPassword(String plainPassword,
                                         String hashedPassword) {

        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {

            String hashedInput = hashPassword(plainPassword);

            return hashedInput.equals(hashedPassword);

        } catch (Exception e) {

            LOGGER.warning("Password verification error: " + e.getMessage());

            return false;
        }
    }

    public static String sanitizeHtml(String input) {

        if (input == null) return "";

        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    public static String sanitizeUsername(String username) {

        if (username == null) return "";

        String trimmed = username.trim();

        return trimmed.replaceAll("[^a-zA-Z0-9_.\\-@]", "");
    }

    public static boolean isBlank(String value) {

        return value == null || value.trim().isEmpty();
    }

    public static String getClientIp(
            javax.servlet.http.HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null
                && !ip.isEmpty()
                && !"unknown".equalsIgnoreCase(ip)) {

            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");

        if (ip != null && !ip.isEmpty()) {
            return ip.trim();
        }

        return request.getRemoteAddr();
    }
}