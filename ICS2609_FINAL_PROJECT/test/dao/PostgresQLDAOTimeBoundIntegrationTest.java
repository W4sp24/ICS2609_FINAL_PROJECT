package dao;

import java.sql.Timestamp;
import java.util.List;
import model.ActivityLog;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import util.TestServletContextStub;

/**
 * Integration tests for PostgresQLDAO time-bound (date-range) queries.
 * Requires PostgreSQL running on localhost:5432 with activity_logs seeded.
 *
 * Covers SRS test cases:
 *   TC-TIME-001  Valid date range returns only records within that range
 *   TC-TIME-002  Date range with no matching records returns empty result (no crash)
 *   TC-DBMS-001  PostgreSQL (third DBMS) is reachable
 *   TC-DBMS-004  Feature connected to additional DBMS accessible after login
 */
public class PostgresQLDAOTimeBoundIntegrationTest {

    private static PostgresQLDAO dao;

    @BeforeClass
    public static void setUp() {
        dao = new PostgresQLDAO(TestServletContextStub.forPostgres());
        // Seed a test log so the date-range query has something to find
        dao.log("timeboundtest@school.edu", "TIMEBOUND_SEED",
                "127.0.0.1", "Admin", "PostgresQLDAOTimeBoundIntegrationTest");
    }

    // ── TC-DBMS-001: PostgreSQL connection is live ────────────────────────

    @Test
    public void postgresConnection_isLive() {
        List<ActivityLog> logs = dao.getLogs(1);
        assertNotNull("PostgreSQL must be reachable and return logs", logs);
    }

    // ── TC-TIME-001: valid date range returns only matching records ────────

    @Test
    public void getLogs_range_returnsNonNullList() {
        Timestamp start = new Timestamp(System.currentTimeMillis() - 86400000L); // 1 day ago
        Timestamp end   = new Timestamp(System.currentTimeMillis() + 86400000L); // 1 day ahead
        List<ActivityLog> logs = dao.getLogs(start, end);
        assertNotNull("Date-range query must return a non-null list", logs);
    }

    @Test
    public void getLogs_range_wideWindow_containsRecentSeedEntry() {
        Timestamp start = new Timestamp(System.currentTimeMillis() - 60000L); // 1 min ago
        Timestamp end   = new Timestamp(System.currentTimeMillis() + 60000L); // 1 min ahead
        List<ActivityLog> logs = dao.getLogs(start, end);
        assertNotNull(logs);
        boolean found = false;
        for (ActivityLog log : logs) {
            if ("timeboundtest@school.edu".equals(log.getUsername())
                    && "TIMEBOUND_SEED".equals(log.getAction())) {
                found = true;
                break;
            }
        }
        assertTrue("Seed entry inserted just now must appear in the current-minute range", found);
    }

    @Test
    public void getLogs_range_allResultsWithinBounds() {
        Timestamp start = new Timestamp(System.currentTimeMillis() - 7L * 86400000L); // 7 days ago
        Timestamp end   = new Timestamp(System.currentTimeMillis());
        List<ActivityLog> logs = dao.getLogs(start, end);
        assertNotNull(logs);
        for (ActivityLog log : logs) {
            if (log.getActivityTime() != null) {
                assertTrue("Log timestamp must be >= start",
                        log.getActivityTime().getTime() >= start.getTime());
                assertTrue("Log timestamp must be <= end",
                        log.getActivityTime().getTime() <= end.getTime());
            }
        }
    }

    @Test
    public void getLogs_range_populatedObjects() {
        Timestamp start = new Timestamp(System.currentTimeMillis() - 86400000L);
        Timestamp end   = new Timestamp(System.currentTimeMillis() + 86400000L);
        List<ActivityLog> logs = dao.getLogs(start, end);
        for (ActivityLog log : logs) {
            assertNotNull("Username must not be null", log.getUsername());
            assertNotNull("Action must not be null", log.getAction());
            assertNotNull("Timestamp must not be null", log.getActivityTime());
        }
    }

    // ── TC-TIME-002: range with no matching records returns empty, not null ─

    @Test
    public void getLogs_range_distantPast_returnsEmptyNotNull() {
        // Year 2000 — should have no records
        Timestamp start = new Timestamp(946684800000L); // 2000-01-01 00:00:00 UTC
        Timestamp end   = new Timestamp(946771200000L); // 2000-01-02 00:00:00 UTC
        List<ActivityLog> logs = dao.getLogs(start, end);
        assertNotNull("Empty result set must return empty list, not null", logs);
        assertTrue("Distant past range must return no records", logs.isEmpty());
    }

    @Test
    public void getLogs_range_futureDate_returnsEmpty() {
        Timestamp start = new Timestamp(System.currentTimeMillis() + 86400000L * 365L); // 1 year ahead
        Timestamp end   = new Timestamp(System.currentTimeMillis() + 86400000L * 366L);
        List<ActivityLog> logs = dao.getLogs(start, end);
        assertNotNull("Future-range result must not be null", logs);
        assertTrue("Future date range must return no records", logs.isEmpty());
    }

    @Test
    public void getLogs_range_startEqualsEnd_doesNotCrash() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<ActivityLog> logs = dao.getLogs(now, now);
        assertNotNull("Equal start/end must not crash; returns list (may be empty)", logs);
    }

    // ── TC-DBMS-004: getLogsByUser — user-scoped activity data ───────────

    @Test
    public void getLogsByUser_seededUser_returnsAtLeastOne() {
        List<ActivityLog> logs = dao.getLogsByUser("timeboundtest@school.edu", 10);
        assertNotNull(logs);
        assertFalse("Seeded test user must have at least one log entry", logs.isEmpty());
    }

    @Test
    public void getLogsByUser_allReturnedLogsMatchUsername() {
        List<ActivityLog> logs = dao.getLogsByUser("timeboundtest@school.edu", 50);
        for (ActivityLog log : logs) {
            assertEquals("All logs must belong to the queried user",
                    "timeboundtest@school.edu", log.getUsername());
        }
    }

    @Test
    public void getLogsByUser_unknownUser_returnsEmptyNotNull() {
        List<ActivityLog> logs = dao.getLogsByUser("nobody@does-not-exist.com", 10);
        assertNotNull("Unknown user must return empty list, not null", logs);
        assertTrue("Unknown user must have zero logs", logs.isEmpty());
    }

    // ── TC-DBMS-002: verify 3-DBMS architecture (Postgres role) ──────────

    @Test
    public void postgresDAO_storesAuditLogsForAllRoles() {
        dao.log("auditcheck@school.edu", "AUDIT_ADMIN", "10.0.0.1", "Admin", "TestSuite");
        dao.log("auditcheck@school.edu", "AUDIT_GUEST", "10.0.0.1", "Guest", "TestSuite");
        List<ActivityLog> logs = dao.getLogsByUser("auditcheck@school.edu", 5);
        assertNotNull(logs);
        assertTrue("Audit logs must be stored for multiple roles", logs.size() >= 2);
    }
}
