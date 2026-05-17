package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import model.Assignment;
import model.Course;
import model.Enrollment;
import model.Grade;
import model.Material;
import model.Module;
import model.Submission;
import model.User;

public class MySqlBusinessDAO {
    private final String driver, url, user, pass;

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
                    Course course = new Course();
                    course.setC_id(rs.getString("c_id"));
                    course.setTeacher_id(rs.getString("teacher_id"));
                    course.setTitle(rs.getString("title"));
                    course.setDescription(rs.getString("description"));
                    course.setStatus(rs.getString("status"));
                    course.setCreated_at(rs.getString("created_at"));
                    courses.add(course);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course getCourseById(String courseId) {
        String sql = "SELECT * FROM courses WHERE c_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course();
                    course.setC_id(rs.getString("c_id"));
                    course.setTeacher_id(rs.getString("teacher_id"));
                    course.setTitle(rs.getString("title"));
                    course.setDescription(rs.getString("description"));
                    course.setStatus(rs.getString("status"));
                    course.setCreated_at(rs.getString("created_at"));
                    return course;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Module> getModulesByCourse(String courseId) {
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules WHERE course_id = ? ORDER BY `order` ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Module module = new Module();
                    module.setMod_id(rs.getString("mod_id"));
                    module.setCourse_id(rs.getString("course_id"));
                    module.setTitle(rs.getString("title"));
                    module.setDescription(rs.getString("description"));
                    module.setOrder(rs.getInt("order"));
                    module.setCreated_at(rs.getString("created_at"));
                    modules.add(module);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modules;
    }

    public List<Material> getMaterialsByModule(String moduleId) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE module_id = ? ORDER BY `order` ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setMat_id(rs.getString("mat_id"));
                    material.setModule_id(rs.getString("module_id"));
                    material.setTitle(rs.getString("title"));
                    material.setType(rs.getString("type"));
                    material.setUrl(rs.getString("url"));
                    material.setOrder(rs.getInt("order"));
                    material.setCreated_at(rs.getString("created_at"));
                    materials.add(material);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return materials;
    }

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return assignments;
    }

    public Assignment getAssignmentById(String assignmentId) {
        String sql = "SELECT * FROM assignments WHERE a_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAssignment(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean submitAssignment(Submission submission) {
        String sql = "INSERT INTO submissions (s_id, assignment_id, student_id, file_url, status, submitted_at) "
                   + "VALUES (UUID(), ?, ?, ?, 'submitted', NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, submission.getAssignment_id());
            ps.setString(2, submission.getStudent_id());
            ps.setString(3, submission.getFile_url());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

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
            e.printStackTrace();
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
                if (rs.next()) {
                    return mapSubmission(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
            e.printStackTrace();
        }
        return grades;
    }

    public Grade getGradeBySubmission(String submissionId) {
        String sql = "SELECT * FROM grades WHERE submission_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGrade(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Enrollment getEnrollment(String courseId, String studentId) {
        String sql = "SELECT * FROM enrollments WHERE course_id = ? AND student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setE_id(rs.getString("e_id"));
                    enrollment.setCourse_id(rs.getString("course_id"));
                    enrollment.setStudent_id(rs.getString("student_id"));
                    enrollment.setStatus(rs.getString("status"));
                    enrollment.setEnrolled_at(rs.getString("enrolled_at"));
                    return enrollment;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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