package sunrise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sunrise.dao.AppointmentDao;
import sunrise.dao.DentistDao;
import sunrise.dao.TreatmentTypeDao;
import sunrise.model.Appointment;
import sunrise.model.Bill;
import sunrise.model.Dentist;
import sunrise.model.Patient;
import sunrise.model.TreatmentType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {

    private BillingService billingService;
    private InMemoryAppointmentDao appointmentDao;

    @BeforeEach
    void setUp() {
        appointmentDao = new InMemoryAppointmentDao();
        DentistDao dentistDao = new InMemoryDentistDao();
        TreatmentTypeDao treatmentTypeDao = new InMemoryTreatmentTypeDao();
        billingService = new BillingService(appointmentDao, dentistDao, treatmentTypeDao);
    }

    @Test
    void generatesBillWithoutDiscountUsingStandardStrategy() {
        appointmentDao.save(new Appointment("APT1001", new Patient("Kamal", "Colombo", "071"),
                "D001", "T001", LocalDate.now(), LocalTime.NOON, 0));

        Optional<Bill> bill = billingService.generateBill("APT1001");

        assertTrue(bill.isPresent());
        assertEquals(1500.0, bill.get().getBaseFee());
        assertEquals(0.0, bill.get().getDiscountAmount());
        assertEquals(1500.0, bill.get().getTotalAmount());
    }

    @Test
    void generatesBillWithDiscountUsingDiscountStrategy() {
        appointmentDao.save(new Appointment("APT1002", new Patient("Sunil", "Kandy", "072"),
                "D001", "T001", LocalDate.now(), LocalTime.NOON, 10));

        Optional<Bill> bill = billingService.generateBill("APT1002");

        assertTrue(bill.isPresent());
        assertEquals(150.0, bill.get().getDiscountAmount());
        assertEquals(1350.0, bill.get().getTotalAmount());
    }

    @Test
    void returnsEmptyWhenAppointmentNumberNotFound() {
        assertTrue(billingService.generateBill("APT9999").isEmpty());
    }

    static class InMemoryAppointmentDao implements AppointmentDao {
        private final List<Appointment> data = new ArrayList<>();
        public void save(Appointment a) { data.add(a); }
        public Optional<Appointment> findByNumber(String number) {
            return data.stream().filter(a -> a.getAppointmentNumber().equals(number)).findFirst();
        }
        public List<Appointment> findAll() { return data; }
    }

    static class InMemoryDentistDao implements DentistDao {
        private final List<Dentist> data = new ArrayList<>(List.of(new Dentist("D001", "Dr. Perera", "General")));
        public void save(Dentist d) { data.add(d); }
        public Optional<Dentist> findById(String id) { return data.stream().filter(d -> d.getId().equals(id)).findFirst(); }
        public List<Dentist> findAll() { return data; }
        public void deleteById(String id) { data.removeIf(d -> d.getId().equals(id)); }
    }

    static class InMemoryTreatmentTypeDao implements TreatmentTypeDao {
        private final List<TreatmentType> data = new ArrayList<>(List.of(new TreatmentType("T001", "Consultation", 1500.0)));
        public void save(TreatmentType t) { data.add(t); }
        public Optional<TreatmentType> findById(String id) { return data.stream().filter(t -> t.getId().equals(id)).findFirst(); }
        public List<TreatmentType> findAll() { return data; }
        public void deleteById(String id) { data.removeIf(t -> t.getId().equals(id)); }
    }
}
