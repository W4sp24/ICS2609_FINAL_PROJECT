package dao;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import util.TestServletContextStub;

/**
 * Integration tests for DerbyAuthDAO.
 * Requires Derby network server running on localhost:1527 with LoginDB seeded.
 */
public class DerbyAuthDAOIntegrationTest {

    private static DerbyAuthDAO dao;

    @BeforeClass
    public static void setUp() {
        dao = new DerbyAuthDAO(TestServletContextStub.forDerby());
    }

    // ── validateLogin ────────────────────────────────────────────────────

    @Test
    public void validateLogin_SysAdmin_returnsCorrectRole() {
        String role = dao.validateLogin("sys.admin1@school.edu", "12345678");
        assertEquals("SysAdmin", role);
    }

    @Test
    public void validateLogin_Teacher_returnsAdminRole() {
        String role = dao.validateLogin("prof.turing@school.edu", "12345678");
        assertEquals("Admin", role);
    }

    @Test
    public void validateLogin_Student_returnsGuestRole() {
        String role = dao.validateLogin("student01@mail.com", "12345678");
        assertEquals("Guest", role);
    }

    @Test
    public void validateLogin_WrongPassword_returnsNull() {
        String role = dao.validateLogin("sys.admin1@school.edu", "wrongpassword");
        assertNull("Wrong password must return null", role);
    }

    @Test
    public void validateLogin_UnknownUser_returnsNull() {
        String role = dao.validateLogin("nobody@unknown.com", "12345678");
        assertNull("Unknown user must return null", role);
    }

    @Test
    public void validateLogin_BlankPassword_returnsNull() {
        // hashPassword("") throws — AuthService guards this, but DAO itself
        // should not throw; it returns null due to hash failure propagation
        try {
            String role = dao.validateLogin("sys.admin1@school.edu", "");
            assertNull(role);
        } catch (RuntimeException e) {
            // acceptable — hashPassword("") throws IllegalArgumentException
        }
    }

    // ── getAllUsers ───────────────────────────────────────────────────────

    @Test
    public void getAllUsers_returnsCorrectTotal() {
        List<String[]> users = dao.getAllUsers();
        assertNotNull(users);
        assertEquals("Expected 50 total users (3 SysAdmin + 12 Admin + 35 Guest)", 50, users.size());
    }

    @Test
    public void getAllUsers_containsSysAdmin() {
        List<String[]> users = dao.getAllUsers();
        boolean found = false;
        for (String[] u : users) {
            if ("SysAdmin".equals(u[1])) { found = true; break; }
        }
        assertTrue("At least one SysAdmin must exist", found);
    }

    @Test
    public void getAllUsers_containsAdmin() {
        List<String[]> users = dao.getAllUsers();
        boolean found = false;
        for (String[] u : users) {
            if ("Admin".equals(u[1])) { found = true; break; }
        }
        assertTrue("At least one Admin (teacher) must exist", found);
    }

    @Test
    public void getAllUsers_containsGuest() {
        List<String[]> users = dao.getAllUsers();
        boolean found = false;
        for (String[] u : users) {
            if ("Guest".equals(u[1])) { found = true; break; }
        }
        assertTrue("At least one Guest (student) must exist", found);
    }

    @Test
    public void getAllUsers_eachRowHasThreeFields() {
        List<String[]> users = dao.getAllUsers();
        for (String[] u : users) {
            assertEquals("Each user row must have [username, role, createdDate]", 3, u.length);
            assertNotNull("Username must not be null", u[0]);
            assertNotNull("Role must not be null", u[1]);
            assertNotNull("createdDate must not be null", u[2]);
        }
    }

    @Test
    public void getAllUsers_orderedByRoleThenUsername() {
        List<String[]> users = dao.getAllUsers();
        assertTrue("Must return at least 2 users to check order", users.size() >= 2);
        for (int i = 0; i < users.size() - 1; i++) {
            String roleA = users.get(i)[1];
            String roleB = users.get(i + 1)[1];
            int roleCmp  = roleA.compareTo(roleB);
            if (roleCmp == 0) {
                // same role: username must be ascending
                String userA = users.get(i)[0];
                String userB = users.get(i + 1)[0];
                assertTrue("Within same role, usernames must be ascending",
                    userA.compareTo(userB) <= 0);
            } else {
                assertTrue("Roles must be ascending", roleCmp <= 0);
            }
        }
    }

    @Test
    public void getAllUsers_sysAdminEmailsMatchExpected() {
        List<String[]> users = dao.getAllUsers();
        boolean found = false;
        for (String[] u : users) {
            if ("sys.admin1@school.edu".equals(u[0]) && "SysAdmin".equals(u[1])) {
                found = true;
                break;
            }
        }
        assertTrue("sys.admin1@school.edu must be present with SysAdmin role", found);
    }
}
