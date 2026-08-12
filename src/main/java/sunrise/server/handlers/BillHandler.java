package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.Bill;
import sunrise.model.User;
import sunrise.service.BillingService;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class BillHandler implements HttpHandler {

    private final BillingService billingService;

    public BillHandler(BillingService billingService) {
        this.billingService = billingService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            ApiSupport.sendError(exchange, 405, "Only GET is supported.");
            return;
        }
        Map<String, String> params = ApiSupport.queryParams(exchange);
        User user = ApiSupport.requireAuth(exchange, params);
        if (user == null) {
            ApiSupport.sendError(exchange, 401, "Please log in.");
            return;
        }
        String number = params.get("number");
        if (number == null || number.isBlank()) {
            ApiSupport.sendError(exchange, 400, "An appointment number is required.");
            return;
        }
        try {
            Optional<Bill> bill = billingService.generateBill(number);
            if (bill.isEmpty()) {
                ApiSupport.sendError(exchange, 404, "No appointment found with number " + number + ".");
                return;
            }
            Map<String, Object> m = new LinkedHashMap<>();
            Bill b = bill.get();
            m.put("appointmentNumber", b.getAppointmentNumber());
            m.put("patientName", b.getPatientName());
            m.put("dentistName", b.getDentistName());
            m.put("treatmentName", b.getTreatmentName());
            m.put("baseFee", b.getBaseFee());
            m.put("discountPercent", b.getDiscountPercent());
            m.put("discountAmount", b.getDiscountAmount());
            m.put("totalAmount", b.getTotalAmount());
            m.put("generatedAt", b.getGeneratedAt().toString());
            ApiSupport.sendJson(exchange, 200, JsonWriter.writeObject(m));
        } catch (IllegalStateException e) {
            ApiSupport.sendError(exchange, 409, e.getMessage());
        }
    }
}
