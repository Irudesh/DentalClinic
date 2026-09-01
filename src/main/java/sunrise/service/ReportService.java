package sunrise.service;

import sunrise.dao.AppointmentDao;
import sunrise.dao.TreatmentTypeDao;
import sunrise.model.Appointment;
import sunrise.model.TreatmentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private final AppointmentDao appointmentDao;
    private final TreatmentTypeDao treatmentTypeDao;

    public ReportService(AppointmentDao appointmentDao, TreatmentTypeDao treatmentTypeDao) {
        this.appointmentDao = appointmentDao;
        this.treatmentTypeDao = treatmentTypeDao;
    }

    public int totalAppointments() {
        return appointmentDao.findAll().size();
    }

    public Map<String, Long> appointmentsPerDentist() {
        Map<String, Long> counts = new HashMap<>();
        for (Appointment a : appointmentDao.findAll()) {
            counts.merge(a.getDentistId(), 1L, Long::sum);
        }
        return counts;
    }

    public double estimatedTotalRevenue() {
        Map<String, Double> feeById = new HashMap<>();
        for (TreatmentType t : treatmentTypeDao.findAll()) {
            feeById.put(t.getId(), t.getFee());
        }
        double total = 0;
        List<Appointment> appointments = appointmentDao.findAll();
        for (Appointment a : appointments) {
            double base = feeById.getOrDefault(a.getTreatmentTypeId(), 0.0);
            double discount = base * (a.getDiscountPercent() / 100.0);
            total += (base - discount);
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
