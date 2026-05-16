package dao;

import model.ActivityLog;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

    public void insertLog(ActivityLog log) {
        String sql = "INSERT INTO activity_logs (username, activity, ip_address, user_role, module, activity_time) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getUsername());
            ps.setString(2, log.getActivity());
            ps.setString(3, log.getIpAddress());
            ps.setString(4, log.getUserRole());
            ps.setString(5, log.getModule());
            ps.setTimestamp(6, log.getActivityTime() != null
                    ? log.getActivityTime()
                    : new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.severe("PostgresQLDAO.insertLog() failed: " + e.getMessage()
                         + " | Log entry: " + log);
        }
    }

    public void log(String username, String activity, String ipAddress,
                    String userRole, String module) {
        ActivityLog log = new ActivityLog(username, activity, ipAddress, userRole, module);
        insertLog(log);
    }
    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT log_id, username, activity, ip_address, user_role, module, activity_time "
                   + "FROM activity_logs ORDER BY activity_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                logs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("PostgresQLDAO.getAllLogs() SQL error: " + e.getMessage());
        }

        return logs;
    }

    public List<ActivityLog> getLogsByDateRange(Timestamp startDate, Timestamp endDate) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT log_id, username, activity, ip_address, user_role, module, activity_time "
                   + "FROM activity_logs "
                   + "WHERE activity_time BETWEEN ? AND ? "
                   + "ORDER BY activity_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("PostgresQLDAO.getLogsByDateRange() SQL error: " + e.getMessage());
        }

        return logs;
    }

    public List<ActivityLog> getLogsByUsername(String username) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT log_id, username, activity, ip_address, user_role, module, activity_time "
                   + "FROM activity_logs WHERE username = ? ORDER BY activity_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("PostgresQLDAO.getLogsByUsername() SQL error: " + e.getMessage());
        }

        return logs;
    }

    public int countReportsToday() {
        String sql = "SELECT COUNT(*) FROM activity_logs "
                   + "WHERE activity = 'REPORT_GENERATED' "
                   + "AND activity_time >= CURRENT_DATE";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.severe("PostgresQLDAO.countReportsToday() SQL error: " + e.getMessage());
        }

        return 0;
    }


    private ActivityLog mapRow(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setLogId(rs.getInt("log_id"));
        log.setUsername(rs.getString("username"));
        log.setActivity(rs.getString("activity"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setUserRole(rs.getString("user_role"));
        log.setModule(rs.getString("module"));
        log.setActivityTime(rs.getTimestamp("activity_time"));
        return log;
    }
}
