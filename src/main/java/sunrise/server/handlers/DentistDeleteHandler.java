package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.User;
import sunrise.service.DentistService;

import java.io.IOException;
import java.util.Map;

public class DentistDeleteHandler implements HttpHandler {

    private final DentistService dentistService;

    public DentistDeleteHandler(DentistService dentistService) {
        this.dentistService = dentistService;
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
            ApiSupport.sendError(exchange, 403, "Only Admin can manage dentists.");
            return;
        }
        dentistService.remove(form.get("id"));
        ApiSupport.sendJson(exchange, 200, "{\"status\":\"deleted\"}");
    }
}
