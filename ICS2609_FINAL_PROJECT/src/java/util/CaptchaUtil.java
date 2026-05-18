package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class CaptchaUtil {

    private static final Logger LOGGER = Logger.getLogger(CaptchaUtil.class.getName());

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 5_000;

    private CaptchaUtil() {}

    public static boolean verify(String secretKey, String recaptchaToken) {

        if (SecurityUtil.isBlank(secretKey) || SecurityUtil.isBlank(recaptchaToken)) {
            LOGGER.warning("reCAPTCHA verify called with blank secretKey or token.");
            return false;
        }

        HttpURLConnection conn = null;

        try {

            String postData =
                    "secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8.name())
                    + "&response=" + URLEncoder.encode(recaptchaToken, StandardCharsets.UTF_8.name());

            URL url = new URL(VERIFY_URL);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length()));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int statusCode = conn.getResponseCode();

            if (statusCode != 200) {
                LOGGER.warning("reCAPTCHA endpoint returned HTTP " + statusCode);
                return false;
            }

            StringBuilder response = new StringBuilder();

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                String line;

                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String jsonResponse = response.toString();

            boolean success = jsonResponse.contains("\"success\": true")
                    || jsonResponse.contains("\"success\":true");

            if (!success) {
                LOGGER.warning("reCAPTCHA verification failed. Response: " + jsonResponse);
            }

            return success;

        } catch (IOException e) {

            LOGGER.severe("reCAPTCHA network error: " + e.getMessage());
            return false;

        } catch (Exception e) {

            LOGGER.severe("reCAPTCHA unexpected error: " + e.getMessage());
            return false;

        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}