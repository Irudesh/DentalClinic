package sunrise.dao.jdbc;

import sunrise.dao.UserDao;
import sunrise.model.Role;
import sunrise.model.User;
import sunrise.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    private final DatabaseConnectionManager db;

    public JdbcUserDao(DatabaseConnectionManager db) {
        this.db = db;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, full_name) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), role = VALUES(role), "
                + "full_name = VALUES(full_name)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getFullName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user " + user.getUsername(), e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT username, password_hash, role, full_name FROM users WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user " + username, e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT username, password_hash, role, full_name FROM users ORDER BY username";
        List<User> result = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list users", e);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user " + username, e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("username"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role")),
                rs.getString("full_name"));
    }
}
