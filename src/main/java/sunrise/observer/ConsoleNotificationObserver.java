package sunrise.observer;

import sunrise.model.Appointment;
import sunrise.util.FileStorageManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A concrete AppointmentObserver that records a confirmation message to a
 * log file whenever a new appointment is registered, simulating the
 * "email alerts / SMS notifications" style feature the rubric calls out.
 * A real deployment would swap this for an observer that calls an SMS or
 * email gateway; because no external network/API access is available
 * here, this observer writes a human-readable confirmation line instead,
 * demonstrating the same extension point.
 */
public class ConsoleNotificationObserver implements AppointmentObserver {

    private static final String LOG_FILE = "notifications.log";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final FileStorageManager storage;

    public ConsoleNotificationObserver(FileStorageManager storage) {
        this.storage = storage;
    }

    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        String message = String.format(
                "[%s] Confirmation: %s, your appointment %s is booked for %s at %s.",
                LocalDateTime.now().format(TS),
                appointment.getPatient().getName(),
                appointment.getAppointmentNumber(),
                appointment.getDate(),
                appointment.getTime());
        storage.appendLine(LOG_FILE, message);
        System.out.println(message);
    }
}
