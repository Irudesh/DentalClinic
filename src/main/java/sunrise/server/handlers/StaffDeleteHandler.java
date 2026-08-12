package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.User;
import sunrise.service.StaffService;

import java.io.IOException;
import java.util.Map;

public class StaffDeleteHandler implements HttpHandler {

    private final StaffService staffService;

    public StaffDeleteHandler(StaffService staffService) {
        this.staffService = staffService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            ApiSupport.sendError(exchange, 405, "Only POST is supported.");
            return;
        }
        Map<String, String> form = ApiSupport.formBody(exchange);
        User user = ApiSupport.requireAuth(exchange, form);
        if (!ApiSupport.isAdmin(user)) {
            ApiSupport.sendError(exchange, 403, "Only Admin can manage staff accounts.");
            return;
        }
        String target = form.get("username");
        if (user.getUsername().equalsIgnoreCase(target)) {
            ApiSupport.sendError(exchange, 400, "You cannot delete your own account while logged in.");
            return;
        }
        staffService.remove(target);
        ApiSupport.sendJson(exchange, 200, "{\"status\":\"deleted\"}");
    }
}
