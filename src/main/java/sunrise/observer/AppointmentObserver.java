package sunrise.observer;

import sunrise.model.Appointment;

/**
 * Observer pattern: implementations react to appointment lifecycle events
 * without AppointmentService needing to know who is listening or how many
 * listeners there are.
 */
public interface AppointmentObserver {

    void onAppointmentRegistered(Appointment appointment);
}
