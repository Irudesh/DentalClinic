package sunrise.service;

import sunrise.dao.DentistDao;
import sunrise.model.Dentist;

import java.util.List;
import java.util.UUID;

public class DentistService {

    private final DentistDao dentistDao;

    public DentistService(DentistDao dentistDao) {
        this.dentistDao = dentistDao;
    }

    public Dentist addDentist(String name, String specialization) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dentist name is required.");
        }
        String id = "D" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Dentist dentist = new Dentist(id, name.trim(), specialization == null ? "" : specialization.trim());
        dentistDao.save(dentist);
        return dentist;
    }

    public List<Dentist> listAll() {
        return dentistDao.findAll();
    }

    public void remove(String id) {
        dentistDao.deleteById(id);
    }
}
