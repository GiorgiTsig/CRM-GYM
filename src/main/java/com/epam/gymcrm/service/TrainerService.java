package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainerDaoImp;
import com.epam.gymcrm.domain.Trainer;
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
public class TrainerService {

    private TrainerDaoImp trainerDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private IdGenerator idGenerator;
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public void setTrainerDao(TrainerDaoImp trainerDao) {
        this.trainerDao = trainerDao;
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

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        log.info("Creating trainer with firstName: {}, lastName: {}, specialization: {}", firstName, lastName, specialization);
        
        validateInput(firstName, "First name");
        validateInput(lastName, "Last name");
        validateInput(specialization, "Specialization");

        String username = usernameGenerator.generateUsername(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        Long id = idGenerator.generateNextId(trainerDao.getAll());

        User user = new User(id, firstName, lastName, username, password, true);
        Trainer trainer = new Trainer(user, specialization);

        trainerDao.save(trainer);

        log.info("Trainer created successfully with id: {}, username: {}", id, username);
        return trainer;
    }

    public Optional<Trainer> selectTrainer(long id) {
        log.info("Selecting trainer with id: {}", id);
        return trainerDao.get(id);
    }

    public Map<Long, Trainer> selectAllTrainers() {
        log.info("Selecting all trainers");
        return trainerDao.getAll();
    }

    public Trainer updateTrainer(long id, String firstName, String lastName, String specialization, boolean isActive) {
        log.info("Updating trainer with id: {}", id);
        
        validateInput(firstName, "First name");
        validateInput(lastName, "Last name");
        validateInput(specialization, "Specialization");

        Optional<Trainer> existingTrainerOpt = trainerDao.get(id);
        if (existingTrainerOpt.isEmpty()) {
            log.error("Cannot update. Trainer with id {} not found", id);
            throw new EntityNotFoundException("Trainer with id " + id + " not found");
        }

        Trainer existingTrainer = existingTrainerOpt.get();
        
        User updatedUser = new User(id, firstName, lastName, existingTrainer.getUsername(), existingTrainer.getPassword(), isActive);

        Trainer updatedTrainer = new Trainer(updatedUser, specialization);

        trainerDao.update(updatedTrainer);

        log.info("Trainer updated successfully with id: {}", id);
        return updatedTrainer;
    }
    
    private void validateInput(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            log.error("Validation failed: {} cannot be null or empty", fieldName);
            throw new ValidationException(fieldName + " cannot be null or empty");
        }
    }
}
