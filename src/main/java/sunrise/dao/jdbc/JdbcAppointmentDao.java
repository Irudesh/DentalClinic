package sunrise.dao.jdbc;

import sunrise.dao.AppointmentDao;
import sunrise.model.Appointment;
import sunrise.model.Patient;
import sunrise.util.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAppointmentDao implements AppointmentDao {

    private final DatabaseConnectionManager db;

    public JdbcAppointmentDao(DatabaseConnectionManager db) {
        this.db = db;
    }

    @Override
    public void save(Appointment appointment) {
        String sql = "INSERT INTO appointments "
                + "(appointment_number, patient_name, address, contact_number, dentist_id, "
                + " treatment_type_id, appointment_date, appointment_time, discount_percent) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointment.getAppointmentNumber());
            ps.setString(2, appointment.getPatient().getName());
            ps.setString(3, appointment.getPatient().getAddress());
            ps.setString(4, appointment.getPatient().getContactNumber());
            ps.setString(5, appointment.getDentistId());
            ps.setString(6, appointment.getTreatmentTypeId());
            ps.setDate(7, java.sql.Date.valueOf(appointment.getDate()));
            ps.setTime(8, java.sql.Time.valueOf(appointment.getTime()));
            ps.setBigDecimal(9, BigDecimal.valueOf(appointment.getDiscountPercent()));
            ps.executeUpdate();
        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException(
                        "That dentist already has an appointment at this date and time.", e);
            }
            throw new RuntimeException("Failed to save appointment " + appointment.getAppointmentNumber(), e);
        }
    }

    @Override
    public Optional<Appointment> findByNumber(String appointmentNumber) {
        String sql = "SELECT appointment_number, patient_name, address, contact_number, dentist_id, "
                + "treatment_type_id, appointment_date, appointment_time, discount_percent "
                + "FROM appointments WHERE appointment_number = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find appointment " + appointmentNumber, e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        String sql = "SELECT appointment_number, patient_name, address, contact_number, dentist_id, "
                + "treatment_type_id, appointment_date, appointment_time, discount_percent "
                + "FROM appointments ORDER BY appointment_date, appointment_time";
        List<Appointment> result = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list appointments", e);
        }
    }

    private Appointment map(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getString("patient_name"), rs.getString("address"), rs.getString("contact_number"));
        return new Appointment(
                rs.getString("appointment_number"),
                patient,
                rs.getString("dentist_id"),
                rs.getString("treatment_type_id"),
                rs.getDate("appointment_date").toLocalDate(),
                rs.getTime("appointment_time").toLocalTime(),
                rs.getBigDecimal("discount_percent").doubleValue());
    }
}
