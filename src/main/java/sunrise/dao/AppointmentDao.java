package sunrise.dao;

import sunrise.model.Appointment;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) pattern: decouples the rest of the application
 * from the storage mechanism. Callers work only against this interface;
 * FileAppointmentDao is today's implementation, but it could be swapped
 * for a JDBC-backed implementation later without touching any service
 * or handler class.
 */
public interface AppointmentDao {

    void save(Appointment appointment);

    Optional<Appointment> findByNumber(String appointmentNumber);

    List<Appointment> findAll();
}
