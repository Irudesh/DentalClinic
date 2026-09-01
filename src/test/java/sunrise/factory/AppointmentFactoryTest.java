package sunrise.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sunrise.model.Appointment;
import sunrise.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
class AppointmentFactoryTest {

    private AppointmentFactory factory;

    @BeforeEach
    void setUp() {
        factory = new AppointmentFactory(new IdGenerator(Collections.emptyList()));
    }

    @Test
    void createsAppointmentWithGeneratedNumberStartingAtApt1001() {
        Appointment appointment = factory.create(
                "Nimal Perera", "12 Galle Road, Colombo", "0771234567",
                "D001", "T001", LocalDate.of(2026, 8, 20), LocalTime.of(10, 30), 0);

        assertEquals("APT1001", appointment.getAppointmentNumber());
        assertEquals("Nimal Perera", appointment.getPatient().getName());
        assertEquals("D001", appointment.getDentistId());
        assertEquals("T001", appointment.getTreatmentTypeId());
    }

    @Test
    void generatesSequentialAppointmentNumbersAcrossMultipleCreations() {
        Appointment first = factory.create("A", "addr", "071", "D001", "T001",
                LocalDate.now(), LocalTime.NOON, 0);
        Appointment second = factory.create("B", "addr", "071", "D001", "T001",
                LocalDate.now(), LocalTime.NOON, 0);

        assertNotEquals(first.getAppointmentNumber(), second.getAppointmentNumber());
    }

    @Test
    void rejectsBlankPatientName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                factory.create("   ", "addr", "071", "D001", "T001", LocalDate.now(), LocalTime.NOON, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("patient name"));
    }

    @Test
    void rejectsMissingDentist() {
        assertThrows(IllegalArgumentException.class, () ->
                factory.create("Name", "addr", "071", "", "T001", LocalDate.now(), LocalTime.NOON, 0));
    }

    @Test
    void rejectsDiscountOutsideZeroToOneHundredRange() {
        assertThrows(IllegalArgumentException.class, () ->
                factory.create("Name", "addr", "071", "D001", "T001", LocalDate.now(), LocalTime.NOON, 150));
        assertThrows(IllegalArgumentException.class, () ->
                factory.create("Name", "addr", "071", "D001", "T001", LocalDate.now(), LocalTime.NOON, -5));
    }
}
