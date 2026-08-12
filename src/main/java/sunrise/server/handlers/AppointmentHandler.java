package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.Appointment;
import sunrise.model.User;
import sunrise.service.AppointmentService;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AppointmentHandler implements HttpHandler {

    private final AppointmentService appointmentService;

    public AppointmentHandler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGet(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            handlePost(exchange);
        } else {
            ApiSupport.sendError(exchange, 405, "Unsupported method.");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Map<String, String> params = ApiSupport.queryParams(exchange);
        User user = ApiSupport.requireAuth(exchange, params);
        if (user == null) {
            ApiSupport.sendError(exchange, 401, "Please log in.");
            return;
        }
        String number = params.get("number");
        if (number != null && !number.isBlank()) {
            Optional<Appointment> found = appointmentService.findByNumber(number);
            if (found.isEmpty()) {
                ApiSupport.sendError(exchange, 404, "No appointment found with number " + number + ".");
                return;
            }
            ApiSupport.sendJson(exchange, 200, JsonWriter.writeObject(toMap(found.get())));
            return;
        }
        List<Map<String, Object>> all = new ArrayList<>();
        for (Appointment a : appointmentService.findAll()) {
            all.add(toMap(a));
        }
        ApiSupport.sendJson(exchange, 200, JsonWriter.writeArray(all));
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Map<String, String> form = ApiSupport.formBody(exchange);
        User user = ApiSupport.requireAuth(exchange, form);
        if (user == null) {
            ApiSupport.sendError(exchange, 401, "Please log in.");
            return;
        }
        try {
            LocalDate date = LocalDate.parse(form.get("date"));
            LocalTime time = LocalTime.parse(form.get("time"));
            double discount = form.containsKey("discountPercent") && !form.get("discountPercent").isBlank()
                    ? Double.parseDouble(form.get("discountPercent")) : 0.0;

            Appointment appointment = appointmentService.registerAppointment(
                    form.get("patientName"), form.get("address"), form.get("contactNumber"),
                    form.get("dentistId"), form.get("treatmentTypeId"), date, time, discount);

            ApiSupport.sendJson(exchange, 201, JsonWriter.writeObject(toMap(appointment)));
        } catch (IllegalArgumentException e) {
            ApiSupport.sendError(exchange, 400, e.getMessage());
        }
    }

    private Map<String, Object> toMap(Appointment a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("appointmentNumber", a.getAppointmentNumber());
        m.put("patientName", a.getPatient().getName());
        m.put("address", a.getPatient().getAddress());
        m.put("contactNumber", a.getPatient().getContactNumber());
        m.put("dentistId", a.getDentistId());
        m.put("treatmentTypeId", a.getTreatmentTypeId());
        m.put("date", a.getDate().toString());
        m.put("time", a.getTime().toString());
        m.put("discountPercent", a.getDiscountPercent());
        return m;
    }
}
