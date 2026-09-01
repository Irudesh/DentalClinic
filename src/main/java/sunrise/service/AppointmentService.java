package sunrise.service;

import sunrise.dao.AppointmentDao;
import sunrise.factory.AppointmentFactory;
import sunrise.model.Appointment;
import sunrise.observer.AppointmentEventPublisher;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class AppointmentService {

    private final AppointmentDao appointmentDao;
    private final AppointmentFactory appointmentFactory;
    private final AppointmentEventPublisher eventPublisher;

    public AppointmentService(AppointmentDao appointmentDao, AppointmentFactory appointmentFactory,
                               AppointmentEventPublisher eventPublisher) {
        this.appointmentDao = appointmentDao;
        this.appointmentFactory = appointmentFactory;
        this.eventPublisher = eventPublisher;
    }

    public Appointment registerAppointment(String patientName, String address, String contactNumber,
                                            String dentistId, String treatmentTypeId,
                                            LocalDate date, LocalTime time, double discountPercent) {
        Appointment appointment = appointmentFactory.create(
                patientName, address, contactNumber, dentistId, treatmentTypeId, date, time, discountPercent);
        appointmentDao.save(appointment);
        eventPublisher.publish(appointment);
        return appointment;
    }

    public Optional<Appointment> findByNumber(String appointmentNumber) {
        return appointmentDao.findByNumber(appointmentNumber);
    }

    public List<Appointment> findAll() {
        return appointmentDao.findAll();
    }
}
