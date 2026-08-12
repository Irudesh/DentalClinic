package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.server.SessionManager;

import java.io.IOException;
import java.util.Map;

public class LogoutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> form = ApiSupport.formBody(exchange);
        SessionManager.getInstance().invalidate(form.get("token"));
        ApiSupport.sendJson(exchange, 200, "{\"status\":\"logged out\"}");
    }
}
