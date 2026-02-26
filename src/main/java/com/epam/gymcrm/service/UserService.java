package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.UserDaoImp;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class UserService {

    private UserDaoImp userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public void setUserDao(UserDaoImp userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Transactional
    public void saveUser(@Valid User user) {
        log.info("Creating user with firstName: {}, lastName: {}", user.getUsername(), user.getLastName());

        String password = passwordGenerator.generatePassword();
        user.setPassword(password);

        String username = usernameGenerator.generateUsername(user.getFirstName(), user.getLastName());
        user.setUsername(username);

        userDao.save(user);

        log.info("user created successfully with username: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public List<User> getAllUser() {
        log.info("Selecting all user");
        return userDao.getAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(String username) {
        log.info("Selecting user with id: {}", username);
        return userDao.get(username);
    }

    @Transactional
    public void updateUser(String username, String firsName, String lastname, String password, Boolean isActive) {
        log.info("Updating User with id: {}", username);

        User existingUser = userDao.get(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setFirstName(firsName);
        existingUser.setLastName(lastname);
        existingUser.setPassword(password);
        existingUser.setActive(isActive);

        userDao.update(existingUser);

        log.info("User updated successfully with id: {}", username);
    }

    @Transactional
    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        userDao.delete(id);
        log.info("user deleted successfully with id: {}", id);
    }
}
