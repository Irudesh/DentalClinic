package sunrise.dao.impl;

import sunrise.dao.UserDao;
import sunrise.model.User;
import sunrise.util.FileStorageManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileUserDao implements UserDao {

    private static final String FILE_NAME = "users.txt";

    private final FileStorageManager storage;

    public FileUserDao(FileStorageManager storage) {
        this.storage = storage;
    }

    @Override
    public void save(User user) {
        List<User> remaining = findAll().stream()
                .filter(u -> !u.getUsername().equalsIgnoreCase(user.getUsername()))
                .collect(Collectors.toList());
        remaining.add(user);
        persist(remaining);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override
    public List<User> findAll() {
        return storage.readAllLines(FILE_NAME).stream()
                .map(User::fromDataLine)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUsername(String username) {
        List<User> remaining = findAll().stream()
                .filter(u -> !u.getUsername().equalsIgnoreCase(username))
                .collect(Collectors.toList());
        persist(remaining);
    }

    private void persist(List<User> users) {
        storage.rewriteAll(FILE_NAME, users.stream().map(User::toDataLine).collect(Collectors.toList()));
    }
}
