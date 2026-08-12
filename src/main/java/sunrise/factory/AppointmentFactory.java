package sunrise.factory;

import sunrise.model.Appointment;
import sunrise.model.Patient;
import sunrise.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Factory pattern: centralises how an Appointment object is built,
 * including generating its unique appointment number and validating the
 * raw input before an Appointment is allowed to exist. Kept deliberately
 * separate from AppointmentService (which coordinates persistence and
 * notifications) so that object-creation concerns and business-workflow
 * concerns are not mixed in one class (Single Responsibility Principle).
 */
public class AppointmentFactory {

    private final IdGenerator idGenerator;

    public AppointmentFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Appointment create(String patientName, String address, String contactNumber,
                               String dentistId, String treatmentTypeId,
                               LocalDate date, LocalTime time, double discountPercent) {
        if (patientName == null || patientName.isBlank()) {
            throw new IllegalArgumentException("Patient name is required.");
        }
        if (contactNumber == null || contactNumber.isBlank()) {
            throw new IllegalArgumentException("Contact number is required.");
        }
        if (dentistId == null || dentistId.isBlank()) {
            throw new IllegalArgumentException("A dentist must be selected.");
        }
        if (treatmentTypeId == null || treatmentTypeId.isBlank()) {
            throw new IllegalArgumentException("A treatment type must be selected.");
        }
        if (date == null || time == null) {
            throw new IllegalArgumentException("Appointment date and time are required.");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }

        Patient patient = new Patient(patientName.trim(), address == null ? "" : address.trim(), contactNumber.trim());
        String appointmentNumber = idGenerator.nextAppointmentNumber();
        return new Appointment(appointmentNumber, patient, dentistId, treatmentTypeId, date, time, discountPercent);
    }
}
