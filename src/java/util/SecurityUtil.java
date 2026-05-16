package util;

import java.security.MessageDigest;
import java.util.logging.Logger;

public class SecurityUtil {

    private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class.getName());

    private SecurityUtil() {}

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.length() == 0) {
            throw new IllegalArgumentException("Password required");
        }
        return sha256(plainPassword);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;

        return sha256(plainPassword).equals(hashedPassword);
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes("UTF-8"));

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xff & bytes[i]);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Hash failed", e);
        }
    }

    public static String sanitizeHtml(String input) {
        if (input == null) return "";

        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    public static String sanitizeUsername(String username) {
        if (username == null) return "";

        return username.trim().replaceAll("[^a-zA-Z0-9_.\\-@]", "");
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String getClientIp(javax.servlet.http.HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() > 0) {
            return ip.trim();
        }

        return request.getRemoteAddr();
    }
}