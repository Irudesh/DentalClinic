package sunrise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sunrise.dao.AppointmentDao;
import sunrise.factory.AppointmentFactory;
import sunrise.model.Appointment;
import sunrise.observer.AppointmentEventPublisher;
import sunrise.observer.AppointmentObserver;
import sunrise.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private InMemoryAppointmentDao dao;
    private List<Appointment> notifiedAppointments;

    @BeforeEach
    void setUp() {
        dao = new InMemoryAppointmentDao();
        AppointmentFactory factory = new AppointmentFactory(new IdGenerator(Collections.emptyList()));
        AppointmentEventPublisher publisher = new AppointmentEventPublisher();
        notifiedAppointments = new ArrayList<>();
        publisher.subscribe((AppointmentObserver) notifiedAppointments::add);
        appointmentService = new AppointmentService(dao, factory, publisher);
    }

    @Test
    void registeringAnAppointmentPersistsItAndNotifiesObservers() {
        Appointment created = appointmentService.registerAppointment(
                "Amal", "Negombo", "0711111111", "D001", "T001",
                LocalDate.now(), LocalTime.of(9, 0), 0);

        assertEquals(1, dao.findAll().size());
        assertEquals(1, notifiedAppointments.size());
        assertEquals(created.getAppointmentNumber(), notifiedAppointments.get(0).getAppointmentNumber());
    }

    @Test
    void findByNumberReturnsEmptyWhenNotRegistered() {
        Optional<Appointment> result = appointmentService.findByNumber("APT9999");
        assertTrue(result.isEmpty());
    }

    static class InMemoryAppointmentDao implements AppointmentDao {
        private final List<Appointment> data = new ArrayList<>();
        public void save(Appointment a) { data.add(a); }
        public Optional<Appointment> findByNumber(String number) {
            return data.stream().filter(a -> a.getAppointmentNumber().equals(number)).findFirst();
        }
        public List<Appointment> findAll() { return data; }
    }
}
