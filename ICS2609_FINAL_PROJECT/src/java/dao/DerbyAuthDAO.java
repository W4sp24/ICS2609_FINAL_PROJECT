/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import util.SecurityUtil;

public class DerbyAuthDAO extends BaseDAO {
    private static final Logger LOGGER = Logger.getLogger(DerbyAuthDAO.class.getName());

    private final String driver, url, user, pass;

    public DerbyAuthDAO(ServletContext context) {
        this.driver = context.getInitParameter("Derby_Driver");
        this.url = context.getInitParameter("Derby_URL");
        this.user = context.getInitParameter("Derby_User");
        this.pass = context.getInitParameter("Derby_Pass");
    }

    protected Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, pass);
    }

    public String validateLogin(String username, String password) {
        String role = null;
        String hashedPassword = SecurityUtil.hashPassword(password);
        String query = "SELECT role FROM USERS WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    role = rs.getString("role");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "validateLogin failed for user " + username, e);
        }
        return role;
    }

    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<String[]>();
        String sql = "SELECT username, role, createdDate FROM USERS ORDER BY role, username";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new String[]{
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("createdDate")
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAllUsers failed", e);
        }
        return users;
    }

    public void addUser(String username, String plainPassword, String role) throws SQLException {
        String hashed  = SecurityUtil.hashPassword(plainPassword);
        String created = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String sql = "INSERT INTO USERS (username, password, role, createdDate) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, hashed);
            ps.setString(3, role);
            ps.setString(4, created);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addUser failed for " + username, e);
            throw new SQLException("addUser failed: " + e.getMessage(), e);
        }
    }

    public void updateUser(String username, String newPassword, String newRole) throws SQLException {
        boolean changePass = (newPassword != null && !newPassword.trim().isEmpty());
        String sql = changePass
                ? "UPDATE USERS SET password = ?, role = ? WHERE username = ?"
                : "UPDATE USERS SET role = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (changePass) {
                ps.setString(1, SecurityUtil.hashPassword(newPassword.trim()));
                ps.setString(2, newRole);
                ps.setString(3, username);
            } else {
                ps.setString(1, newRole);
                ps.setString(2, username);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateUser failed for " + username, e);
            throw new SQLException("updateUser failed: " + e.getMessage(), e);
        }
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM USERS WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "deleteUser failed for " + username, e);
            throw new SQLException("deleteUser failed: " + e.getMessage(), e);
        }
    }
}