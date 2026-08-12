package sunrise.model;

import java.util.Objects;

/**
 * Represents a staff login account (Task: User Authentication).
 * The password is always stored as a hash (see sunrise.util.PasswordUtil),
 * never in plain text.
 */
public class User {

    private final String username;
    private String passwordHash;
    private Role role;
    private String fullName;

    public User(String username, String passwordHash, Role role, String fullName) {
        this.username = Objects.requireNonNull(username, "username is required");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash is required");
        this.role = Objects.requireNonNull(role, "role is required");
        this.fullName = fullName == null ? "" : fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** Serialises this user to a single pipe-delimited line for text-file storage. */
    public String toDataLine() {
        return String.join("|", username, passwordHash, role.name(), fullName);
    }

    public static User fromDataLine(String line) {
        String[] p = line.split("\\|", -1);
        return new User(p[0], p[1], Role.valueOf(p[2]), p.length > 3 ? p[3] : "");
    }
}
