package util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pure unit tests for SecurityUtil — no databases, no servlet context needed.
 */
public class SecurityUtilTest {

    // Known SHA-256 hash of "12345678" — matches derby_setup.sql seed data
    private static final String HASH_12345678 =
        "ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f";

    // ── hashPassword ──────────────────────────────────────────────────────

    @Test
    public void hashKnownValue() {
        assertEquals(HASH_12345678, SecurityUtil.hashPassword("12345678"));
    }

    @Test
    public void hashConsistency() {
        String first  = SecurityUtil.hashPassword("somepassword");
        String second = SecurityUtil.hashPassword("somepassword");
        assertEquals("Same input must always produce same hash", first, second);
    }

    @Test
    public void hashProduces64HexChars() {
        String hash = SecurityUtil.hashPassword("test");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hashNullThrows() {
        SecurityUtil.hashPassword(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hashEmptyThrows() {
        SecurityUtil.hashPassword("");
    }

    @Test
    public void hashDifferentPasswordsProduceDifferentHashes() {
        String h1 = SecurityUtil.hashPassword("password1");
        String h2 = SecurityUtil.hashPassword("password2");
        assertNotEquals(h1, h2);
    }

    // ── verifyPassword ────────────────────────────────────────────────────

    @Test
    public void verifyCorrectPassword() {
        assertTrue(SecurityUtil.verifyPassword("12345678", HASH_12345678));
    }

    @Test
    public void verifyWrongPassword() {
        assertFalse(SecurityUtil.verifyPassword("wrongpass", HASH_12345678));
    }

    @Test
    public void verifyNullPasswordReturnsFalse() {
        assertFalse(SecurityUtil.verifyPassword(null, HASH_12345678));
    }

    @Test
    public void verifyNullHashReturnsFalse() {
        assertFalse(SecurityUtil.verifyPassword("12345678", null));
    }

    @Test
    public void verifyBothNullReturnsFalse() {
        assertFalse(SecurityUtil.verifyPassword(null, null));
    }

    // ── sanitizeUsername ──────────────────────────────────────────────────

    @Test
    public void sanitizeUsernamePreservesValidEmail() {
        assertEquals("user@example.com", SecurityUtil.sanitizeUsername("user@example.com"));
    }

    @Test
    public void sanitizeUsernameStripsSpecialChars() {
        // only a-z A-Z 0-9 _ . - @ are allowed
        String result = SecurityUtil.sanitizeUsername("user!#$%^&*()+='");
        assertFalse("Special chars should be stripped", result.contains("!"));
        assertFalse(result.contains("#"));
        assertFalse(result.contains("$"));
    }

    @Test
    public void sanitizeUsernameTrimsWhitespace() {
        assertEquals("user@test.com", SecurityUtil.sanitizeUsername("  user@test.com  "));
    }

    @Test
    public void sanitizeUsernameNullReturnsEmpty() {
        assertEquals("", SecurityUtil.sanitizeUsername(null));
    }

    @Test
    public void sanitizeUsernamePreservesDotsAndDashes() {
        assertEquals("first.last-name@school.edu",
            SecurityUtil.sanitizeUsername("first.last-name@school.edu"));
    }

    // ── sanitizeHtml ──────────────────────────────────────────────────────

    @Test
    public void sanitizeHtmlEscapesLessThan() {
        assertTrue(SecurityUtil.sanitizeHtml("<script>").contains("&lt;"));
    }

    @Test
    public void sanitizeHtmlEscapesGreaterThan() {
        assertTrue(SecurityUtil.sanitizeHtml("<div>").contains("&gt;"));
    }

    @Test
    public void sanitizeHtmlEscapesAmpersand() {
        assertEquals("a&amp;b", SecurityUtil.sanitizeHtml("a&b"));
    }

    @Test
    public void sanitizeHtmlEscapesDoubleQuote() {
        assertTrue(SecurityUtil.sanitizeHtml("say \"hi\"").contains("&quot;"));
    }

    @Test
    public void sanitizeHtmlEscapesSingleQuote() {
        assertTrue(SecurityUtil.sanitizeHtml("it's").contains("&#x27;"));
    }

    @Test
    public void sanitizeHtmlNullReturnsEmpty() {
        assertEquals("", SecurityUtil.sanitizeHtml(null));
    }

    @Test
    public void sanitizeHtmlSafeStringPassesThrough() {
        assertEquals("Hello World", SecurityUtil.sanitizeHtml("Hello World"));
    }

    // ── isBlank ───────────────────────────────────────────────────────────

    @Test
    public void isBlankNull() {
        assertTrue(SecurityUtil.isBlank(null));
    }

    @Test
    public void isBlankEmptyString() {
        assertTrue(SecurityUtil.isBlank(""));
    }

    @Test
    public void isBlankWhitespaceOnly() {
        assertTrue(SecurityUtil.isBlank("   "));
    }

    @Test
    public void isBlankTabAndNewline() {
        assertTrue(SecurityUtil.isBlank("\t\n"));
    }

    @Test
    public void isBlankNonEmpty() {
        assertFalse(SecurityUtil.isBlank("a"));
    }

    @Test
    public void isBlankEmailAddress() {
        assertFalse(SecurityUtil.isBlank("user@test.com"));
    }
}
