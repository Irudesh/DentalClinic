package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.User;
import sunrise.server.SessionManager;
import sunrise.service.AuthService;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LoginHandler implements HttpHandler {

    private final AuthService authService;

    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            ApiSupport.sendError(exchange, 405, "Only POST is supported.");
            return;
        }
        Map<String, String> form = ApiSupport.formBody(exchange);
        Optional<User> user = authService.login(form.get("username"), form.get("password"));
        if (user.isEmpty()) {
            ApiSupport.sendError(exchange, 401, "Invalid username or password.");
            return;
        }
        String token = SessionManager.getInstance().createSession(user.get());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("token", token);
        body.put("username", user.get().getUsername());
        body.put("fullName", user.get().getFullName());
        body.put("role", user.get().getRole().name());
        ApiSupport.sendJson(exchange, 200, JsonWriter.writeObject(body));
    }
}
