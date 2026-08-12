package sunrise.observer;

import sunrise.model.Appointment;

import java.util.ArrayList;
import java.util.List;

/**
 * The "subject" half of the Observer pattern. AppointmentService calls
 * publish(...) once, and every registered observer is notified in turn -
 * new notification channels (e.g. a future email or SMS observer) can be
 * added later without changing AppointmentService at all.
 */
public class AppointmentEventPublisher {

    private final List<AppointmentObserver> observers = new ArrayList<>();

    public void subscribe(AppointmentObserver observer) {
        observers.add(observer);
    }

    public void publish(Appointment appointment) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentRegistered(appointment);
        }
    }
}
