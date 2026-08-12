package sunrise.dao.impl;

import sunrise.dao.AppointmentDao;
import sunrise.model.Appointment;
import sunrise.util.FileStorageManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileAppointmentDao implements AppointmentDao {

    private static final String FILE_NAME = "appointments.txt";

    private final FileStorageManager storage;

    public FileAppointmentDao(FileStorageManager storage) {
        this.storage = storage;
    }

    @Override
    public void save(Appointment appointment) {
        storage.appendLine(FILE_NAME, appointment.toDataLine());
    }

    @Override
    public Optional<Appointment> findByNumber(String appointmentNumber) {
        return findAll().stream()
                .filter(a -> a.getAppointmentNumber().equalsIgnoreCase(appointmentNumber))
                .findFirst();
    }

    @Override
    public List<Appointment> findAll() {
        return storage.readAllLines(FILE_NAME).stream()
                .map(Appointment::fromDataLine)
                .collect(Collectors.toList());
    }
}
