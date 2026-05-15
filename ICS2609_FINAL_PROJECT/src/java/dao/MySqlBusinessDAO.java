package dao;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletContext;
import model.Course;

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
        List<Course> courseList = new ArrayList<>();
        String query = "SELECT * FROM COURSES";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseID(rs.getInt("courseID"));
                course.setCourseName(rs.getString("courseName"));
                course.setUnits(rs.getInt("units"));
                courseList.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseList;
    }
}