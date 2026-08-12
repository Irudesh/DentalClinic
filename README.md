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

## Database setup (MySQL)

The system now uses MySQL as its persistence layer (see `db/schema.sql`),
designed and exported using MySQL Workbench's EER diagram tool. The
schema includes foreign keys, a `UNIQUE` constraint plus a `BEFORE INSERT`
trigger enforcing appointment business rules, a stored function
(`fn_calculate_total`) mirroring the Strategy-pattern discount
calculation, a stored procedure (`sp_revenue_summary`) for the Admin
reporting use case, and a reporting view (`vw_appointment_details`).

### 1. Create the schema

In MySQL Workbench (or the `mysql` command line):

```sql
CREATE DATABASE sunrise_dental;
```

Then run `db/schema.sql` against that new schema (Workbench: File → Run
SQL Script...).

### 2. Add the MySQL JDBC driver to your project

Download **MySQL Connector/J** (the `.jar` file) from
https://dev.mysql.com/downloads/connector/j/ and add it to your project's
build path:

- **IntelliJ IDEA:** File → Project Structure → Libraries → + → select the
  downloaded jar.
- **Eclipse:** right-click the project → Build Path → Configure Build Path
  → Libraries → Add External JARs... → select the downloaded jar.

This sandbox has no internet access, so the jar could not be downloaded
or bundled here - this step must be done on your own machine.

### 3. Configure the connection

Copy `src/main/resources/db.properties.example` to
`src/main/resources/db.properties` and fill in your own MySQL username/
password. `db.properties` is git-ignored so your password is never
committed.

### 4. Switch storage backend if needed

`Main.USE_DATABASE` (top of `Main.java`) controls whether the app uses
the MySQL DAOs or falls back to the original plain-text-file DAOs. Because
every DAO is accessed only through its interface, this is the only line
that needs to change to switch backends - nothing else in the codebase
depends on which storage mechanism is active. This was a deliberate
benefit of using the DAO/Repository pattern from the start.



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
- The MySQL Connector/J driver jar and a running MySQL server were not
  available in the sandbox this project was built in (no internet
  access), so the JDBC code, while carefully written against the
  documented JDBC API and manually checked, has not been run/tested by
  Claude against a live database. It must be tested by the student
  against their own MySQL instance before submission, and any errors
  reported back for fixing.
- No JDK (`javac`) was available in the sandbox either (only a JRE), so
  none of the Java source in this project has been compiler-verified by
  Claude. It should be opened in an IDE (IntelliJ/Eclipse) and compiled
  there; any errors should be reported back for fixing.
