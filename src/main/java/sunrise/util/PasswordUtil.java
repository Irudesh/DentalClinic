package sunrise.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Minimal password hashing so that credentials are never stored in plain
 * text, without depending on any external library.
 *
 * Note (documented as a known limitation for the report): a single static
 * salt is used here for simplicity. In a production system each user
 * should have their own random salt, and a slower algorithm such as
 * bcrypt or PBKDF2 should be preferred over a single SHA-256 pass.
 */
public final class PasswordUtil {

    private static final String SALT = "SunriseDentalClinic#2026";

    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest((SALT + plainPassword).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashed) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available on this JVM", e);
        }
    }

    public static boolean matches(String plainPassword, String storedHash) {
        return hash(plainPassword).equals(storedHash);
    }
}
