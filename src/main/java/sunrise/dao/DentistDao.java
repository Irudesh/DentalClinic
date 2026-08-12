package sunrise.dao;

import sunrise.model.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistDao {

    void save(Dentist dentist);

    Optional<Dentist> findById(String id);

    List<Dentist> findAll();

    void deleteById(String id);
}
