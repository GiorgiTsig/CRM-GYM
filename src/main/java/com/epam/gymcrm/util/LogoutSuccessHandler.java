package com.epam.gymcrm.util;

import com.epam.gymcrm.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(LogoutSuccessHandler.class);
    private final UserRepository userRepository;

    public LogoutSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();
            log.info("User logged out: {}", username);

            userRepository.getUsersByUsername(username).ifPresent(user -> {
                user.setLastLogout(Instant.now());
                userRepository.save(user);
            });
        }

        response.setStatus(HttpServletResponse.SC_OK);

    }
}
