package dao;

import model.User;
import util.SecurityUtil;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DerbyAuthDAO {

    private static final Logger LOGGER = Logger.getLogger(DerbyAuthDAO.class.getName());

    private static final String JNDI_NAME = "java:comp/env/jdbc/DerbyAuth";

    private Connection getConnection() throws SQLException {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(JNDI_NAME);
            return ds.getConnection();
        } catch (NamingException e) {
            throw new SQLException("JNDI lookup failed for: " + JNDI_NAME, e);
        }
    }

    public User authenticate(String username, String plainPassword) {
        String sanitized = SecurityUtil.sanitizeUsername(username);
        if (SecurityUtil.isBlank(sanitized) || SecurityUtil.isBlank(plainPassword)) {
            return null;
        }

        String sql = "SELECT user_id, username, password_hash, role, created_date "
                   + "FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sanitized);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");

                    if (SecurityUtil.verifyPassword(plainPassword, storedHash)) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setRole(rs.getString("role"));
                        user.setCreatedDate(rs.getTimestamp("created_date"));
                        return user;
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.severe("DerbyAuthDAO.authenticate() SQL error: " + e.getMessage());
        }

        return null; 
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, role, created_date FROM users ORDER BY user_id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setCreatedDate(rs.getTimestamp("created_date"));
                users.add(u);
            }

        } catch (SQLException e) {
            LOGGER.severe("DerbyAuthDAO.getAllUsers() SQL error: " + e.getMessage());
        }

        return users;
    }

    public User getUserByUsername(String username) {
        String sanitized = SecurityUtil.sanitizeUsername(username);
        if (SecurityUtil.isBlank(sanitized)) return null;

        String sql = "SELECT user_id, username, role, created_date "
                   + "FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sanitized);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(rs.getString("role"));
                    u.setCreatedDate(rs.getTimestamp("created_date"));
                    return u;
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("DerbyAuthDAO.getUserByUsername() SQL error: " + e.getMessage());
        }

        return null;
    }

    public List<User> getUsersByDateRange(Timestamp startDate, Timestamp endDate) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, role, created_date FROM users "
                   + "WHERE created_date BETWEEN ? AND ? ORDER BY created_date";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(rs.getString("role"));
                    u.setCreatedDate(rs.getTimestamp("created_date"));
                    users.add(u);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("DerbyAuthDAO.getUsersByDateRange() SQL error: " + e.getMessage());
        }

        return users;
    }

    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.severe("DerbyAuthDAO.countUsers() SQL error: " + e.getMessage());
        }
        return 0;
    }

    public boolean insertUser(String username, String plainPassword, String role) {
        if (SecurityUtil.isBlank(username) || SecurityUtil.isBlank(plainPassword)) {
            return false;
        }
        String hash = SecurityUtil.hashPassword(plainPassword);
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, hash);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.severe("DerbyAuthDAO.insertUser() SQL error: " + e.getMessage());
            return false;
        }
    }
}
