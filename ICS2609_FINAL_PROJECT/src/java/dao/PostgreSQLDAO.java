package dao;

import java.sql.*;
import javax.servlet.ServletContext;

public class PostgreSQLDAO {
    private String url, user, pass, driver;

    public PostgreSQLDAO(ServletContext context) {
        this.driver = context.getInitParameter("Postgres_Driver");
        this.url = context.getInitParameter("Postgres_URL");
        this.user = context.getInitParameter("Postgres_User");
        this.pass = context.getInitParameter("Postgres_Pass");
    }

    private Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, pass);
    }

    public void recordLog(String username, String activity, String module) {
        String sql = "INSERT INTO ACTIVITY_LOGS (username, activity, sourceModule) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, activity);
            pstmt.setString(3, module);
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}