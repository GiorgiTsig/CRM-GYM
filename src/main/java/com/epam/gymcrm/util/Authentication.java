package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class Authentication {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Boolean auth(String username, String password) {
        User getUser = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return getUser.getPassword().equals(password);
    }
}
