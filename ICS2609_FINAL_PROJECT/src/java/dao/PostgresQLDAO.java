package dao;

import java.sql.*;
import javax.servlet.ServletContext;
import model.ActivityLog;

public class PostgresQLDAO extends BaseDAO {
    private final String driver, url, user, pass;

    public PostgresQLDAO(ServletContext context) {
        this.driver = context.getInitParameter("Postgres_Driver");
        this.url    = context.getInitParameter("Postgres_URL");
        this.user   = context.getInitParameter("Postgres_User");
        this.pass   = context.getInitParameter("Postgres_Pass");
    }

    protected Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, pass);
    }

    public void log(String username, String action, String ipAddress, String role, String source) {
        String sql = "INSERT INTO activity_logs (username, action, ip_address, role, source, log_timestamp) "
                   + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, action);
            ps.setString(3, ipAddress);
            ps.setString(4, role);
            ps.setString(5, source);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertLog(ActivityLog log) {
        this.log(log.getUsername(), log.getAction(), log.getIpAddress(), log.getRole(), log.getSource());
    }
}