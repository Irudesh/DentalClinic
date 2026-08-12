package sunrise.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sunrise.model.User;
import sunrise.service.ReportService;
import sunrise.util.JsonWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportHandler implements HttpHandler {

    private final ReportService reportService;

    public ReportHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            ApiSupport.sendError(exchange, 405, "Only GET is supported.");
            return;
        }
        Map<String, String> params = ApiSupport.queryParams(exchange);
        User user = ApiSupport.requireAuth(exchange, params);
        if (!ApiSupport.isAdmin(user)) {
            ApiSupport.sendError(exchange, 403, "Only Admin can view reports.");
            return;
        }
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalAppointments", reportService.totalAppointments());
        summary.put("estimatedTotalRevenue", reportService.estimatedTotalRevenue());

        Map<String, Object> perDentist = new LinkedHashMap<>();
        reportService.appointmentsPerDentist().forEach((id, count) -> perDentist.put(id, count));
        summary.put("appointmentsPerDentist", perDentist);

        ApiSupport.sendJson(exchange, 200, JsonWriter.writeObject(summary));
    }
}
