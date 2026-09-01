package sunrise.service;

import sunrise.dao.UserDao;
import sunrise.model.User;
import sunrise.util.PasswordUtil;

import java.util.Optional;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        return userDao.findByUsername(username)
                .filter(user -> PasswordUtil.matches(password, user.getPasswordHash()));
    }
}
