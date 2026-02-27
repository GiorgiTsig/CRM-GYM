package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class UserService {

    private UserRepository userRepository;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUser(@Valid User user) {
        log.info("Creating user with firstName: {}, lastName: {}", user.getUsername(), user.getLastName());

        String password = passwordGenerator.generatePassword();
        user.setPassword(password);

        String username = usernameGenerator.generateUsername(user.getFirstName(), user.getLastName());
        user.setUsername(username);

        userRepository.save(user);

        log.info("user created successfully with username: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(String username) {
        log.info("Selecting user with id: {}", username);
        return userRepository.getUsersByUsername(username);
    }

    @Transactional
    public void updateUser(String username, String firsName, String lastname, String password, Boolean isActive) {
        log.info("Updating User with id: {}", username);

        User existingUser = userRepository.getUsersByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setFirstName(firsName);
        existingUser.setLastName(lastname);
        existingUser.setPassword(password);
        existingUser.setActive(isActive);

        userRepository.save(existingUser);

        log.info("User updated successfully with id: {}", username);
    }

    @Transactional
    public void deleteUser(String username) {
        log.info("Deleting user with username: {}", username);
        userRepository.deleteUserByUsername(username);
        log.info("user deleted successfully with username: {}", username);
    }
}
