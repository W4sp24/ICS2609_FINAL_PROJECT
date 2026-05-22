package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import model.Assignment;
import model.Course;
import model.Enrollment;
import model.Grade;
import model.Material;
import model.Module;
import model.Submission;
import model.User;

public class MySqlBusinessDAO extends BaseDAO {
    private static final Logger LOGGER = Logger.getLogger(MySqlBusinessDAO.class.getName());

    private final String driver, url, user, pass;

    public MySqlBusinessDAO(ServletContext context) {
        this.driver = context.getInitParameter("MySQL_Driver");
        this.url = context.getInitParameter("MySQL_URL");
        this.user = context.getInitParameter("MySQL_User");
        this.pass = context.getInitParameter("MySQL_Pass");
    }

    protected Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, pass);
    }

    // -------------------------------------------------------------------------
    // Course Reads
    // -------------------------------------------------------------------------

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                courses.add(mapCourse(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAllCourses failed", e);
        }
        return courses;
    }

    public List<Course> getEnrolledCourses(String studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c "
                   + "JOIN enrollments e ON c.c_id = e.course_id "
                   + "WHERE e.student_id = ? AND e.status = 'active'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapCourse(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getEnrolledCourses failed for student " + studentId, e);
        }
        return courses;
    }

    public Course getCourseById(String courseId) {
        String sql = "SELECT * FROM courses WHERE c_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCourse(rs);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getCourseById failed for id " + courseId, e);
        }
        return null;
    }

    public List<Course> getCoursesByTeacher(String teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE teacher_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapCourse(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getCoursesByTeacher failed for teacher " + teacherId, e);
        }
        return courses;
    }

    // -------------------------------------------------------------------------
    // Module Reads
    // -------------------------------------------------------------------------

    public List<Module> getModulesByCourse(String courseId) {
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules WHERE course_id = ? ORDER BY `order` ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modules.add(mapModule(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getModulesByCourse failed for course " + courseId, e);
        }
        return modules;
    }

    public Module getModuleById(String moduleId) {
        String sql = "SELECT * FROM modules WHERE mod_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapModule(rs);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getModuleById failed for id " + moduleId, e);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Material Reads
    // -------------------------------------------------------------------------

    public List<Material> getMaterialsByModule(String moduleId) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE module_id = ? ORDER BY `order` ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapMaterial(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getMaterialsByModule failed for module " + moduleId, e);
        }
        return materials;
    }

    public Material getMaterialById(String matId) {
        String sql = "SELECT * FROM materials WHERE mat_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapMaterial(rs);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getMaterialById failed for id " + matId, e);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Assignment Reads
    // -------------------------------------------------------------------------

    public List<Assignment> getAssignmentsByModule(String moduleId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE module_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapAssignment(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAssignmentsByModule failed for module " + moduleId, e);
        }
        return assignments;
    }

    public List<Assignment> getAssignmentsByCourse(String courseId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT a.* FROM assignments a "
                   + "JOIN modules m ON a.module_id = m.mod_id "
                   + "WHERE m.course_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapAssignment(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAssignmentsByCourse failed for course " + courseId, e);
        }
        return assignments;
    }

    public Assignment getAssignmentById(String assignmentId) {
        String sql = "SELECT * FROM assignments WHERE a_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAssignment(rs);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAssignmentById failed for id " + assignmentId, e);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Submission Reads
    // -------------------------------------------------------------------------

    public List<Submission> getSubmissionsByStudent(String studentId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapSubmission(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getSubmissionsByStudent failed for student " + studentId, e);
        }
        return submissions;
    }

    public Submission getSubmissionByAssignment(String assignmentId, String studentId) {
        String sql = "SELECT * FROM submissions WHERE assignment_id = ? AND student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentId);
            ps.setString(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSubmission(rs);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getSubmissionByAssignment failed", e);
        }
        return null;
    }

    public List<Submission> getSubmissionsByCourse(String courseId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.* FROM submissions s "
                   + "JOIN assignments a ON s.assignment_id = a.a_id "
                   + "JOIN modules m ON a.module_id = m.mod_id "
                   + "WHERE m.course_id = ? AND s.status = 'submitted'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapSubmission(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getSubmissionsByCourse failed for course " + courseId, e);
        }
        return submissions;
    }

    public List<Submission> getAllSubmissionsByCourse(String courseId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.* FROM submissions s "
                   + "JOIN assignments a ON s.assignment_id = a.a_id "
                   + "JOIN modules m ON a.module_id = m.mod_id "
                   + "WHERE m.course_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapSubmission(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAllSubmissionsByCourse failed for course " + courseId, e);
        }
        return submissions;
    }

    public List<Submission> getSubmissionsByAssignment(String assignmentId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE assignment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapSubmission(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getSubmissionsByAssignment failed for assignment " + assignmentId, e);
        }
        return submissions;
    }

    // -------------------------------------------------------------------------
    // Grade Reads
    // -------------------------------------------------------------------------

    public List<Grade> getGradesByStudent(String studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.* FROM grades g "
                   + "JOIN submissions s ON g.submission_id = s.s_id "
                   + "WHERE s.student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapGrade(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getGradesByStudent failed for student " + studentId, e);
        }
        return grades;
    }

    public Grade getGradeBySubmission(String submissionId) {
        String sql = "SELECT * FROM grades WHERE submission_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapGrade(rs);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getGradeBySubmission failed for submission " + submissionId, e);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Enrollment Reads
    // -------------------------------------------------------------------------

    public Enrollment getEnrollment(String courseId, String studentId) {
        String sql = "SELECT * FROM enrollments WHERE course_id = ? AND student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEnrollment(rs);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getEnrollment failed", e);
        }
        return null;
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ? AND status = 'active'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapEnrollment(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getEnrollmentsByCourse failed for course " + courseId, e);
        }
        return enrollments;
    }

    public List<Enrollment> getAllEnrollmentsByCourse(String courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapEnrollment(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAllEnrollmentsByCourse failed for course " + courseId, e);
        }
        return enrollments;
    }

    // -------------------------------------------------------------------------
    // User Reads
    // -------------------------------------------------------------------------

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setU_id(rs.getString("u_id"));
                    u.setEmail(rs.getString("email"));
                    u.setFirstName(rs.getString("first_name"));
                    u.setLastName(rs.getString("last_name"));
                    u.setAppRole(rs.getString("role"));
                    u.setCreatedAt(rs.getString("created_at"));
                    return u;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getUserByEmail failed for email " + email, e);
        }
        return null;
    }

    public List<User> getAllTeachers() {
        return getUsersByRole("teacher");
    }

    public List<User> getAllStudents() {
        return getUsersByRole("student");
    }

    public void addUser(String email, String firstName, String lastName, String role) throws SQLException {
        String sql = "INSERT INTO users (email, first_name, last_name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addUser failed for email " + email, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addUser unexpected error for email " + email, e);
            throw new SQLException("addUser failed", e);
        }
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
            LOGGER.log(Level.SEVERE, "getUsersByRole failed for role " + targetRole, e);
        }
        return users;
    }

    // -------------------------------------------------------------------------
    // Course Writes
    // -------------------------------------------------------------------------

    public boolean addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (c_id, teacher_id, title, description, status, created_at) "
                   + "VALUES (UUID(), ?, ?, ?, 'draft', NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTeacher_id());
            ps.setString(2, course.getTitle());
            ps.setString(3, course.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addCourse failed: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addCourse unexpected error", e);
            throw new SQLException("addCourse failed", e);
        }
    }

    public boolean updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET title=?, description=?, status=? WHERE c_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setString(3, course.getStatus());
            ps.setString(4, course.getC_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateCourse failed for id " + course.getC_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateCourse unexpected error", e);
            throw new SQLException("updateCourse failed", e);
        }
    }

    public boolean deleteCourse(String courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE c_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteCourse failed for id " + courseId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "deleteCourse unexpected error", e);
            throw new SQLException("deleteCourse failed", e);
        }
    }

    public boolean updateCourseStatus(String courseId, String status) throws SQLException {
        String sql = "UPDATE courses SET status=? WHERE c_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateCourseStatus failed for id " + courseId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateCourseStatus unexpected error", e);
            throw new SQLException("updateCourseStatus failed", e);
        }
    }

    // -------------------------------------------------------------------------
    // Module Writes
    // -------------------------------------------------------------------------

    public boolean addModule(Module module) throws SQLException {
        String sql = "INSERT INTO modules (mod_id, course_id, title, description, `order`, created_at) "
                   + "VALUES (UUID(), ?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, module.getCourse_id());
            ps.setString(2, module.getTitle());
            ps.setString(3, module.getDescription());
            ps.setInt(4, module.getOrder());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addModule failed for course " + module.getCourse_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addModule unexpected error", e);
            throw new SQLException("addModule failed", e);
        }
    }

    public boolean updateModule(Module module) throws SQLException {
        String sql = "UPDATE modules SET title=?, description=?, `order`=? WHERE mod_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, module.getTitle());
            ps.setString(2, module.getDescription());
            ps.setInt(3, module.getOrder());
            ps.setString(4, module.getMod_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateModule failed for id " + module.getMod_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateModule unexpected error", e);
            throw new SQLException("updateModule failed", e);
        }
    }

    public boolean deleteModule(String moduleId) throws SQLException {
        String sql = "DELETE FROM modules WHERE mod_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteModule failed for id " + moduleId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "deleteModule unexpected error", e);
            throw new SQLException("deleteModule failed", e);
        }
    }

    // -------------------------------------------------------------------------
    // Material Writes
    // -------------------------------------------------------------------------

    public boolean addMaterial(Material material) throws SQLException {
        String sql = "INSERT INTO materials (mat_id, module_id, title, type, url, `order`, created_at) "
                   + "VALUES (UUID(), ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getModule_id());
            ps.setString(2, material.getTitle());
            ps.setString(3, material.getType());
            ps.setString(4, material.getUrl());
            ps.setInt(5, material.getOrder());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addMaterial failed for module " + material.getModule_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addMaterial unexpected error", e);
            throw new SQLException("addMaterial failed", e);
        }
    }

    public boolean updateMaterial(Material material) throws SQLException {
        String sql = "UPDATE materials SET title=?, type=?, url=?, `order`=? WHERE mat_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getTitle());
            ps.setString(2, material.getType());
            ps.setString(3, material.getUrl());
            ps.setInt(4, material.getOrder());
            ps.setString(5, material.getMat_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateMaterial failed for id " + material.getMat_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateMaterial unexpected error", e);
            throw new SQLException("updateMaterial failed", e);
        }
    }

    public boolean deleteMaterial(String matId) throws SQLException {
        String sql = "DELETE FROM materials WHERE mat_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteMaterial failed for id " + matId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "deleteMaterial unexpected error", e);
            throw new SQLException("deleteMaterial failed", e);
        }
    }


    public boolean addAssignment(Assignment assignment) throws SQLException {
        String sql = "INSERT INTO assignments (a_id, module_id, title, instructions, due_date, max_score, created_at) "
                   + "VALUES (UUID(), ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignment.getModule_id());
            ps.setString(2, assignment.getTitle());
            ps.setString(3, assignment.getInstructions());
            if (assignment.getDue_date() == null || assignment.getDue_date().isEmpty())
                ps.setNull(4, Types.TIMESTAMP);
            else
                ps.setString(4, assignment.getDue_date());
            if (assignment.getMax_score() == 0)
                ps.setNull(5, Types.DECIMAL);
            else
                ps.setDouble(5, assignment.getMax_score());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addAssignment failed for module " + assignment.getModule_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addAssignment unexpected error", e);
            throw new SQLException("addAssignment failed", e);
        }
    }

    public boolean updateAssignment(Assignment assignment) throws SQLException {
        String sql = "UPDATE assignments SET title=?, instructions=?, due_date=?, max_score=? WHERE a_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignment.getTitle());
            ps.setString(2, assignment.getInstructions());
            if (assignment.getDue_date() == null || assignment.getDue_date().isEmpty())
                ps.setNull(3, Types.TIMESTAMP);
            else
                ps.setString(3, assignment.getDue_date());
            if (assignment.getMax_score() == 0)
                ps.setNull(4, Types.DECIMAL);
            else
                ps.setDouble(4, assignment.getMax_score());
            ps.setString(5, assignment.getA_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateAssignment failed for id " + assignment.getA_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateAssignment unexpected error", e);
            throw new SQLException("updateAssignment failed", e);
        }
    }

    public boolean deleteAssignment(String assignmentId) throws SQLException {
        String sql = "DELETE FROM assignments WHERE a_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteAssignment failed for id " + assignmentId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "deleteAssignment unexpected error", e);
            throw new SQLException("deleteAssignment failed", e);
        }
    }



    public boolean submitAssignment(Submission submission) throws SQLException {
        String sql = "INSERT INTO submissions (s_id, assignment_id, student_id, file_url, status, submitted_at) "
                   + "VALUES (UUID(), ?, ?, ?, 'submitted', NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, submission.getAssignment_id());
            ps.setString(2, submission.getStudent_id());
            ps.setString(3, submission.getFile_url());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "submitAssignment failed for assignment " + submission.getAssignment_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "submitAssignment unexpected error", e);
            throw new SQLException("submitAssignment failed", e);
        }
    }



    public boolean enrollStudent(String courseId, String studentId) throws SQLException {
        String sql = "INSERT INTO enrollments (e_id, course_id, student_id, status, enrolled_at) "
                   + "VALUES (UUID(), ?, ?, 'active', NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "enrollStudent failed for course " + courseId + " student " + studentId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "enrollStudent unexpected error", e);
            throw new SQLException("enrollStudent failed", e);
        }
    }

    public boolean reEnrollStudent(String courseId, String studentId) throws SQLException {
        String sql = "UPDATE enrollments SET status='active', enrolled_at=NOW() "
                   + "WHERE course_id=? AND student_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "reEnrollStudent failed for course " + courseId + " student " + studentId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "reEnrollStudent unexpected error", e);
            throw new SQLException("reEnrollStudent failed", e);
        }
    }

    public boolean dropEnrollment(String courseId, String studentId) throws SQLException {
        String sql = "UPDATE enrollments SET status='dropped' WHERE course_id=? AND student_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "dropEnrollment failed for course " + courseId + " student " + studentId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "dropEnrollment unexpected error", e);
            throw new SQLException("dropEnrollment failed", e);
        }
    }



    public boolean addGrade(Grade grade) throws SQLException {
        String insertGradeSQL = "INSERT INTO grades (g_id, submission_id, graded_by, score, feedback, graded_at) "
                              + "VALUES (UUID(), ?, ?, ?, ?, NOW())";
        String updateSubmissionSQL = "UPDATE submissions SET status='graded' WHERE s_id=?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(insertGradeSQL);
                 PreparedStatement ps2 = conn.prepareStatement(updateSubmissionSQL)) {
                ps1.setString(1, grade.getSubmission_id());
                ps1.setString(2, grade.getGraded_by());
                ps1.setDouble(3, grade.getScore());
                if (grade.getFeedback() == null || grade.getFeedback().isEmpty())
                    ps1.setNull(4, Types.VARCHAR);
                else
                    ps1.setString(4, grade.getFeedback());
                ps1.executeUpdate();
                ps2.setString(1, grade.getSubmission_id());
                ps2.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "addGrade failed for submission " + grade.getSubmission_id(), e);
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addGrade connection error", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addGrade unexpected error", e);
            throw new SQLException("addGrade failed", e);
        }
    }

    public boolean updateGrade(Grade grade) throws SQLException {
        String sql = "UPDATE grades SET score=?, feedback=? WHERE g_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, grade.getScore());
            if (grade.getFeedback() == null || grade.getFeedback().isEmpty())
                ps.setNull(2, Types.VARCHAR);
            else
                ps.setString(2, grade.getFeedback());
            ps.setString(3, grade.getG_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateGrade failed for id " + grade.getG_id(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateGrade unexpected error", e);
            throw new SQLException("updateGrade failed", e);
        }
    }


    
    //mga helpers
    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setC_id(rs.getString("c_id"));
        c.setTeacher_id(rs.getString("teacher_id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        c.setCreated_at(rs.getString("created_at"));
        return c;
    }

    private Module mapModule(ResultSet rs) throws SQLException {
        Module m = new Module();
        m.setMod_id(rs.getString("mod_id"));
        m.setCourse_id(rs.getString("course_id"));
        m.setTitle(rs.getString("title"));
        m.setDescription(rs.getString("description"));
        m.setOrder(rs.getInt("order"));
        m.setCreated_at(rs.getString("created_at"));
        return m;
    }

    private Material mapMaterial(ResultSet rs) throws SQLException {
        Material m = new Material();
        m.setMat_id(rs.getString("mat_id"));
        m.setModule_id(rs.getString("module_id"));
        m.setTitle(rs.getString("title"));
        m.setType(rs.getString("type"));
        m.setUrl(rs.getString("url"));
        m.setOrder(rs.getInt("order"));
        m.setCreated_at(rs.getString("created_at"));
        return m;
    }

    private Assignment mapAssignment(ResultSet rs) throws SQLException {
        Assignment a = new Assignment();
        a.setA_id(rs.getString("a_id"));
        a.setModule_id(rs.getString("module_id"));
        a.setTitle(rs.getString("title"));
        a.setInstructions(rs.getString("instructions"));
        a.setDue_date(rs.getString("due_date"));
        a.setMax_score(rs.getDouble("max_score"));
        a.setCreated_at(rs.getString("created_at"));
        return a;
    }

    private Submission mapSubmission(ResultSet rs) throws SQLException {
        Submission s = new Submission();
        s.setS_id(rs.getString("s_id"));
        s.setAssignment_id(rs.getString("assignment_id"));
        s.setStudent_id(rs.getString("student_id"));
        s.setFile_url(rs.getString("file_url"));
        s.setStatus(rs.getString("status"));
        s.setSubmitted_at(rs.getString("submitted_at"));
        return s;
    }

    private Grade mapGrade(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setG_id(rs.getString("g_id"));
        g.setSubmission_id(rs.getString("submission_id"));
        g.setGraded_by(rs.getString("graded_by"));
        g.setScore(rs.getDouble("score"));
        g.setFeedback(rs.getString("feedback"));
        g.setGraded_at(rs.getString("graded_at"));
        return g;
    }

    private Enrollment mapEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setE_id(rs.getString("e_id"));
        e.setCourse_id(rs.getString("course_id"));
        e.setStudent_id(rs.getString("student_id"));
        e.setStatus(rs.getString("status"));
        e.setEnrolled_at(rs.getString("enrolled_at"));
        return e;
    }
}
