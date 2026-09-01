package sunrise;

import sunrise.dao.AppointmentDao;
import sunrise.dao.DentistDao;
import sunrise.dao.TreatmentTypeDao;
import sunrise.dao.UserDao;
import sunrise.dao.jdbc.JdbcAppointmentDao;
import sunrise.dao.jdbc.JdbcDentistDao;
import sunrise.dao.jdbc.JdbcTreatmentTypeDao;
import sunrise.dao.jdbc.JdbcUserDao;
import sunrise.factory.AppointmentFactory;
import sunrise.model.Appointment;
import sunrise.model.Dentist;
import sunrise.model.Role;
import sunrise.model.TreatmentType;
import sunrise.model.User;
import sunrise.observer.AppointmentEventPublisher;
import sunrise.observer.ConsoleNotificationObserver;
import sunrise.server.ApiServer;
import sunrise.service.*;
import sunrise.util.DatabaseConnectionManager;
import sunrise.util.FileStorageManager;
import sunrise.util.IdGenerator;
import sunrise.util.PasswordUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final boolean USE_DATABASE = true;

    public static void main(String[] args) throws IOException {
        UserDao userDao;
        DentistDao dentistDao;
        TreatmentTypeDao treatmentTypeDao;
        AppointmentDao appointmentDao;

        if (USE_DATABASE) {
            DatabaseConnectionManager db = DatabaseConnectionManager.getInstance();
            userDao = new JdbcUserDao(db);
            dentistDao = new JdbcDentistDao(db);
            treatmentTypeDao = new JdbcTreatmentTypeDao(db);
            appointmentDao = new JdbcAppointmentDao(db);
        } else {
            FileStorageManager storage = FileStorageManager.getInstance();
            userDao = new sunrise.dao.impl.FileUserDao(storage);
            dentistDao = new sunrise.dao.impl.FileDentistDao(storage);
            treatmentTypeDao = new sunrise.dao.impl.FileTreatmentTypeDao(storage);
            appointmentDao = new sunrise.dao.impl.FileAppointmentDao(storage);
        }

        seedDefaultData(userDao, dentistDao, treatmentTypeDao);

        List<String> existingNumbers = appointmentDao.findAll().stream()
                .map(Appointment::getAppointmentNumber)
                .collect(Collectors.toList());
        IdGenerator idGenerator = new IdGenerator(existingNumbers);
        AppointmentFactory appointmentFactory = new AppointmentFactory(idGenerator);

        AppointmentEventPublisher eventPublisher = new AppointmentEventPublisher();
        eventPublisher.subscribe(new ConsoleNotificationObserver(FileStorageManager.getInstance()));

        AuthService authService = new AuthService(userDao);
        AppointmentService appointmentService = new AppointmentService(appointmentDao, appointmentFactory, eventPublisher);
        BillingService billingService = new BillingService(appointmentDao, dentistDao, treatmentTypeDao);
        DentistService dentistService = new DentistService(dentistDao);
        TreatmentTypeService treatmentTypeService = new TreatmentTypeService(treatmentTypeDao);
        StaffService staffService = new StaffService(userDao);
        ReportService reportService = new ReportService(appointmentDao, treatmentTypeDao);

        ApiServer server = new ApiServer(8080, authService, appointmentService, billingService,
                dentistService, treatmentTypeService, staffService, reportService);
        server.start();
    }

    private static void seedDefaultData(UserDao userDao, DentistDao dentistDao, TreatmentTypeDao treatmentTypeDao) {
        if (userDao.findAll().isEmpty()) {
            userDao.save(new User("admin", PasswordUtil.hash("admin123"), Role.ADMIN, "Clinic Administrator"));
            userDao.save(new User("reception", PasswordUtil.hash("reception123"), Role.RECEPTIONIST, "Front Desk"));
            System.out.println("Seeded default accounts: admin/admin123 (Admin), reception/reception123 (Receptionist)");
        }
        if (dentistDao.findAll().isEmpty()) {
            dentistDao.save(new Dentist("D001", "Dr. N. Perera", "General Dentistry"));
            dentistDao.save(new Dentist("D002", "Dr. S. Silva", "Orthodontics"));
        }
        if (treatmentTypeDao.findAll().isEmpty()) {
            treatmentTypeDao.save(new TreatmentType("T001", "Consultation", 1500.0));
            treatmentTypeDao.save(new TreatmentType("T002", "Scaling and Polishing", 3000.0));
            treatmentTypeDao.save(new TreatmentType("T003", "Filling", 4500.0));
            treatmentTypeDao.save(new TreatmentType("T004", "Root Canal Treatment", 15000.0));
        }
    }
}
