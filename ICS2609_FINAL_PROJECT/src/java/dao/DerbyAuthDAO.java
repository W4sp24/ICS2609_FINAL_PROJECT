/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.sql.*;
import javax.servlet.ServletContext;

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
        String query = "SELECT role FROM USERS WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);

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
}