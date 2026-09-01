package sunrise.dao.impl;

import sunrise.dao.DentistDao;
import sunrise.model.Dentist;
import sunrise.util.FileStorageManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileDentistDao implements DentistDao {

    private static final String FILE_NAME = "dentists.txt";

    private final FileStorageManager storage;

    public FileDentistDao(FileStorageManager storage) {
        this.storage = storage;
    }

    @Override
    public void save(Dentist dentist) {

        List<Dentist> remaining = findAll().stream()
                .filter(d -> !d.getId().equalsIgnoreCase(dentist.getId()))
                .collect(Collectors.toList());
        remaining.add(dentist);
        persist(remaining);
    }

    @Override
    public Optional<Dentist> findById(String id) {
        return findAll().stream().filter(d -> d.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public List<Dentist> findAll() {
        return storage.readAllLines(FILE_NAME).stream()
                .map(Dentist::fromDataLine)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        List<Dentist> remaining = findAll().stream()
                .filter(d -> !d.getId().equalsIgnoreCase(id))
                .collect(Collectors.toList());
        persist(remaining);
    }

    private void persist(List<Dentist> dentists) {
        storage.rewriteAll(FILE_NAME, dentists.stream().map(Dentist::toDataLine).collect(Collectors.toList()));
    }
}
