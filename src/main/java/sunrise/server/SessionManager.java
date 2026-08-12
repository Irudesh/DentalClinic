package sunrise.server;

import sunrise.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton pattern: one shared table of active session tokens, since the
 * HTTP server may be handling several client requests concurrently on
 * different threads (a ConcurrentHashMap is used for that reason).
 *
 * A deliberately simple token scheme is used - the frontend receives an
 * opaque token on login and sends it back on every subsequent request -
 * rather than a framework-managed session/cookie mechanism, in keeping
 * with the "no frameworks" constraint.
 */
public final class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public String createSession(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public User getUser(String token) {
        return token == null ? null : sessions.get(token);
    }

    public void invalidate(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }
}
