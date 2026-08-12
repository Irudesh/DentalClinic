package sunrise.dao.jdbc;

import sunrise.dao.DentistDao;
import sunrise.model.Dentist;
import sunrise.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL-backed implementation of DentistDao. All statements are
 * PreparedStatements with bound parameters (never string-concatenated
 * SQL), to avoid SQL injection - relevant to the module's Ethical/
 * secure-coding learning outcome.
 */
public class JdbcDentistDao implements DentistDao {

    private final DatabaseConnectionManager db;

    public JdbcDentistDao(DatabaseConnectionManager db) {
        this.db = db;
    }

    @Override
    public void save(Dentist dentist) {
        String sql = "INSERT INTO dentists (id, name, specialization) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = VALUES(name), specialization = VALUES(specialization)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dentist.getId());
            ps.setString(2, dentist.getName());
            ps.setString(3, dentist.getSpecialization());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save dentist " + dentist.getId(), e);
        }
    }

    @Override
    public Optional<Dentist> findById(String id) {
        String sql = "SELECT id, name, specialization FROM dentists WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find dentist " + id, e);
        }
    }

    @Override
    public List<Dentist> findAll() {
        String sql = "SELECT id, name, specialization FROM dentists ORDER BY name";
        List<Dentist> result = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list dentists", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM dentists WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete dentist " + id, e);
        }
    }

    private Dentist map(ResultSet rs) throws SQLException {
        return new Dentist(rs.getString("id"), rs.getString("name"), rs.getString("specialization"));
    }
}
