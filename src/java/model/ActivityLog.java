package model;

import java.sql.Timestamp;

/**
 * Model representing a system activity log entry.
 * Stored in PostgreSQL (DBMS 3) for audit and tracing purposes.
 *
 * SQL Table Schema (PostgreSQL):
 * --------------------------------------------------
 * CREATE TABLE activity_logs (
 *     log_id       SERIAL PRIMARY KEY,
 *     username     VARCHAR(100),
 *     activity     VARCHAR(255) NOT NULL,
 *     ip_address   VARCHAR(45),
 *     user_role    VARCHAR(50),
 *     module       VARCHAR(100),
 *     activity_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * --------------------------------------------------
 */
public class ActivityLog {

    private int logId;
    private String username;    // May be null for unauthenticated attempts
    private String activity;    // e.g. "LOGIN_SUCCESS", "LOGIN_FAIL", "LOGOUT", "CAPTCHA_FAIL"
    private String ipAddress;
    private String userRole;    // "Admin", "Guest", or "UNKNOWN"
    private String module;      // e.g. "LoginServlet", "CaptchaServlet", "AuthFilter"
    private Timestamp activityTime;

    public ActivityLog() {}

    public ActivityLog(String username, String activity, String ipAddress,
                       String userRole, String module) {
        this.username     = username;
        this.activity     = activity;
        this.ipAddress    = ipAddress;
        this.userRole     = userRole;
        this.module       = module;
        this.activityTime = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getLogId()                          { return logId; }
    public void setLogId(int logId)                { this.logId = logId; }

    public String getUsername()                    { return username; }
    public void setUsername(String username)       { this.username = username; }

    public String getActivity()                    { return activity; }
    public void setActivity(String activity)       { this.activity = activity; }

    public String getIpAddress()                   { return ipAddress; }
    public void setIpAddress(String ipAddress)     { this.ipAddress = ipAddress; }

    public String getUserRole()                    { return userRole; }
    public void setUserRole(String userRole)       { this.userRole = userRole; }

    public String getModule()                      { return module; }
    public void setModule(String module)           { this.module = module; }

    public Timestamp getActivityTime()                     { return activityTime; }
    public void setActivityTime(Timestamp activityTime)    { this.activityTime = activityTime; }

    @Override
    public String toString() {
        return "ActivityLog{logId=" + logId + ", username=" + username
                + ", activity=" + activity + ", ip=" + ipAddress
                + ", role=" + userRole + ", module=" + module
                + ", time=" + activityTime + "}";
    }
}
