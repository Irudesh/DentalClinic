package sunrise.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void sameInputAlwaysProducesSameHash() {
        assertEquals(PasswordUtil.hash("admin123"), PasswordUtil.hash("admin123"));
    }

    @Test
    void differentInputsProduceDifferentHashes() {
        assertNotEquals(PasswordUtil.hash("admin123"), PasswordUtil.hash("admin124"));
    }

    @Test
    void hashIsNeverEqualToThePlainPassword() {
        assertNotEquals("admin123", PasswordUtil.hash("admin123"));
    }

    @Test
    void matchesReturnsTrueForCorrectPassword() {
        String hash = PasswordUtil.hash("mySecret");
        assertTrue(PasswordUtil.matches("mySecret", hash));
    }

    @Test
    void matchesReturnsFalseForIncorrectPassword() {
        String hash = PasswordUtil.hash("mySecret");
        assertFalse(PasswordUtil.matches("wrongGuess", hash));
    }
}
