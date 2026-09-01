package sunrise.observer;

import sunrise.model.Appointment;

import java.util.ArrayList;
import java.util.List;

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
