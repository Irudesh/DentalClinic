package sunrise.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public final class DatabaseConnectionManager {

    private static final DatabaseConnectionManager INSTANCE = new DatabaseConnectionManager();

    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnectionManager() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "db.properties not found on the classpath. Copy db.properties.example to "
                                + "db.properties and fill in your MySQL connection details.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read db.properties", e);
        }

        this.url = required(props, "db.url");
        this.username = required(props, "db.username");
        this.password = props.getProperty("db.password", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "MySQL Connector/J was not found on the classpath. Download it from "
                            + "https://dev.mysql.com/downloads/connector/j/ and add the jar to your "
                            + "project's build path / module dependencies.", e);
        }
    }

    public static DatabaseConnectionManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("db.properties is missing required key: " + key);
        }
        return value;
    }
}
