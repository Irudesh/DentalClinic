package sunrise.service;

import sunrise.dao.TreatmentTypeDao;
import sunrise.model.TreatmentType;

import java.util.List;
import java.util.UUID;

public class TreatmentTypeService {

    private final TreatmentTypeDao treatmentTypeDao;

    public TreatmentTypeService(TreatmentTypeDao treatmentTypeDao) {
        this.treatmentTypeDao = treatmentTypeDao;
    }

    public TreatmentType addTreatmentType(String name, double fee) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Treatment name is required.");
        }
        if (fee < 0) {
            throw new IllegalArgumentException("Fee cannot be negative.");
        }
        String id = "T" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        TreatmentType type = new TreatmentType(id, name.trim(), fee);
        treatmentTypeDao.save(type);
        return type;
    }

    public List<TreatmentType> listAll() {
        return treatmentTypeDao.findAll();
    }

    public void remove(String id) {
        treatmentTypeDao.deleteById(id);
    }
}
