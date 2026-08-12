# Sunrise Dental Clinic - Appointment & Billing System

CIS6003 Advanced Programming - Coursework (WRIT1)

A distributed, menu-driven Java application for Sunrise Dental Clinic, built
with **pure JDK only** (no frameworks), a plain **HTML/CSS/JavaScript**
frontend, a hand-built HTTP web-service layer, and text-file persistence.

## Tech stack

- Java 21 (no external frameworks)
- `com.sun.net.httpserver.HttpServer` for the web-service layer
- Plain HTML5 / CSS3 / vanilla JavaScript frontend
- Text-file storage (pipe-delimited), accessed via a Singleton `FileStorageManager`
- JUnit 5 for unit tests

## Design patterns used

| Pattern | Class(es) | Purpose |
|---|---|---|
| Singleton | `FileStorageManager`, `SessionManager` | One shared, thread-safe access point to shared state |
| Factory | `AppointmentFactory` | Centralised, validated Appointment creation |
| DAO / Repository | `*Dao` interfaces + `dao.impl.*` | Decouples business logic from the storage mechanism |
| Observer | `AppointmentObserver`, `AppointmentEventPublisher`, `ConsoleNotificationObserver` | Notifies interested parties when an appointment is registered, without coupling the service to the notification mechanism |
| Strategy | `FeeCalculationStrategy`, `StandardFeeStrategy`, `DiscountFeeStrategy` | Selects the correct billing calculation at run time for the "Apply Discount" use case |

## Project layout

```
src/main/java/sunrise/
  model/      - domain classes (Appointment, Patient, Dentist, TreatmentType, Bill, User, Role)
  dao/        - DAO interfaces + dao/impl file-based implementations
  util/       - FileStorageManager (Singleton), IdGenerator, PasswordUtil, JsonWriter, FormParser
  factory/    - AppointmentFactory
  observer/   - Observer pattern classes
  service/    - business/orchestration layer (+ service/fee for Strategy pattern)
  server/     - ApiServer, SessionManager (Singleton), StaticFileHandler, server/handlers/*
  Main.java   - composition root / entry point

src/test/java/sunrise/   - JUnit 5 test classes
web/                      - static frontend (HTML/CSS/JS)
data/                     - runtime text-file data store (created automatically)
docs/                     - report and supporting documentation
```

## How to build and run

This project uses no build tool beyond `javac`/`java` (deliberately, to avoid
any dependency-management framework). You will need:

1. A JDK (21 recommended) with `javac` available - this sandbox only has a
   JRE, so compilation must be done on your own machine/IDE.
2. JUnit 5 (`junit-jupiter`) added to your test classpath - most IDEs
   (IntelliJ, Eclipse) can add this automatically when you run a test class.

### Compile and run from the command line

```bash
# from the project root
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
java -cp out sunrise.Main
```

Then open **http://localhost:8080** in a browser.

### Default accounts (seeded automatically on first run)

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Receptionist | `reception` | `reception123` |

### Running the tests

Open the project in IntelliJ IDEA or Eclipse, add JUnit 5 to the module's
test dependencies, and run the classes under `src/test/java`.

## Known limitations (documented honestly for the report)

- Password hashing uses a single static salt with SHA-256 rather than a
  per-user salt and a slower algorithm (bcrypt/PBKDF2); acceptable for a
  coursework demonstrator, flagged as a production concern.
- Authentication uses a simple in-memory bearer token rather than
  HttpOnly cookies, since no session-management framework is permitted.
- Data is stored as plain text files rather than a relational database,
  per the brief's explicit allowance for "appropriate data structures and
  text files"; this was the practical choice given the "no frameworks"
  constraint (a JDBC driver for an embedded database would need to be
  downloaded as a third-party library).
