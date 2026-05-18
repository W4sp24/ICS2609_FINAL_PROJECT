package model;

import java.sql.Timestamp;

public class ActivityLog {

    private int logId;
    private String username;
    private String activity;
    private String ipAddress;
    private String userRole;
    private String module;
    private Timestamp activityTime;

    public ActivityLog() {
    }

    public ActivityLog(String username, String activity, String ipAddress,
            String userRole, String module) {
        this.username = username;
        this.activity = activity;
        this.ipAddress = ipAddress;
        this.userRole = userRole;
        this.module = module;
        this.activityTime = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getAction() {
        return activity;
    }  

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getRole() {
        return userRole;
    }  
    
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSource() {
        return module;
    }   

    public Timestamp getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Timestamp activityTime) {
        this.activityTime = activityTime;
    }

    @Override
    public String toString() {
        return "ActivityLog{logId=" + logId + ", username=" + username
                + ", activity=" + activity + ", ip=" + ipAddress
                + ", role=" + userRole + ", module=" + module
                + ", time=" + activityTime + "}";
    }
}
