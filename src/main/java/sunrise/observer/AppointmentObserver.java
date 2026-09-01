package sunrise.observer;

import sunrise.model.Appointment;

public interface AppointmentObserver {

    void onAppointmentRegistered(Appointment appointment);
}
