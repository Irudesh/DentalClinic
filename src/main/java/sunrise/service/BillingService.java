package sunrise.service;

import sunrise.dao.AppointmentDao;
import sunrise.dao.DentistDao;
import sunrise.dao.TreatmentTypeDao;
import sunrise.model.Appointment;
import sunrise.model.Bill;
import sunrise.model.Dentist;
import sunrise.model.TreatmentType;
import sunrise.service.fee.DiscountFeeStrategy;
import sunrise.service.fee.FeeCalculationStrategy;
import sunrise.service.fee.StandardFeeStrategy;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implements "Calculate and Print Bill". Picks a FeeCalculationStrategy
 * at run time (Strategy pattern) depending on whether the appointment has
 * a discount applied.
 */
public class BillingService {

    private final AppointmentDao appointmentDao;
    private final DentistDao dentistDao;
    private final TreatmentTypeDao treatmentTypeDao;

    public BillingService(AppointmentDao appointmentDao, DentistDao dentistDao, TreatmentTypeDao treatmentTypeDao) {
        this.appointmentDao = appointmentDao;
        this.dentistDao = dentistDao;
        this.treatmentTypeDao = treatmentTypeDao;
    }

    public Optional<Bill> generateBill(String appointmentNumber) {
        Optional<Appointment> appointmentOpt = appointmentDao.findByNumber(appointmentNumber);
        if (appointmentOpt.isEmpty()) {
            return Optional.empty();
        }
        Appointment appointment = appointmentOpt.get();

        TreatmentType treatmentType = treatmentTypeDao.findById(appointment.getTreatmentTypeId())
                .orElseThrow(() -> new IllegalStateException(
                        "Treatment type " + appointment.getTreatmentTypeId() + " no longer exists."));
        Dentist dentist = dentistDao.findById(appointment.getDentistId())
                .orElseThrow(() -> new IllegalStateException(
                        "Dentist " + appointment.getDentistId() + " no longer exists."));

        double baseFee = treatmentType.getFee();
        double discountPercent = appointment.getDiscountPercent();

        FeeCalculationStrategy strategy = discountPercent > 0
                ? new DiscountFeeStrategy()
                : new StandardFeeStrategy();

        double total = strategy.calculateTotal(baseFee, discountPercent);
        double discountAmount = StandardFeeStrategy.round(baseFee - total);

        Bill bill = new Bill(
                appointment.getAppointmentNumber(),
                appointment.getPatient().getName(),
                dentist.getName(),
                treatmentType.getName(),
                baseFee,
                discountPercent,
                discountAmount,
                total,
                LocalDateTime.now());
        return Optional.of(bill);
    }
}
