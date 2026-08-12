package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.TreatmentType;
import sunrise.model.User;
import sunrise.service.TreatmentTypeService;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TreatmentTypeHandler implements HttpHandler {

    private final TreatmentTypeService treatmentTypeService;

    public TreatmentTypeHandler(TreatmentTypeService treatmentTypeService) {
        this.treatmentTypeService = treatmentTypeService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            Map<String, String> params = ApiSupport.queryParams(exchange);
            User user = ApiSupport.requireAuth(exchange, params);
            if (user == null) {
                ApiSupport.sendError(exchange, 401, "Please log in.");
                return;
            }
            List<Map<String, Object>> all = new ArrayList<>();
            for (TreatmentType t : treatmentTypeService.listAll()) {
                all.add(toMap(t));
            }
            ApiSupport.sendJson(exchange, 200, JsonWriter.writeArray(all));
        } else if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> form = ApiSupport.formBody(exchange);
            User user = ApiSupport.requireAuth(exchange, form);
            if (!ApiSupport.isAdmin(user)) {
                ApiSupport.sendError(exchange, 403, "Only Admin can manage treatment types.");
                return;
            }
            try {
                double fee = Double.parseDouble(form.get("fee"));
                TreatmentType created = treatmentTypeService.addTreatmentType(form.get("name"), fee);
                ApiSupport.sendJson(exchange, 201, JsonWriter.writeObject(toMap(created)));
            } catch (NumberFormatException e) {
                ApiSupport.sendError(exchange, 400, "Fee must be a valid number.");
            } catch (IllegalArgumentException e) {
                ApiSupport.sendError(exchange, 400, e.getMessage());
            }
        } else {
            ApiSupport.sendError(exchange, 405, "Unsupported method.");
        }
    }

    private Map<String, Object> toMap(TreatmentType t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", t.getId());
        m.put("name", t.getName());
        m.put("fee", t.getFee());
        return m;
    }
}
