package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.Dentist;
import sunrise.model.User;
import sunrise.service.DentistService;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DentistHandler implements HttpHandler {

    private final DentistService dentistService;

    public DentistHandler(DentistService dentistService) {
        this.dentistService = dentistService;
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
            for (Dentist d : dentistService.listAll()) {
                all.add(toMap(d));
            }
            ApiSupport.sendJson(exchange, 200, JsonWriter.writeArray(all));
        } else if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> form = ApiSupport.formBody(exchange);
            User user = ApiSupport.requireAuth(exchange, form);
            if (!ApiSupport.isAdmin(user)) {
                ApiSupport.sendError(exchange, 403, "Only Admin can manage dentists.");
                return;
            }
            try {
                Dentist created = dentistService.addDentist(form.get("name"), form.get("specialization"));
                ApiSupport.sendJson(exchange, 201, JsonWriter.writeObject(toMap(created)));
            } catch (IllegalArgumentException e) {
                ApiSupport.sendError(exchange, 400, e.getMessage());
            }
        } else {
            ApiSupport.sendError(exchange, 405, "Unsupported method.");
        }
    }

    private Map<String, Object> toMap(Dentist d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", d.getId());
        m.put("name", d.getName());
        m.put("specialization", d.getSpecialization());
        return m;
    }
}
