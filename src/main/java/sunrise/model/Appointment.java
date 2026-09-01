package sunrise.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private final String appointmentNumber;
    private final Patient patient;
    private final String dentistId;
    private final String treatmentTypeId;
    private final LocalDate date;
    private final LocalTime time;
    private final double discountPercent; 

    public Appointment(String appointmentNumber, Patient patient, String dentistId,
                        String treatmentTypeId, LocalDate date, LocalTime time,
                        double discountPercent) {
        this.appointmentNumber = appointmentNumber;
        this.patient = patient;
        this.dentistId = dentistId;
        this.treatmentTypeId = treatmentTypeId;
        this.date = date;
        this.time = time;
        this.discountPercent = discountPercent;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getDentistId() {
        return dentistId;
    }

    public String getTreatmentTypeId() {
        return treatmentTypeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public String toDataLine() {
        return String.join("|",
                appointmentNumber,
                patient.getName(),
                patient.getAddress(),
                patient.getContactNumber(),
                dentistId,
                treatmentTypeId,
                date.toString(),
                time.toString(),
                String.valueOf(discountPercent));
    }

    public static Appointment fromDataLine(String line) {
        String[] p = line.split("\\|", -1);
        Patient patient = new Patient(p[1], p[2], p[3]);
        double discount = p.length > 8 && !p[8].isEmpty() ? Double.parseDouble(p[8]) : 0.0;
        return new Appointment(p[0], patient, p[4], p[5], LocalDate.parse(p[6]), LocalTime.parse(p[7]), discount);
    }
}
