package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDaoImp;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.exception.ValidationException;
import com.epam.gymcrm.util.IdGenerator;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TraineeService {

    private TraineeDaoImp traineeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private IdGenerator idGenerator;
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    @Autowired
    public void setTraineeDao(TraineeDaoImp traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Autowired
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Trainee createTrainee(String firstName, String lastName, String dateOfBirth, String address) {
        log.info("Creating trainee with firstName: {}, lastName: {}", firstName, lastName);
        
        validateInput(firstName, "First name");
        validateInput(lastName, "Last name");

        String username = usernameGenerator.generateUsername(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        Long id = idGenerator.generateNextId(traineeDao.getAll());

        User user = new User(id, firstName, lastName, username, password, true);
        Trainee trainee = new Trainee(dateOfBirth, address, user);

        traineeDao.save(trainee);

        log.info("Trainee created successfully with id: {}, username: {}", id, username);
        return trainee;
    }

    public Optional<Trainee> selectTrainee(long id) {
        log.info("Selecting trainee with id: {}", id);
        return traineeDao.get(id);
    }

    public Map<Long, Trainee> selectAllTrainees() {
        log.info("Selecting all trainees");
        return traineeDao.getAll();
    }

    public Trainee updateTrainee(long id, String firstName, String lastName, String dateOfBirth, String address, boolean isActive) {
        log.info("Updating trainee with id: {}", id);
        
        validateInput(firstName, "First name");
        validateInput(lastName, "Last name");

        Optional<Trainee> existingTraineeOpt = traineeDao.get(id);
        if (existingTraineeOpt.isEmpty()) {
            log.error("Cannot update. Trainee with id {} not found", id);
            throw new EntityNotFoundException("Trainee with id " + id + " not found");
        }

        Trainee existingTrainee = existingTraineeOpt.get();
        
        User updatedUser = new User(id, firstName, lastName, existingTrainee.getUsername(), existingTrainee.getPassword(), isActive);

        Trainee updatedTrainee = new Trainee(dateOfBirth, address, updatedUser);

        traineeDao.update(updatedTrainee);

        log.info("Trainee updated successfully with id: {}", id);
        return updatedTrainee;
    }

    public void deleteTrainee(long id) {
        log.info("Deleting trainee with id: {}", id);
        traineeDao.delete(id);
        log.info("Trainee deleted successfully with id: {}", id);
    }
    
    private void validateInput(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            log.error("Validation failed: {} cannot be null or empty", fieldName);
            throw new ValidationException(fieldName + " cannot be null or empty");
        }
    }
}
