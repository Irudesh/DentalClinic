package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import sunrise.model.Role;
import sunrise.model.User;
import sunrise.server.SessionManager;
import sunrise.util.FormParser;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Small shared helper (not a framework - just a few static utility
 * methods) used by every API handler to avoid repeating the same
 * boilerplate for reading query parameters, checking auth, and writing
 * JSON responses.
 */
final class ApiSupport {

    private ApiSupport() {
    }

    static Map<String, String> queryParams(HttpExchange exchange) {
        return FormParser.parse(exchange.getRequestURI().getRawQuery());
    }

    static Map<String, String> formBody(HttpExchange exchange) throws IOException {
        return FormParser.parseBody(exchange.getRequestBody());
    }

    static User requireAuth(HttpExchange exchange, Map<String, String> params) {
        String token = params.get("token");
        return SessionManager.getInstance().getUser(token);
    }

    static boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    static void sendError(HttpExchange exchange, int status, String message) throws IOException {
        sendJson(exchange, status, JsonWriter.writeError(message));
    }
}
