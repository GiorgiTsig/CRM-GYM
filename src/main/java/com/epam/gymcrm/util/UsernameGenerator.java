package com.epam.gymcrm.util;

import com.epam.gymcrm.dao.UserDaoImp;
import com.epam.gymcrm.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsernameGenerator {

    private static final Logger log = LoggerFactory.getLogger(UsernameGenerator.class);

    private UserDaoImp userDao;

    @Autowired
    public void setUser(UserDaoImp userDao) {
        this.userDao = userDao;
    }

    /**
     * Generates a unique username based on first name and last name.
     * Format: firstName.lastName
     * If username exists, appends serial number: firstName.lastName1, firstName.lastName2, etc.
     *
     * @param firstName First name
     * @param lastName Last name
     * @return Unique username
     */
    public String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int serialNumber = 1;

        while (usernameExistsInUser(username)) {
            username = baseUsername + serialNumber;
            serialNumber++;
        }

        log.debug("Generated username: {}", username);
        return username;
    }

    private boolean usernameExistsInUser(String username) {
        List<User> allUser = userDao.getAll();
        return allUser.stream()
                .anyMatch(user -> username.equals(user.getUsername()));
    }
}
