package sunrise.dao;

import sunrise.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentDao {

    void save(Appointment appointment);

    Optional<Appointment> findByNumber(String appointmentNumber);

    List<Appointment> findAll();
}
