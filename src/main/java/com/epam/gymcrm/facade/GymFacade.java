package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
public class GymFacade {

    private final UserService userService;

    public GymFacade(UserService userService) {
        this.userService = userService;
    }

    public void createUser(@Valid User user) {
        userService.saveUser(user);
    }
}
