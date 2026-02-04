package com.epam.gymcrm.util;

import com.epam.gymcrm.dao.TraineeDaoImp;
import com.epam.gymcrm.dao.TrainerDaoImp;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UsernameGenerator {

    private static final Logger log = LoggerFactory.getLogger(UsernameGenerator.class);

    private TraineeDaoImp traineeDao;
    private TrainerDaoImp trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDaoImp traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDaoImp trainerDao) {
        this.trainerDao = trainerDao;
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

        while (usernameExistsInTrainees(username) || usernameExistsInTrainers(username)) {
            username = baseUsername + serialNumber;
            serialNumber++;
        }

        log.debug("Generated username: {}", username);
        return username;
    }

    private boolean usernameExistsInTrainees(String username) {
        Map<Long, Trainee> allTrainees = traineeDao.getAll();
        return allTrainees.values().stream()
                .anyMatch(trainee -> username.equals(trainee.getUsername()));
    }

    private boolean usernameExistsInTrainers(String username) {
        Map<Long, Trainer> allTrainers = trainerDao.getAll();
        return allTrainers.values().stream()
                .anyMatch(trainer -> username.equals(trainer.getUsername()));
    }
}
