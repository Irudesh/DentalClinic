package sunrise.model;

import java.time.LocalDateTime;

public class Bill {

    private final String appointmentNumber;
    private final String patientName;
    private final String dentistName;
    private final String treatmentName;
    private final double baseFee;
    private final double discountPercent;
    private final double discountAmount;
    private final double totalAmount;
    private final LocalDateTime generatedAt;

    public Bill(String appointmentNumber, String patientName, String dentistName, String treatmentName,
                double baseFee, double discountPercent, double discountAmount, double totalAmount,
                LocalDateTime generatedAt) {
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.dentistName = dentistName;
        this.treatmentName = treatmentName;
        this.baseFee = baseFee;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.generatedAt = generatedAt;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}
