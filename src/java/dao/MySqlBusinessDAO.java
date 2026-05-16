package dao;

import model.Course;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MySqlBusinessDAO {

    private static final Logger LOGGER = Logger.getLogger(MySqlBusinessDAO.class.getName());

    private static final String JNDI_NAME = "java:comp/env/jdbc/MySQLBusiness";

    private Connection getConnection() throws SQLException {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(JNDI_NAME);
            return ds.getConnection();
        } catch (NamingException e) {
            throw new SQLException("JNDI lookup failed for: " + JNDI_NAME, e);
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, course_name, instructor, schedule, created_date "
                   + "FROM courses ORDER BY course_id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setInstructor(rs.getString("instructor"));
                c.setSchedule(rs.getString("schedule"));
                c.setCreatedDate(rs.getTimestamp("created_date"));
                courses.add(c);
            }
        } catch (SQLException e) {
            LOGGER.severe("MySqlBusinessDAO.getAllCourses() SQL error: " + e.getMessage());
        }

        return courses;
    }

    public List<Course> getCoursesByDateRange(Timestamp startDate, Timestamp endDate) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, course_name, instructor, schedule, created_date "
                   + "FROM courses WHERE created_date BETWEEN ? AND ? ORDER BY created_date";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setCourseId(rs.getInt("course_id"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setInstructor(rs.getString("instructor"));
                    c.setSchedule(rs.getString("schedule"));
                    c.setCreatedDate(rs.getTimestamp("created_date"));
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("MySqlBusinessDAO.getCoursesByDateRange() SQL error: " + e.getMessage());
        }

        return courses;
    }

    public int countCourses() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.severe("MySqlBusinessDAO.countCourses() SQL error: " + e.getMessage());
        }
        return 0;
    }
}
