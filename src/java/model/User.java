package model;

import java.sql.Timestamp;

/**
 * Model class representing a system user.
 * Stores authentication and role information retrieved from Apache Derby.
 */
public class User {

    private int userId;
    private String username;
    private String passwordHash; // BCrypt hash — NEVER expose in reports or logs
    private String role;         // "Admin" or "Guest"
    private Timestamp createdDate;

    public User() {}

    public User(int userId, String username, String role, Timestamp createdDate) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }

    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }

    /** Returns BCrypt hash. Never print or log this value. */
    public String getPasswordHash()             { return passwordHash; }
    public void setPasswordHash(String h)       { this.passwordHash = h; }

    public String getRole()                     { return role; }
    public void setRole(String role)            { this.role = role; }

    public Timestamp getCreatedDate()                   { return createdDate; }
    public void setCreatedDate(Timestamp createdDate)   { this.createdDate = createdDate; }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        // Password intentionally omitted
        return "User{userId=" + userId + ", username=" + username + ", role=" + role + "}";
    }
}
