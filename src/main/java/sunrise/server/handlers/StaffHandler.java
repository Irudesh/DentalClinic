package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.Role;
import sunrise.model.User;
import sunrise.service.StaffService;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StaffHandler implements HttpHandler {

    private final StaffService staffService;

    public StaffHandler(StaffService staffService) {
        this.staffService = staffService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            Map<String, String> params = ApiSupport.queryParams(exchange);
            User user = ApiSupport.requireAuth(exchange, params);
            if (!ApiSupport.isAdmin(user)) {
                ApiSupport.sendError(exchange, 403, "Only Admin can view staff accounts.");
                return;
            }
            List<Map<String, Object>> all = new ArrayList<>();
            for (User u : staffService.listAll()) {
                all.add(toMap(u));
            }
            ApiSupport.sendJson(exchange, 200, JsonWriter.writeArray(all));
        } else if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> form = ApiSupport.formBody(exchange);
            User user = ApiSupport.requireAuth(exchange, form);
            if (!ApiSupport.isAdmin(user)) {
                ApiSupport.sendError(exchange, 403, "Only Admin can manage staff accounts.");
                return;
            }
            try {
                Role role = Role.valueOf(form.getOrDefault("role", "RECEPTIONIST").toUpperCase());
                User created = staffService.addStaff(form.get("username"), form.get("password"), role, form.get("fullName"));
                ApiSupport.sendJson(exchange, 201, JsonWriter.writeObject(toMap(created)));
            } catch (IllegalArgumentException e) {
                ApiSupport.sendError(exchange, 400, e.getMessage());
            }
        } else {
            ApiSupport.sendError(exchange, 405, "Unsupported method.");
        }
    }

    private Map<String, Object> toMap(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("username", u.getUsername());
        m.put("fullName", u.getFullName());
        m.put("role", u.getRole().name());
        return m;
    }
}
