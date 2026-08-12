package sunrise.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses application/x-www-form-urlencoded bodies (used for POST requests
 * from the plain HTML/JS frontend) and URL query strings (used for GET
 * requests), both without any external dependency.
 */
public final class FormParser {

    private FormParser() {
    }

    public static Map<String, String> parse(String encoded) {
        Map<String, String> result = new HashMap<>();
        if (encoded == null || encoded.isBlank()) {
            return result;
        }
        for (String pair : encoded.split("&")) {
            if (pair.isBlank()) {
                continue;
            }
            int eq = pair.indexOf('=');
            String key = eq >= 0 ? pair.substring(0, eq) : pair;
            String value = eq >= 0 ? pair.substring(eq + 1) : "";
            result.put(decode(key), decode(value));
        }
        return result;
    }

    public static Map<String, String> parseBody(InputStream body) throws IOException {
        return parse(new String(body.readAllBytes(), StandardCharsets.UTF_8));
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
