package sunrise.server;

import com.sun.net.httpserver.HttpServer;
import sunrise.server.handlers.*;
import sunrise.service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ApiServer {

    private final int port;
    private final AuthService authService;
    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private final DentistService dentistService;
    private final TreatmentTypeService treatmentTypeService;
    private final StaffService staffService;
    private final ReportService reportService;

    public ApiServer(int port, AuthService authService, AppointmentService appointmentService,
                      BillingService billingService, DentistService dentistService,
                      TreatmentTypeService treatmentTypeService, StaffService staffService,
                      ReportService reportService) {
        this.port = port;
        this.authService = authService;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.dentistService = dentistService;
        this.treatmentTypeService = treatmentTypeService;
        this.staffService = staffService;
        this.reportService = reportService;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // JSON API endpoints
        server.createContext("/api/login", new LoginHandler(authService));
        server.createContext("/api/logout", new LogoutHandler());
        server.createContext("/api/appointments", new AppointmentHandler(appointmentService));
        server.createContext("/api/bill", new BillHandler(billingService));
        server.createContext("/api/dentists", new DentistHandler(dentistService));
        server.createContext("/api/dentists/delete", new DentistDeleteHandler(dentistService));
        server.createContext("/api/treatmenttypes", new TreatmentTypeHandler(treatmentTypeService));
        server.createContext("/api/treatmenttypes/delete", new TreatmentTypeDeleteHandler(treatmentTypeService));
        server.createContext("/api/staff", new StaffHandler(staffService));
        server.createContext("/api/staff/delete", new StaffDeleteHandler(staffService));
        server.createContext("/api/reports/summary", new ReportHandler(reportService));

        // Static frontend (HTML/CSS/JS) - everything not under /api
        server.createContext("/", new StaticFileHandler());

        // A small fixed thread pool lets the server handle several
        // concurrent staff members without blocking on a single request.
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Sunrise Dental Clinic server running on http://localhost:" + port);
    }
}
