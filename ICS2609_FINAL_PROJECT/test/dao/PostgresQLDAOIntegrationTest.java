package dao;

import java.util.List;
import model.ActivityLog;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import util.TestServletContextStub;

/**
 * Integration tests for PostgresQLDAO.
 * Requires PostgreSQL running on localhost:5432 with activity_logs table created.
 */
public class PostgresQLDAOIntegrationTest {

    private static PostgresQLDAO dao;

    private static final String TEST_USER   = "test.integration@school.edu";
    private static final String TEST_ACTION = "INTEGRATION_TEST";
    private static final String TEST_IP     = "127.0.0.1";
    private static final String TEST_ROLE   = "SysAdmin";
    private static final String TEST_SOURCE = "PostgresQLDAOIntegrationTest";

    @BeforeClass
    public static void setUp() {
        dao = new PostgresQLDAO(TestServletContextStub.forPostgres());
    }

    // ── log() ─────────────────────────────────────────────────────────────

    @Test
    public void log_insertsRecord() {
        // Insert a unique log entry and verify it appears as the most recent record
        String uniqueAction = TEST_ACTION + "_" + System.currentTimeMillis();
        dao.log(TEST_USER, uniqueAction, TEST_IP, TEST_ROLE, TEST_SOURCE);

        List<ActivityLog> logs = dao.getLogs(1);
        assertNotNull(logs);
        assertFalse("Log list must not be empty after insert", logs.isEmpty());

        ActivityLog top = logs.get(0);
        assertEquals("Most recent log username should match", TEST_USER, top.getUsername());
        assertEquals("Most recent log action should match", uniqueAction, top.getAction());
    }

    @Test
    public void log_doesNotThrowOnValidInput() {
        // Should complete silently without exception
        dao.log("user@test.com", "TEST_ACTION", "192.168.1.1", "Guest", "TestSuite");
    }

    // ── getLogs() ─────────────────────────────────────────────────────────

    @Test
    public void getLogs_respectsLimit() {
        List<ActivityLog> logs = dao.getLogs(3);
        assertNotNull(logs);
        assertTrue("getLogs(3) must return at most 3 entries", logs.size() <= 3);
    }

    @Test
    public void getLogs_largerLimit_returnsAtLeastOne() {
        List<ActivityLog> logs = dao.getLogs(100);
        assertNotNull(logs);
        assertFalse("Database should have at least one log entry", logs.isEmpty());
    }

    @Test
    public void getLogs_orderedByTimestampDescending() {
        List<ActivityLog> logs = dao.getLogs(50);
        assertNotNull(logs);
        if (logs.size() < 2) return; // can't check order with one entry

        for (int i = 0; i < logs.size() - 1; i++) {
            java.sql.Timestamp t1 = logs.get(i).getActivityTime();
            java.sql.Timestamp t2 = logs.get(i + 1).getActivityTime();
            if (t1 != null && t2 != null) {
                assertTrue("Logs must be ordered newest first (DESC)",
                    t1.compareTo(t2) >= 0);
            }
        }
    }

    @Test
    public void getLogs_returnsPopulatedActivityLogObjects() {
        List<ActivityLog> logs = dao.getLogs(10);
        assertNotNull(logs);
        for (ActivityLog log : logs) {
            assertNotNull("Username must not be null", log.getUsername());
            assertNotNull("Action must not be null", log.getAction());
            assertNotNull("IP address must not be null", log.getIpAddress());
            assertNotNull("Timestamp must not be null", log.getActivityTime());
        }
    }

    @Test
    public void getLogs_zeroLimit_returnsEmpty() {
        List<ActivityLog> logs = dao.getLogs(0);
        assertNotNull(logs);
        assertTrue("Limit 0 should return empty list", logs.isEmpty());
    }
}
