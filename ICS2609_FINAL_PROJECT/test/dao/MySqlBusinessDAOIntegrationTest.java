package dao;

import java.util.List;
import model.Course;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import util.TestServletContextStub;

/**
 * Integration tests for MySqlBusinessDAO.
 * Requires MySQL running on localhost:3306 with course_management_db seeded.
 */
public class MySqlBusinessDAOIntegrationTest {

    private static MySqlBusinessDAO dao;

    @BeforeClass
    public static void setUp() {
        dao = new MySqlBusinessDAO(TestServletContextStub.forMySQL());
    }

    // ── getUserByEmail ────────────────────────────────────────────────────

    @Test
    public void getUserByEmail_SysAdmin_returnsAdminRole() {
        User u = dao.getUserByEmail("sys.admin1@school.edu");
        assertNotNull("sys.admin1 must exist in MySQL", u);
        assertEquals("admin", u.getAppRole());
    }

    @Test
    public void getUserByEmail_Teacher_returnsTeacherRole() {
        User u = dao.getUserByEmail("prof.turing@school.edu");
        assertNotNull("prof.turing must exist in MySQL", u);
        assertEquals("teacher", u.getAppRole());
    }

    @Test
    public void getUserByEmail_Student_returnsStudentRole() {
        User u = dao.getUserByEmail("student01@mail.com");
        assertNotNull("student01 must exist in MySQL", u);
        assertEquals("student", u.getAppRole());
    }

    @Test
    public void getUserByEmail_Unknown_returnsNull() {
        User u = dao.getUserByEmail("nobody@doesnotexist.com");
        assertNull("Unknown email must return null", u);
    }

    @Test
    public void getUserByEmail_returnsPopulatedUser() {
        User u = dao.getUserByEmail("sys.admin1@school.edu");
        assertNotNull(u);
        assertNotNull("u_id must not be null", u.getU_id());
        assertNotNull("email must not be null", u.getEmail());
        assertEquals("sys.admin1@school.edu", u.getEmail());
    }

    // ── getAllStudents ─────────────────────────────────────────────────────

    @Test
    public void getAllStudents_notNull() {
        List<User> students = dao.getAllStudents();
        assertNotNull(students);
    }

    @Test
    public void getAllStudents_notEmpty() {
        List<User> students = dao.getAllStudents();
        assertFalse("Student list must not be empty", students.isEmpty());
    }

    @Test
    public void getAllStudents_allHaveStudentRole() {
        List<User> students = dao.getAllStudents();
        for (User s : students) {
            assertEquals("All returned users must have 'student' role",
                "student", s.getAppRole());
        }
    }

    // ── getAllTeachers ─────────────────────────────────────────────────────

    @Test
    public void getAllTeachers_notNull() {
        List<User> teachers = dao.getAllTeachers();
        assertNotNull(teachers);
    }

    @Test
    public void getAllTeachers_notEmpty() {
        List<User> teachers = dao.getAllTeachers();
        assertFalse("Teacher list must not be empty", teachers.isEmpty());
    }

    @Test
    public void getAllTeachers_allHaveTeacherRole() {
        List<User> teachers = dao.getAllTeachers();
        for (User t : teachers) {
            assertEquals("All returned users must have 'teacher' role",
                "teacher", t.getAppRole());
        }
    }

    // ── getAllCourses ──────────────────────────────────────────────────────

    @Test
    public void getAllCourses_notNull() {
        List<Course> courses = dao.getAllCourses();
        assertNotNull("getAllCourses must not return null", courses);
    }

    @Test
    public void getAllCourses_eachCourseHasIdAndTitle() {
        List<Course> courses = dao.getAllCourses();
        for (Course c : courses) {
            assertNotNull("Course ID must not be null", c.getC_id());
            assertNotNull("Course title must not be null", c.getTitle());
        }
    }

    // ── getCoursesByTeacher ────────────────────────────────────────────────

    @Test
    public void getCoursesByTeacher_forKnownTeacher_returnsOnlyTheirCourses() {
        User teacher = dao.getUserByEmail("prof.turing@school.edu");
        assertNotNull(teacher);
        List<Course> courses = dao.getCoursesByTeacher(teacher.getU_id());
        assertNotNull(courses);
        for (Course c : courses) {
            assertEquals("Course must belong to the queried teacher",
                teacher.getU_id(), c.getTeacher_id());
        }
    }

    @Test
    public void getCoursesByTeacher_unknownId_returnsEmpty() {
        List<Course> courses = dao.getCoursesByTeacher("nonexistent-uuid-0000");
        assertNotNull(courses);
        assertTrue("Unknown teacher ID should return empty list", courses.isEmpty());
    }
}
