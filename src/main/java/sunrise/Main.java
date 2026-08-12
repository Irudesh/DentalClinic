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

/**
 * Application entry point: composes all the objects (dependency wiring is
 * done here by hand, since no dependency-injection framework is
 * permitted), seeds first-run demo data, and starts the web server.
 *
 * Storage backend: this class wires the MySQL-backed (JDBC) DAO
 * implementations by default. Because every DAO is used only through its
 * interface (sunrise.dao.AppointmentDao, DentistDao, TreatmentTypeDao,
 * UserDao) everywhere else in the application, switching back to the
 * plain-text-file DAOs for comparison/demo purposes is a one-line change
 * per line below (swap JdbcXxxDao for FileXxxDao) - nothing in the
 * service, factory, observer, or server layers needs to change either
 * way. This is the practical benefit of the DAO/Repository pattern
 * discussed in the report.
 */
public class Main {

    /** Set to false to fall back to the text-file storage implementation instead of MySQL. */
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
        // The notification log is a simple text file regardless of which
        // database backend is used for application data - it is not part
        // of the DAO layer, just an audit trail, so it always uses
        // FileStorageManager directly.
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

    /** Seeds a default Admin and Receptionist account plus sample dentists/treatments on first run only. */
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
