package sunrise.util;

import java.util.List;
import java.util.Map;

/**
 * A deliberately small JSON serialiser covering exactly what this
 * application's API responses need (strings, numbers, booleans, nested
 * objects and arrays of objects). Written by hand because the "no
 * frameworks" constraint rules out pulling in a library such as Gson.
 */
public final class JsonWriter {

    private JsonWriter() {
    }

    @SuppressWarnings("unchecked")
    public static String writeObject(Map<String, Object> fields) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(quote(entry.getKey())).append(":");
            appendValue(sb, entry.getValue());
        }
        sb.append("}");
        return sb.toString();
    }

    public static String writeArray(List<Map<String, Object>> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(writeObject(items.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String writeError(String message) {
        StringBuilder sb = new StringBuilder("{\"error\":");
        sb.append(quote(message)).append("}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void appendValue(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append(quote((String) value));
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Map) {
            sb.append(writeObject((Map<String, Object>) value));
        } else if (value instanceof List) {
            sb.append(writeArray((List<Map<String, Object>>) value));
        } else {
            sb.append(quote(value.toString()));
        }
    }

    private static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
