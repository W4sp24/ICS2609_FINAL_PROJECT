package dao;

import java.sql.*;
import javax.servlet.ServletContext;
import model.ActivityLog;

public class PostgresQLDAO {
    private String driver, url, user, pass;

    public PostgresQLDAO(ServletContext context) {
        this.driver = context.getInitParameter("Postgres_Driver");
        this.url = context.getInitParameter("Postgres_URL");
        this.user = context.getInitParameter("Postgres_User");
        this.pass = context.getInitParameter("Postgres_Pass");
    }

    public void insertLog(ActivityLog log) {
        String sql = "INSERT INTO ACTIVITY_LOGS (username, action, log_timestamp) VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, log.getUsername()); 
            ps.setString(2, log.getAction());  
            
            ps.executeUpdate();
            System.out.println("Log entry recorded for: " + log.getUsername());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}