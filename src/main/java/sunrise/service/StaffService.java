package sunrise.service;

import sunrise.dao.UserDao;
import sunrise.model.Role;
import sunrise.model.User;
import sunrise.util.PasswordUtil;

import java.util.List;

public class StaffService {

    private final UserDao userDao;

    public StaffService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User addStaff(String username, String password, Role role, String fullName) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        if (userDao.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        User user = new User(username.trim(), PasswordUtil.hash(password), role, fullName);
        userDao.save(user);
        return user;
    }

    public List<User> listAll() {
        return userDao.findAll();
    }

    public void remove(String username) {
        userDao.deleteByUsername(username);
    }
}
