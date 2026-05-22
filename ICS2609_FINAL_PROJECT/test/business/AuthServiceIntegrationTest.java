package business;

import dao.DerbyAuthDAO;
import dao.MySqlBusinessDAO;
import dao.PostgresQLDAO;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import util.TestServletContextStub;

/**
 * Integration tests for AuthService.login() — the full authentication flow.
 * Requires all three databases (Derby, MySQL, PostgreSQL) running.
 *
 * AuthService.login() sequence:
 *   1. DerbyAuthDAO.validateLogin() — verifies credentials, returns Derby role
 *   2. MySqlBusinessDAO.getUserByEmail() — fetches MySQL user + MySQL role
 *   3. PostgresQLDAO.log() — records the event
 *   4. Returns User with appRole = Derby role
 *
 * Derby roles: "Admin" | "Guest" (SysAdmin role was collapsed into Admin)
 * MySQL roles: "admin" | "teacher" | "student"
 */
public class AuthServiceIntegrationTest {

    private static AuthService authService;

    @BeforeClass
    public static void setUp() {
        TestServletContextStub ctx = TestServletContextStub.forAll();
        DerbyAuthDAO     derbyDAO = new DerbyAuthDAO(ctx);
        PostgresQLDAO    logDAO   = new PostgresQLDAO(ctx);
        MySqlBusinessDAO mysqlDAO = new MySqlBusinessDAO(ctx);
        authService = new AuthService(derbyDAO, logDAO, mysqlDAO);
    }

    // ── TC-AUTH-001: valid Admin login ────────────────────────────────────

    @Test
    public void login_Admin_returnsAdminDerbyRole() {
        User u = authService.login("sys.admin1@school.edu", "12345678", "127.0.0.1");
        assertNotNull("Admin login must succeed", u);
        assertEquals("Derby role must be Admin", "Admin", u.getAppRole());
        assertEquals("Email must match", "sys.admin1@school.edu", u.getEmail());
    }

    // TC-AUTH-001: teacher is also Admin in Derby
    @Test
    public void login_Teacher_returnsAdminDerbyRole() {
        User u = authService.login("prof.turing@school.edu", "12345678", "127.0.0.1");
        assertNotNull("Teacher login must succeed", u);
        assertEquals("Derby role for teacher must be Admin", "Admin", u.getAppRole());
    }

    // TC-AUTH-002: valid Guest (student) login
    @Test
    public void login_Student_returnsGuestDerbyRole() {
        User u = authService.login("student01@mail.com", "12345678", "127.0.0.1");
        assertNotNull("Student login must succeed", u);
        assertEquals("Derby role must be Guest", "Guest", u.getAppRole());
    }

    @Test
    public void login_successfulUser_hasMySQLUUID() {
        User u = authService.login("sys.admin1@school.edu", "12345678", "127.0.0.1");
        assertNotNull(u);
        assertNotNull("User must have a MySQL UUID (u_id)", u.getU_id());
        assertFalse("u_id must not be empty", u.getU_id().isEmpty());
    }

    // ── TC-AUTH-003: failed logins ────────────────────────────────────────

    @Test
    public void login_WrongPassword_returnsNull() {
        User u = authService.login("sys.admin1@school.edu", "wrongpassword", "127.0.0.1");
        assertNull("Wrong password must return null", u);
    }

    @Test
    public void login_UnknownUser_returnsNull() {
        User u = authService.login("nobody@unknown.com", "12345678", "127.0.0.1");
        assertNull("Unknown user must return null", u);
    }

    @Test
    public void login_BlankUsername_returnsNull() {
        User u = authService.login("", "12345678", "127.0.0.1");
        assertNull("Blank username must return null", u);
    }

    @Test
    public void login_NullUsername_returnsNull() {
        User u = authService.login(null, "12345678", "127.0.0.1");
        assertNull("Null username must return null", u);
    }

    @Test
    public void login_BlankPassword_returnsNull() {
        User u = authService.login("sys.admin1@school.edu", "", "127.0.0.1");
        assertNull("Blank password must return null", u);
    }

    @Test
    public void login_NullPassword_returnsNull() {
        User u = authService.login("sys.admin1@school.edu", null, "127.0.0.1");
        assertNull("Null password must return null", u);
    }

    // ── TC-AUTH-005: edge cases that must not throw ───────────────────────

    @Test
    public void login_sqlInjectionAttempt_doesNotThrow() {
        User u = authService.login("'; DROP TABLE USERS; --", "pass", "127.0.0.1");
        assertNull("SQL injection attempt must return null, not throw", u);
    }

    @Test
    public void login_veryLongInput_doesNotThrow() {
        String longStr = new String(new char[500]).replace('\0', 'a');
        User u = authService.login(longStr, longStr, "127.0.0.1");
        assertNull(u);
    }

    // ── TC-ROLE-001/002: role-routing assertions ─────────────────────────

    @Test
    public void login_AdminRole_isNotGuest() {
        User u = authService.login("sys.admin1@school.edu", "12345678", "127.0.0.1");
        assertNotNull(u);
        assertNotEquals("Admin user must not have Guest role", "Guest", u.getAppRole());
    }

    @Test
    public void login_GuestRole_isNotAdmin() {
        User u = authService.login("student01@mail.com", "12345678", "127.0.0.1");
        assertNotNull(u);
        assertNotEquals("Guest user must not have Admin role", "Admin", u.getAppRole());
    }

    // TC-DBMS-002: all 3 DBMS work together in the login flow
    @Test
    public void login_usesDerbyMySQLAndPostgres_noException() {
        // A successful login exercises all three: Derby auth, MySQL profile lookup, Postgres log
        User u = authService.login("sys.admin1@school.edu", "12345678", "127.0.0.1");
        assertNotNull("Full 3-DBMS login flow must succeed", u);
        assertNotNull("MySQL UUID must be populated (MySQL is live)", u.getU_id());
        // If Postgres logging fails it is silently swallowed, so just verify no exception
    }
}
