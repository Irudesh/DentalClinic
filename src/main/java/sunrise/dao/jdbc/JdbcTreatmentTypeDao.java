package sunrise.dao.jdbc;

import sunrise.dao.TreatmentTypeDao;
import sunrise.model.TreatmentType;
import sunrise.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTreatmentTypeDao implements TreatmentTypeDao {

    private final DatabaseConnectionManager db;

    public JdbcTreatmentTypeDao(DatabaseConnectionManager db) {
        this.db = db;
    }

    @Override
    public void save(TreatmentType treatmentType) {
        String sql = "INSERT INTO treatment_types (id, name, fee) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = VALUES(name), fee = VALUES(fee)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treatmentType.getId());
            ps.setString(2, treatmentType.getName());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(treatmentType.getFee()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save treatment type " + treatmentType.getId(), e);
        }
    }

    @Override
    public Optional<TreatmentType> findById(String id) {
        String sql = "SELECT id, name, fee FROM treatment_types WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find treatment type " + id, e);
        }
    }

    @Override
    public List<TreatmentType> findAll() {
        String sql = "SELECT id, name, fee FROM treatment_types ORDER BY name";
        List<TreatmentType> result = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list treatment types", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM treatment_types WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete treatment type " + id, e);
        }
    }

    private TreatmentType map(ResultSet rs) throws SQLException {
        return new TreatmentType(rs.getString("id"), rs.getString("name"), rs.getBigDecimal("fee").doubleValue());
    }
}
