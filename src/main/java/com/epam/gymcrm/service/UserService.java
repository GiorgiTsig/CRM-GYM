package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.repository.UserRepository;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class UserService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(String username) {
        log.info("Selecting user with id: {}", username);
        return userRepository.getUsersByUsername(username);
    }

    @Transactional
    public User updatePassword(@NotEmpty String username, @NotEmpty String newPassword) {
        log.info("Started Changing Password");
        User user = this.getUser(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return user;
    }
}
