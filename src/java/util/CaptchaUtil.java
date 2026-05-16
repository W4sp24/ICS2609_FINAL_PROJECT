package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class CaptchaUtil {

    private static final Logger LOGGER = Logger.getLogger(CaptchaUtil.class.getName());
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;

    private CaptchaUtil() {}

    public static boolean verify(String secretKey, String recaptchaToken) {

        if (SecurityUtil.isBlank(secretKey) || SecurityUtil.isBlank(recaptchaToken)) {
            LOGGER.warning("Blank reCAPTCHA inputs");
            return false;
        }

        HttpURLConnection conn = null;

        try {
            String postData =
                    "secret=" + URLEncoder.encode(secretKey, "UTF-8") +
                    "&response=" + URLEncoder.encode(recaptchaToken, "UTF-8");

            URL url = new URL(VERIFY_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            if (code != 200) {
                LOGGER.warning("HTTP error: " + code);
                return false;
            }

            String response = readStream(conn.getInputStream());

            return response.contains("\"success\": true") ||
                   response.contains("\"success\":true");

        } catch (Exception e) {
            LOGGER.severe("Captcha error: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static String readStream(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        return sb.toString();
    }
}