/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import util.SecurityUtil;

public class DerbyAuthDAO extends BaseDAO {
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
            e.printStackTrace();
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
        } catch (Exception e) { e.printStackTrace(); }
        return users;
    }
}