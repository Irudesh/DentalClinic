package sunrise.dao;

import sunrise.model.TreatmentType;

import java.util.List;
import java.util.Optional;

public interface TreatmentTypeDao {

    void save(TreatmentType treatmentType);

    Optional<TreatmentType> findById(String id);

    List<TreatmentType> findAll();

    void deleteById(String id);
}
