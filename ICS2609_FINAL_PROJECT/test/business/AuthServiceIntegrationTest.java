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
 *   4. Returns User with appRole = Derby role (overwritten by user.setAppRole(role))
 */
public class AuthServiceIntegrationTest {

    private static AuthService authService;

    @BeforeClass
    public static void setUp() {
        TestServletContextStub ctx = TestServletContextStub.forAll();
        DerbyAuthDAO   derbyDAO = new DerbyAuthDAO(ctx);
        PostgresQLDAO  logDAO   = new PostgresQLDAO(ctx);
        MySqlBusinessDAO mysqlDAO = new MySqlBusinessDAO(ctx);
        authService = new AuthService(derbyDAO, logDAO, mysqlDAO);
    }

    // ── Successful logins ─────────────────────────────────────────────────

    @Test
    public void login_SysAdmin_returnsUserWithSysAdminRole() {
        User u = authService.login("sys.admin1@school.edu", "12345678", "127.0.0.1");
        assertNotNull("SysAdmin login must succeed", u);
        assertEquals("Derby role must be SysAdmin", "SysAdmin", u.getAppRole());
        assertEquals("Email must match", "sys.admin1@school.edu", u.getEmail());
    }

    @Test
    public void login_Teacher_returnsUserWithAdminRole() {
        User u = authService.login("prof.turing@school.edu", "12345678", "127.0.0.1");
        assertNotNull("Teacher login must succeed", u);
        assertEquals("Derby role must be Admin (for teachers)", "Admin", u.getAppRole());
    }

    @Test
    public void login_Student_returnsUserWithGuestRole() {
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

    // ── Failed logins ─────────────────────────────────────────────────────

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

    // ── Login does not throw on edge-case inputs ───────────────────────────

    @Test
    public void login_sqlInjectionAttempt_doesNotThrow() {
        // Parameterized queries must prevent this from causing SQL errors
        User u = authService.login("'; DROP TABLE USERS; --", "pass", "127.0.0.1");
        assertNull("SQL injection attempt must return null, not throw", u);
    }

    @Test
    public void login_veryLongInput_doesNotThrow() {
        String longStr = new String(new char[500]).replace('\0', 'a');
        User u = authService.login(longStr, longStr, "127.0.0.1");
        assertNull(u);
    }
}
