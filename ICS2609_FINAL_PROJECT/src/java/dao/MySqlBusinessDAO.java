package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import model.Course;
import model.User;

public class MySqlBusinessDAO {
    private String driver, url, user, pass;

    public MySqlBusinessDAO(ServletContext context) {
        this.driver = context.getInitParameter("MySQL_Driver");
        this.url = context.getInitParameter("MySQL_URL");
        this.user = context.getInitParameter("MySQL_User");
        this.pass = context.getInitParameter("MySQL_Pass");
    }

    private Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, pass);
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setC_id(rs.getString("c_id"));
                course.setTeacher_id(rs.getString("teacher_id"));
                course.setTitle(rs.getString("title"));
                course.setDescription(rs.getString("description"));
                course.setStatus(rs.getString("status"));
                course.setCreated_at(rs.getString("created_at"));
                courses.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<User> getAllTeachers() {
        return getUsersByRole("teacher");
    }

    public List<User> getAllStudents() {
        return getUsersByRole("student");
    }

    private List<User> getUsersByRole(String targetRole) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, targetRole);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setU_id(rs.getString("u_id"));
                    u.setEmail(rs.getString("email"));
                    u.setFirstName(rs.getString("first_name"));
                    u.setLastName(rs.getString("last_name"));
                    u.setAppRole(rs.getString("role"));
                    u.setCreatedAt(rs.getString("created_at"));
                    users.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}