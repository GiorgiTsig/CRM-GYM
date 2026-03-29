package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_MINUTES = 5;


    private final UserService userService;
    private final UserRepository userRepository;

    public AuthenticationFailureListener(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        log.info("Invalid credentials attempt received");
        String username = event.getAuthentication().getName();
        User user = userService.getUser(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        int previousAttempts = user.getFailedLoginAttempts();
        int attempts = previousAttempts + 1;
        user.setFailedLoginAttempts(attempts);
        log.info("User {} failed login attempt count updated: {} -> {}", username, previousAttempts, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            LocalDateTime lockUntilTime = LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES);
            user.setLockUntil(lockUntilTime);
            log.warn("User {} has been locked out due to {} failed attempts. Account locked until: {}", 
                    username, attempts, lockUntilTime);
        }

        userRepository.save(user);
        log.debug("User {} authentication failure record saved to database", username);
    }
}
