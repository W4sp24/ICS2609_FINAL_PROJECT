package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

public final class TraceLogger {

    private static final String[] APP_PACKAGES = {
        "controller", "dao", "business", "util", "filters", "listeners"
    };

    private static FileHandler fileHandler;

    private TraceLogger() {}

    public static void init(ServletContext ctx) {
        try {
            String logDirPath  = null;
            String webRootPath = ctx.getRealPath("/");
            if (webRootPath != null) {
                File parent1 = new File(webRootPath).getParentFile();
                File parent2 = (parent1 != null) ? parent1.getParentFile() : null;
                File cand1   = (parent1 != null) ? new File(parent1, "logs") : null;
                File cand2   = (parent2 != null) ? new File(parent2, "logs") : null;

                if      (cand1 != null && cand1.exists()) { logDirPath = cand1.getAbsolutePath(); }
                else if (cand2 != null && cand2.exists()) { logDirPath = cand2.getAbsolutePath(); }
                else if (cand1 != null && cand1.mkdirs()) { logDirPath = cand1.getAbsolutePath(); }
            }
            if (logDirPath == null) {
                logDirPath = System.getProperty("user.home") + File.separator + "ics2609_logs";
            }
            File logDir = new File(logDirPath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            String date    = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String logFile = logDirPath + File.separator + "log_" + date + ".log";

            FileHandler fh = new FileHandler(logFile, true);
            fh.setLevel(Level.ALL);
            fh.setFormatter(new TraceFormatter());

            fileHandler = fh;
            for (String pkg : APP_PACKAGES) {
                Logger pkgLogger = Logger.getLogger(pkg);
                pkgLogger.addHandler(fileHandler);
                pkgLogger.setLevel(Level.ALL);
            }

            Logger.getLogger(TraceLogger.class.getName())
                  .info("Trace log initialised: " + logFile);

        } catch (IOException e) {
            Logger.getLogger(TraceLogger.class.getName())
                  .log(Level.WARNING, "Trace log init failed — falling back to server log", e);
        }
    }

    public static void shutdown() {
        if (fileHandler == null) return;
        for (String pkg : APP_PACKAGES) {
            Logger.getLogger(pkg).removeHandler(fileHandler);
        }
        fileHandler.close();
        fileHandler = null;
    }

    private static final class TraceFormatter extends Formatter {
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(sdf.format(new Date(record.getMillis()))).append("] ");
            sb.append("[").append(record.getLevel().getName()).append("] ");
            sb.append(record.getLoggerName()).append(" - ");
            sb.append(formatMessage(record));
            if (record.getThrown() != null) {
                sb.append(System.lineSeparator());
                StringWriter sw = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(sw));
                sb.append(sw);
            }
            sb.append(System.lineSeparator());
            return sb.toString();
        }
    }
}
