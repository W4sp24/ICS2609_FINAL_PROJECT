package dao;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import util.TestServletContextStub;

/**
 * Integration tests for DerbyAuthDAO.
 * Requires Derby network server running on localhost:1527 with LoginDB seeded.
 * Derby seed: 15 Admin + 35 Guest = 50 total users (no SysAdmin role).
 */
public class DerbyAuthDAOIntegrationTest {

    private static DerbyAuthDAO dao;

    @BeforeClass
    public static void setUp() {
        dao = new DerbyAuthDAO(TestServletContextStub.forDerby());
    }

    // ── validateLogin ────────────────────────────────────────────────────

    // TC-AUTH-001: valid Admin login returns "Admin" role
    @Test
    public void validateLogin_Admin_returnsAdminRole() {
        String role = dao.validateLogin("sys.admin1@school.edu", "12345678");
        assertEquals("Admin", role);
    }

    // TC-AUTH-002: valid Teacher login returns "Admin" role (teachers are Admin in Derby)
    @Test
    public void validateLogin_Teacher_returnsAdminRole() {
        String role = dao.validateLogin("prof.turing@school.edu", "12345678");
        assertEquals("Admin", role);
    }

    // TC-AUTH-002: valid Guest (student) login returns "Guest" role
    @Test
    public void validateLogin_Student_returnsGuestRole() {
        String role = dao.validateLogin("student01@mail.com", "12345678");
        assertEquals("Guest", role);
    }

    // TC-AUTH-003: wrong password returns null
    @Test
    public void validateLogin_WrongPassword_returnsNull() {
        String role = dao.validateLogin("sys.admin1@school.edu", "wrongpassword");
        assertNull("Wrong password must return null", role);
    }

    // TC-AUTH-003: unknown user returns null
    @Test
    public void validateLogin_UnknownUser_returnsNull() {
        String role = dao.validateLogin("nobody@unknown.com", "12345678");
        assertNull("Unknown user must return null", role);
    }

    @Test
    public void validateLogin_BlankPassword_returnsNull() {
        try {
            String role = dao.validateLogin("sys.admin1@school.edu", "");
            assertNull(role);
        } catch (RuntimeException e) {
            // acceptable — hashPassword("") throws IllegalArgumentException
        }
    }

    // ── getAllUsers ───────────────────────────────────────────────────────

    // TC-REP-005/TC-REP-007: at least 50 users required for pagination testing
    @Test
    public void getAllUsers_returnsAtLeast50ForPagination() {
        List<String[]> users = dao.getAllUsers();
        assertNotNull(users);
        assertTrue("Must have at least 50 users to test pagination (FR-REP-007)",
            users.size() >= 50);
    }

    @Test
    public void getAllUsers_returnsExactly50() {
        List<String[]> users = dao.getAllUsers();
        assertEquals("Expected exactly 50 users (15 Admin + 35 Guest)", 50, users.size());
    }

    @Test
    public void getAllUsers_containsAdminRole() {
        List<String[]> users = dao.getAllUsers();
        boolean found = false;
        for (String[] u : users) {
            if ("Admin".equals(u[1])) { found = true; break; }
        }
        assertTrue("At least one Admin must exist", found);
    }

    @Test
    public void getAllUsers_containsGuestRole() {
        List<String[]> users = dao.getAllUsers();
        boolean found = false;
        for (String[] u : users) {
            if ("Guest".equals(u[1])) { found = true; break; }
        }
        assertTrue("At least one Guest must exist", found);
    }

    // TC-REP-002: no password field in user records (only username, role, date)
    @Test
    public void getAllUsers_eachRowHasThreeFieldsNoPassword() {
        List<String[]> users = dao.getAllUsers();
        for (String[] u : users) {
            assertEquals("Each row must have exactly [username, role, createdDate]", 3, u.length);
            assertNotNull("Username must not be null", u[0]);
            assertNotNull("Role must not be null", u[1]);
            assertNotNull("createdDate must not be null", u[2]);
            // TC-REP-002: password must never appear in any field
            for (String field : u) {
                assertFalse("Password hash must never appear in user record",
                    field != null && field.length() == 64 && field.matches("[0-9a-f]+"));
            }
        }
    }

    // TC-REP-003: only valid roles (Admin / Guest) are present
    @Test
    public void getAllUsers_onlyAdminAndGuestRoles() {
        List<String[]> users = dao.getAllUsers();
        for (String[] u : users) {
            String role = u[1];
            assertTrue("Role must be 'Admin' or 'Guest', found: " + role,
                "Admin".equals(role) || "Guest".equals(role));
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
    public void getAllUsers_knownAdminEmailPresent() {
        List<String[]> users = dao.getAllUsers();
        boolean found = false;
        for (String[] u : users) {
            if ("sys.admin1@school.edu".equals(u[0]) && "Admin".equals(u[1])) {
                found = true;
                break;
            }
        }
        assertTrue("sys.admin1@school.edu must be present with Admin role", found);
    }

    // TC-DBMS-001: authentication DB connection is live
    @Test
    public void derbyConnection_isLive() {
        List<String[]> users = dao.getAllUsers();
        assertNotNull("Derby connection must be live and return users", users);
        assertFalse("Derby must return at least one user", users.isEmpty());
    }
}
