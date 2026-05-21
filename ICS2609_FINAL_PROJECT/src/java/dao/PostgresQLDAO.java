package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public List<ActivityLog> getLogs(int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs ORDER BY log_timestamp DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapLog(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public List<ActivityLog> getLogs(Timestamp start, Timestamp end) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE log_timestamp BETWEEN ? AND ? ORDER BY log_timestamp DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapLog(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public List<ActivityLog> getLogsByUser(String username, int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE username = ? ORDER BY log_timestamp DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapLog(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    private ActivityLog mapLog(ResultSet rs) throws SQLException {
        ActivityLog entry = new ActivityLog();
        entry.setLogId(rs.getInt("log_id"));
        entry.setUsername(rs.getString("username"));
        entry.setActivity(rs.getString("action"));
        entry.setIpAddress(rs.getString("ip_address"));
        entry.setUserRole(rs.getString("role"));
        entry.setModule(rs.getString("source"));
        entry.setActivityTime(rs.getTimestamp("log_timestamp"));
        return entry;
    }
}