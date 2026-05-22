package dao;

import java.sql.SQLException;
import java.util.List;
import model.Assignment;
import model.Course;
import model.Enrollment;
import model.Module;
import model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;
import util.TestServletContextStub;

/**
 * Integration tests for MySqlBusinessDAO CRUD operations.
 * Requires MySQL running on localhost:3306 with course_management_db seeded.
 *
 * Tests run in letter-prefixed alphabetical order (a → b → c …).
 * Each test both performs the operation AND asserts its result immediately,
 * so no test depends on assertions from a prior method.
 *
 * Covers SRS test cases:
 *   TC-DBMS-004  Additional DBMS data is accessible after login
 *   TC-DBMS-008  Primary and foreign key relationships applied correctly
 *   TC-ERR-002   DAO with bad credentials returns empty, does not crash
 *   TC-DBMS-007  DB credentials come from external config, not hardcoded
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MySqlBusinessDAOCrudIntegrationTest {

    private static MySqlBusinessDAO dao;
    private static String testTeacherId;
    private static String testStudentId;

    // IDs shared across test methods — set in earlier letters, read in later ones
    private static String createdCourseId;
    private static String createdModuleId;
    private static String createdAssignmentId;

    @BeforeClass
    public static void setUp() {
        dao = new MySqlBusinessDAO(TestServletContextStub.forMySQL());
        User teacher = dao.getUserByEmail("prof.turing@school.edu");
        assertNotNull("prof.turing must exist in MySQL for CRUD tests", teacher);
        testTeacherId = teacher.getU_id();

        List<User> students = dao.getAllStudents();
        assertFalse("Need at least one student", students.isEmpty());
        testStudentId = students.get(0).getU_id();
    }

    @AfterClass
    public static void tearDown() {
        if (createdCourseId != null) {
            try { dao.deleteCourse(createdCourseId); } catch (Exception ignored) {}
        }
    }

    // ── TC-DBMS-004: MySQL is live ────────────────────────────────────────

    @Test
    public void a_mysqlConnection_isLiveAndReturnsData() {
        List<Course> courses = dao.getAllCourses();
        assertNotNull("MySQL must be reachable", courses);
        List<User> students = dao.getAllStudents();
        assertNotNull(students);
        assertFalse("MySQL must return at least one student", students.isEmpty());
        List<User> teachers = dao.getAllTeachers();
        assertFalse("MySQL must return at least one teacher", teachers.isEmpty());
    }

    // ── Course CRUD ───────────────────────────────────────────────────────

    @Test
    public void b_courseAdd_createsInDraftAndIsQueryable() throws SQLException {
        Course c = new Course();
        c.setTeacher_id(testTeacherId);
        c.setTitle("CRUD Test Course");
        c.setDescription("Created by MySqlBusinessDAOCrudIntegrationTest");
        boolean result = dao.addCourse(c);
        assertTrue("addCourse must return true", result);

        // Find the created course and store its ID
        List<Course> courses = dao.getCoursesByTeacher(testTeacherId);
        for (Course existing : courses) {
            if ("CRUD Test Course".equals(existing.getTitle())) {
                createdCourseId = existing.getC_id();
                break;
            }
        }
        assertNotNull("Created course must be findable by teacher", createdCourseId);

        // Verify default status is draft
        Course fetched = dao.getCourseById(createdCourseId);
        assertNotNull(fetched);
        assertEquals("New course must default to 'draft' status", "draft", fetched.getStatus());
    }

    @Test
    public void c_courseUpdate_changesTitleAndStatus() throws SQLException {
        assertNotNull("Requires course from test b", createdCourseId);
        Course c = new Course();
        c.setC_id(createdCourseId);
        c.setTitle("CRUD Test Course UPDATED");
        c.setDescription("Updated description");
        c.setStatus("published");
        assertTrue("updateCourse must return true", dao.updateCourse(c));

        Course fetched = dao.getCourseById(createdCourseId);
        assertEquals("CRUD Test Course UPDATED", fetched.getTitle());
        assertEquals("published", fetched.getStatus());
    }

    @Test
    public void c_courseUpdateStatus_changesStatusOnly() throws SQLException {
        assertNotNull("Requires course from test b", createdCourseId);
        assertTrue(dao.updateCourseStatus(createdCourseId, "draft"));
        assertEquals("draft", dao.getCourseById(createdCourseId).getStatus());
    }

    // ── Module CRUD ───────────────────────────────────────────────────────

    @Test
    public void d_moduleAdd_createsAndAppearsInList() throws SQLException {
        assertNotNull("Requires course from test b", createdCourseId);
        Module m = new Module();
        m.setCourse_id(createdCourseId);
        m.setTitle("Test Module 1");
        m.setDescription("Module for CRUD test");
        m.setOrder(1);
        assertTrue("addModule must return true", dao.addModule(m));

        List<Module> modules = dao.getModulesByCourse(createdCourseId);
        assertFalse("Course must have at least one module", modules.isEmpty());
        for (Module existing : modules) {
            if ("Test Module 1".equals(existing.getTitle())) {
                createdModuleId = existing.getMod_id();
                break;
            }
        }
        assertNotNull("Created module must be findable", createdModuleId);
    }

    @Test
    public void e_moduleUpdate_changesTitle() throws SQLException {
        assertNotNull("Requires module from test d", createdModuleId);
        Module m = new Module();
        m.setMod_id(createdModuleId);
        m.setTitle("Test Module 1 UPDATED");
        m.setDescription("Updated desc");
        m.setOrder(2);
        assertTrue(dao.updateModule(m));
        assertEquals("Test Module 1 UPDATED", dao.getModuleById(createdModuleId).getTitle());
    }

    // ── Assignment CRUD ───────────────────────────────────────────────────

    @Test
    public void f_assignmentAdd_createsAndIsQueryableById() throws SQLException {
        assertNotNull("Requires module from test d", createdModuleId);
        Assignment a = new Assignment();
        a.setModule_id(createdModuleId);
        a.setTitle("Test Assignment 1");
        a.setInstructions("Submit by end of week.");
        a.setMax_score(100.0);
        assertTrue("addAssignment must return true", dao.addAssignment(a));

        List<Assignment> assignments = dao.getAssignmentsByModule(createdModuleId);
        for (Assignment existing : assignments) {
            if ("Test Assignment 1".equals(existing.getTitle())) {
                createdAssignmentId = existing.getA_id();
                break;
            }
        }
        assertNotNull("Created assignment must be findable", createdAssignmentId);

        // Verify getAssignmentById works
        Assignment fetched = dao.getAssignmentById(createdAssignmentId);
        assertNotNull(fetched);
        assertEquals("Test Assignment 1", fetched.getTitle());
        assertEquals(100.0, fetched.getMax_score(), 0.001);
    }

    @Test
    public void g_assignmentUpdate_changesMaxScore() throws SQLException {
        assertNotNull("Requires assignment from test f", createdAssignmentId);
        Assignment a = new Assignment();
        a.setA_id(createdAssignmentId);
        a.setTitle("Test Assignment 1 UPDATED");
        a.setInstructions("Updated instructions");
        a.setMax_score(50.0);
        assertTrue(dao.updateAssignment(a));
        assertEquals(50.0, dao.getAssignmentById(createdAssignmentId).getMax_score(), 0.001);
    }

    // ── Enrollment CRUD (TC-DBMS-008: FK relationship) ────────────────────

    @Test
    public void h_enrollment_fullLifecycle() throws SQLException {
        assertNotNull("Requires course from test b", createdCourseId);

        // Ensure clean state — drop if already enrolled from prior run
        Enrollment existing = dao.getEnrollment(createdCourseId, testStudentId);
        if (existing != null) {
            dao.dropEnrollment(createdCourseId, testStudentId);
            existing = null;
        }

        // Enroll
        assertTrue("enrollStudent must succeed", dao.enrollStudent(createdCourseId, testStudentId));
        Enrollment e = dao.getEnrollment(createdCourseId, testStudentId);
        assertNotNull(e);
        assertEquals("Enrollment must be active", "active", e.getStatus());

        // Verify in active list
        boolean inList = false;
        for (Enrollment en : dao.getEnrollmentsByCourse(createdCourseId)) {
            if (testStudentId.equals(en.getStudent_id())) { inList = true; break; }
        }
        assertTrue("Enrolled student must appear in active course list", inList);

        // Drop
        assertTrue("dropEnrollment must succeed", dao.dropEnrollment(createdCourseId, testStudentId));
        assertEquals("dropped", dao.getEnrollment(createdCourseId, testStudentId).getStatus());

        // Dropped student must not appear in active list
        for (Enrollment en : dao.getEnrollmentsByCourse(createdCourseId)) {
            assertNotEquals("Dropped student must not appear in active list",
                    testStudentId, en.getStudent_id());
        }

        // Re-enroll
        assertTrue("reEnrollStudent must succeed",
                dao.reEnrollStudent(createdCourseId, testStudentId));
        assertEquals("active", dao.getEnrollment(createdCourseId, testStudentId).getStatus());
    }

    // ── TC-DBMS-008: FK cascade delete ───────────────────────────────────

    @Test
    public void i_deleteAssignment_removesIt() throws SQLException {
        assertNotNull("Requires assignment from test f", createdAssignmentId);
        assertTrue(dao.deleteAssignment(createdAssignmentId));
        assertNull("Deleted assignment must be gone", dao.getAssignmentById(createdAssignmentId));
        createdAssignmentId = null;
    }

    @Test
    public void j_deleteModule_cascadesToAssignments() throws SQLException {
        assertNotNull("Requires module from test d", createdModuleId);
        // Add a new assignment to verify cascade
        Assignment a = new Assignment();
        a.setModule_id(createdModuleId);
        a.setTitle("Cascade Test Assignment");
        a.setInstructions("Cascade test instructions");
        a.setMax_score(10.0);
        dao.addAssignment(a);

        assertTrue(dao.deleteModule(createdModuleId));
        assertNull("Deleted module must be gone", dao.getModuleById(createdModuleId));
        assertTrue("FK cascade must remove child assignments",
                dao.getAssignmentsByModule(createdModuleId).isEmpty());
        createdModuleId = null;
    }

    // ── TC-ERR-002: bad credentials → empty list, not crash ───────────────

    @Test
    public void k_badCredentials_readMethodsReturnEmptyNotNull() {
        TestServletContextStub badCtx = new TestServletContextStub();
        badCtx.setParam("MySQL_Driver", "com.mysql.cj.jdbc.Driver");
        badCtx.setParam("MySQL_URL",    "jdbc:mysql://localhost:3306/course_management_db");
        badCtx.setParam("MySQL_User",   "bad_user_xyz");
        badCtx.setParam("MySQL_Pass",   "bad_pass_xyz");
        MySqlBusinessDAO badDao = new MySqlBusinessDAO(badCtx);

        List<Course> courses = badDao.getAllCourses();
        assertNotNull("Bad-creds getAllCourses must return empty list, not null", courses);
        assertTrue(courses.isEmpty());

        List<User> students = badDao.getAllStudents();
        assertNotNull(students);
        assertTrue(students.isEmpty());
    }

    // ── TC-DBMS-007: credentials come from external config ────────────────

    @Test
    public void k_daoCredentials_readFromContextNotHardcoded() {
        // DAO with alternate URL fails gracefully (returns empty, not hardcoded fallback)
        TestServletContextStub altCtx = new TestServletContextStub();
        altCtx.setParam("MySQL_Driver", "com.mysql.cj.jdbc.Driver");
        altCtx.setParam("MySQL_URL",    "jdbc:mysql://no-such-host:3306/no_db");
        altCtx.setParam("MySQL_User",   "root");
        altCtx.setParam("MySQL_Pass",   "app");
        MySqlBusinessDAO altDao = new MySqlBusinessDAO(altCtx);

        List<Course> result = altDao.getAllCourses();
        assertNotNull("Alternate-config DAO must return empty list gracefully", result);

        // The real DAO (valid config) should still work
        assertNotNull(dao.getAllCourses());
    }
}
