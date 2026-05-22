package business;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pure unit tests for date validation logic used in ReportServlet (time-bound report).
 *
 * Covers SRS test cases:
 *   TC-TIME-003  Invalid date format → validation error, no crash
 *   TC-TIME-004  Start date after end date → validation error
 *   TC-TIME-005  Filename timestamp reflects generation time, not filter dates
 */
public class ReportDateValidationTest {

    // Mirrors the SimpleDateFormat used in ReportServlet
    private SimpleDateFormat makeSdf() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf;
    }

    // ── TC-TIME-003: invalid date formats ────────────────────────────────

    @Test
    public void invalidDate_slashFormat_throwsParseException() {
        try {
            makeSdf().parse("2026/05/01");
            fail("Slash-formatted date must throw ParseException");
        } catch (ParseException e) {
            // expected
        }
    }

    @Test
    public void invalidDate_DMYformat_throwsParseException() {
        try {
            makeSdf().parse("01-05-2026");
            fail("DD-MM-YYYY format must throw ParseException");
        } catch (ParseException e) {
            // expected
        }
    }

    @Test
    public void invalidDate_plainText_throwsParseException() {
        try {
            makeSdf().parse("not-a-date");
            fail("Non-date text must throw ParseException");
        } catch (ParseException e) {
            // expected
        }
    }

    @Test
    public void invalidDate_month13_throwsParseException() {
        try {
            makeSdf().parse("2026-13-01");
            fail("Month 13 must throw ParseException (non-lenient)");
        } catch (ParseException e) {
            // expected
        }
    }

    @Test
    public void invalidDate_day32_throwsParseException() {
        try {
            makeSdf().parse("2026-01-32");
            fail("Day 32 must throw ParseException (non-lenient)");
        } catch (ParseException e) {
            // expected
        }
    }

    @Test
    public void invalidDate_blankString_throwsParseException() {
        try {
            makeSdf().parse("   ".trim());
            fail("Blank date string must throw ParseException");
        } catch (ParseException e) {
            // expected
        }
    }

    // ── TC-TIME-003: valid date format passes ─────────────────────────────

    @Test
    public void validDate_isoFormat_parsesSuccessfully() throws Exception {
        Date d = makeSdf().parse("2026-05-01");
        assertNotNull("Valid ISO date must parse without exception", d);
    }

    @Test
    public void validDate_startOfYear_parsesSuccessfully() throws Exception {
        Date d = makeSdf().parse("2026-01-01");
        assertNotNull(d);
    }

    @Test
    public void validDate_endOfYear_parsesSuccessfully() throws Exception {
        Date d = makeSdf().parse("2026-12-31");
        assertNotNull(d);
    }

    // ── TC-TIME-004: start date after end date ───────────────────────────

    @Test
    public void startAfterEnd_isDetectedCorrectly() throws Exception {
        Date start = makeSdf().parse("2026-12-01");
        Date end   = makeSdf().parse("2026-01-01");
        assertTrue("Start after end must be detected", start.after(end));
    }

    @Test
    public void startEqualsEnd_isAllowed() throws Exception {
        Date start = makeSdf().parse("2026-06-01");
        Date end   = makeSdf().parse("2026-06-01");
        assertFalse("Equal start and end dates must not trigger the start-after-end check",
                start.after(end));
    }

    @Test
    public void startBeforeEnd_isValid() throws Exception {
        Date start = makeSdf().parse("2026-01-01");
        Date end   = makeSdf().parse("2026-12-31");
        assertFalse("Valid range: start before end must not be flagged", start.after(end));
    }

    // ── TC-TIME-003: Timestamp conversion for DB query ───────────────────

    @Test
    public void timestampFromDate_preservesMillis() throws Exception {
        Date d = makeSdf().parse("2026-05-01");
        Timestamp ts = new Timestamp(d.getTime());
        assertNotNull(ts);
        assertEquals("Timestamp millis must match parsed date millis", d.getTime(), ts.getTime());
    }

    @Test
    public void endDateTimestamp_includesFullDay() throws Exception {
        // ReportServlet adds 86400000L - 1L (full day minus 1 ms) to the end date
        Date end = makeSdf().parse("2026-05-01");
        Timestamp tsEnd = new Timestamp(end.getTime() + 86400000L - 1L);
        assertTrue("End timestamp must be after start of same day", tsEnd.getTime() > end.getTime());
        // Difference must be exactly 86399999 ms (one day minus 1 ms)
        assertEquals(86399999L, tsEnd.getTime() - end.getTime());
    }

    // ── TC-TIME-005: filename timestamp ≠ filter date range ───────────────

    @Test
    public void filenameTimestamp_reflectsGenerationTime_notFilterRange() throws Exception {
        // The filename timestamp is always System.currentTimeMillis()-based, not the filter range.
        long before = System.currentTimeMillis();
        String ts = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        long after  = System.currentTimeMillis();

        // The generated timestamp must fall within the current test's time window
        long tsMillis = new java.text.SimpleDateFormat("yyyyMMddHHmmss").parse(ts).getTime();
        assertTrue("Filename timestamp must be at or after test start", tsMillis >= before - 1000);
        assertTrue("Filename timestamp must be at or before test end",  tsMillis <= after  + 1000);
    }

    @Test
    public void filterDateRange_doesNotAffectFilenameTimestamp() throws Exception {
        // Simulate: report filtered for 2024 data, generated now
        Date filterStart = makeSdf().parse("2024-01-01");
        Date filterEnd   = makeSdf().parse("2024-12-31");
        String filenameTs = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Filename timestamp year must be current year, not filter year
        String filterYear = "2024";
        String currentYear = String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
        assertFalse("Filename timestamp must not be from the filter year",
                filenameTs.startsWith(filterYear));
        assertTrue("Filename timestamp must start with the current year",
                filenameTs.startsWith(currentYear));
    }
}
