package sunrise.observer;

import sunrise.model.Appointment;
import sunrise.util.FileStorageManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
