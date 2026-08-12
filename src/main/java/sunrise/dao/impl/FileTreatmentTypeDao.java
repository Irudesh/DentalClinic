package sunrise.dao.impl;

import sunrise.dao.TreatmentTypeDao;
import sunrise.model.TreatmentType;
import sunrise.util.FileStorageManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileTreatmentTypeDao implements TreatmentTypeDao {

    private static final String FILE_NAME = "treatment_types.txt";

    private final FileStorageManager storage;

    public FileTreatmentTypeDao(FileStorageManager storage) {
        this.storage = storage;
    }

    @Override
    public void save(TreatmentType treatmentType) {
        List<TreatmentType> remaining = findAll().stream()
                .filter(t -> !t.getId().equalsIgnoreCase(treatmentType.getId()))
                .collect(Collectors.toList());
        remaining.add(treatmentType);
        persist(remaining);
    }

    @Override
    public Optional<TreatmentType> findById(String id) {
        return findAll().stream().filter(t -> t.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public List<TreatmentType> findAll() {
        return storage.readAllLines(FILE_NAME).stream()
                .map(TreatmentType::fromDataLine)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        List<TreatmentType> remaining = findAll().stream()
                .filter(t -> !t.getId().equalsIgnoreCase(id))
                .collect(Collectors.toList());
        persist(remaining);
    }

    private void persist(List<TreatmentType> items) {
        storage.rewriteAll(FILE_NAME, items.stream().map(TreatmentType::toDataLine).collect(Collectors.toList()));
    }
}
