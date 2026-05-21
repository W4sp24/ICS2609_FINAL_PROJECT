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

public class MySqlBusinessDAO extends BaseDAO {
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
                    return mapEnrollment(rs);
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
            e.printStackTrace();
        }
        return null;
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



    // Supporting Read Methods (Teacher)

    public List<Course> getCoursesByTeacher(String teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE teacher_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setC_id(rs.getString("c_id"));
                    c.setTeacher_id(rs.getString("teacher_id"));
                    c.setTitle(rs.getString("title"));
                    c.setDescription(rs.getString("description"));
                    c.setStatus(rs.getString("status"));
                    c.setCreated_at(rs.getString("created_at"));
                    courses.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    /**
     * Fetches a single module by its primary key.
     */
    public Module getModuleById(String moduleId) {
        String sql = "SELECT * FROM modules WHERE mod_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Module m = new Module();
                    m.setMod_id(rs.getString("mod_id"));
                    m.setCourse_id(rs.getString("course_id"));
                    m.setTitle(rs.getString("title"));
                    m.setDescription(rs.getString("description"));
                    m.setOrder(rs.getInt("order"));
                    m.setCreated_at(rs.getString("created_at"));
                    return m;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns only active enrollments for a course (status = 'active').

     */
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
            e.printStackTrace();
        }
        return enrollments;
    }

    /**
     * Returns all enrollment records for a course regardless of status.
     * Use this for enrollment history views where dropped/completed records matter.
     */
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
            e.printStackTrace();
        }
        return enrollments;
    }

    /**
     * Returns a single material by its primary key.
     */
    public Material getMaterialById(String matId) {
        String sql = "SELECT * FROM materials WHERE mat_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns all submissions with status='submitted' across every assignment in a course.

     */
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return submissions;
    }

    /**
     * Returns all student submissions for a given assignment.
     */
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
            e.printStackTrace();
        }
        return submissions;
    }

    // -------------------------------------------------------------------------
    // Course Writes
    // -------------------------------------------------------------------------

    /**
     * Creates a new course owned by the given teacher. Status defaults to 'draft'.
     */
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (c_id, teacher_id, title, description, status, created_at) "
                   + "VALUES (UUID(), ?, ?, ?, 'draft', NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTeacher_id());
            ps.setString(2, course.getTitle());
            ps.setString(3, course.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the title, description, and status of an existing course.
     */
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET title=?, description=?, status=? WHERE c_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setString(3, course.getStatus());
            ps.setString(4, course.getC_id());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a course. DB CASCADE removes its modules, materials,
     */
    public boolean deleteCourse(String courseId) {
        String sql = "DELETE FROM courses WHERE c_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Changes only the status of a course (draft / published / archived).
     */
    public boolean updateCourseStatus(String courseId, String status) {
        String sql = "UPDATE courses SET status=? WHERE c_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Module Writes
    // -------------------------------------------------------------------------

    /**
     * Adds a new module to a course.
     */
    public boolean addModule(Module module) {
        String sql = "INSERT INTO modules (mod_id, course_id, title, description, `order`, created_at) "
                   + "VALUES (UUID(), ?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, module.getCourse_id());
            ps.setString(2, module.getTitle());
            ps.setString(3, module.getDescription());
            ps.setInt(4, module.getOrder());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates title, description, and display order of a module.
     */
    public boolean updateModule(Module module) {
        String sql = "UPDATE modules SET title=?, description=?, `order`=? WHERE mod_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, module.getTitle());
            ps.setString(2, module.getDescription());
            ps.setInt(3, module.getOrder());
            ps.setString(4, module.getMod_id());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a module. DB CASCADE removes its materials and assignments.
     */
    public boolean deleteModule(String moduleId) {
        String sql = "DELETE FROM modules WHERE mod_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Attaches a material (video, document, or link) to a module.
     */
    public boolean addMaterial(Material material) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates title, type, URL, and display order of a material.
     */
    public boolean updateMaterial(Material material) {
        String sql = "UPDATE materials SET title=?, type=?, url=?, `order`=? WHERE mat_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getTitle());
            ps.setString(2, material.getType());
            ps.setString(3, material.getUrl());
            ps.setInt(4, material.getOrder());
            ps.setString(5, material.getMat_id());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Removes a material from a module.
     */
    public boolean deleteMaterial(String matId) {
        String sql = "DELETE FROM materials WHERE mat_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Assignment Writes
    // -------------------------------------------------------------------------

    /**
     * Creates an assignment under a module. due_date and max_score are optional;

     */
    public boolean addAssignment(Assignment assignment) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an assignment's title, instructions, due date, and max score.

     */
    public boolean updateAssignment(Assignment assignment) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes an assignment. DB CASCADE removes its submissions and grades.

     */
    public boolean deleteAssignment(String assignmentId) {
        String sql = "DELETE FROM assignments WHERE a_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Enrollment Writes
    // -------------------------------------------------------------------------

    /**
     * Enrolls a student in a course for the first time (status = 'active').

     */
    public boolean enrollStudent(String courseId, String studentId) {
        String sql = "INSERT INTO enrollments (e_id, course_id, student_id, status, enrolled_at) "
                   + "VALUES (UUID(), ?, ?, 'active', NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Re-activates a previously dropped enrollment (sets status back to 'active').

     */
    public boolean reEnrollStudent(String courseId, String studentId) {
        String sql = "UPDATE enrollments SET status='active', enrolled_at=NOW() "
                   + "WHERE course_id=? AND student_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets a student's enrollment status to 'dropped'.
     */
    public boolean dropEnrollment(String courseId, String studentId) {
        String sql = "UPDATE enrollments SET status='dropped' WHERE course_id=? AND student_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Grade Writes
    // -------------------------------------------------------------------------

    /**
     * Inserts a grade and marks the submission as 'graded' in a single transaction.

     */
    public boolean addGrade(Grade grade) {
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
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the score and feedback of an existing grade.
     * Used by GradeServlet action=update.
     */
    public boolean updateGrade(Grade grade) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Additional Private Helper
    // -------------------------------------------------------------------------

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