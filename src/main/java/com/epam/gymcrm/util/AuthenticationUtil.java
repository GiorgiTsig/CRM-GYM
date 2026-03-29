package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationUtil {

    private UserService userService;
    private MeterRegistry meterRegistry;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Boolean auth(String username, String password) {
        User getUser = userService.getUser(username).orElseThrow(() -> {
            meterRegistry.counter("crm_auth_attempts_total", "result", "failure").increment();
            return new AuthenticationFailedException("Invalid credentials");
        });

        if(!getUser.getPassword().equals(password)) {
            meterRegistry.counter("crm_auth_attempts_total", "result", "failure").increment();
            throw new AuthenticationFailedException("Invalid credentials");
        }

        meterRegistry.counter("crm_auth_attempts_total", "result", "success").increment();
        return getUser.getPassword().equals(password);
    }
}
