package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainerDaoImp;
import com.epam.gymcrm.dao.TrainingTypeDao;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.util.Authentication;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class TrainerService {

    private TrainerDaoImp trainerDao;
    private UserService  userService;
    private Authentication authentication;
    private TrainingTypeDao trainingTypeDao;
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {this.trainingTypeDao = trainingTypeDao;}

    @Autowired
    public void setTrainerDao(TrainerDaoImp trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthentication(Authentication authentication) {this.authentication = authentication;}

    @Transactional
    public void createTrainer(@Valid Trainer trainer, @NotBlank String username, @NotBlank String type) {
        User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("Username doesn't exists"));
        log.info("Creating trainer with specialization: {}", type);

        TrainingType trainingType = trainingType(type);

        trainer.setTrainingType(trainingType);
        trainer.setUser(user);
        user.setTrainer(trainer);

        trainerDao.save(trainer);

        log.info("Trainee updated successfully with id: {}", trainer.getId());
    }

    @Transactional
    public void createTrainerProfile(@Valid User user, @Valid Trainer trainer, @NotBlank String type) {
        log.info("Creating trainer profile for {} {}", user.getFirstName(), user.getLastName());

        userService.saveUser(user);

        TrainingType trainingType = trainingType(type);
        trainer.setTrainingType(trainingType);
        trainer.setUser(user);
        user.setTrainer(trainer);

        trainerDao.save(trainer);

        log.info("Trainer profile created with username: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainer(@NotBlank String username) {
        return trainerDao.get(username);
    }

    @Transactional(readOnly = true)
    Optional<Trainer> findTrainerByUsername(@NotBlank String username) {
        log.info("Selecting trainer with id: {}", username);
        return trainerDao.get(username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        log.info("Selecting all trainers");
        return trainerDao.getAll();
    }

    @Transactional(readOnly = true)
    public boolean authenticateTrainer(@NotBlank String username, @NotBlank String password) {
        if (!authentication.auth(username, password)) {
            return false;
        }
        return trainerDao.get(username).isPresent();
    }

    @Transactional
    public void updateTrainer(@NotBlank String username, @NotBlank String password, @NotBlank String type) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainer trainer = trainerDao.get(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));;

        TrainingType trainingType = trainingType(type);
        trainer.setTrainingType(trainingType);
        trainerDao.update(trainer);
    }

    @Transactional
    public void updateTrainerProfile(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String specialization
    ) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainer trainer = trainerDao.get(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exists"));
        User user = trainer.getUser();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainer.setTrainingType(trainingType(specialization));

        trainerDao.update(trainer);
        log.info("Trainer profile updated successfully for username: {}", username);
    }

    @Transactional
    public void deleteTrainer(UUID id, @NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        log.info("Deleting Trainer with id: {}", id);
        trainerDao.delete(id);
        log.info("Trainer deleted successfully with id: {}", id);
    }

    @Transactional
    public void changeTrainerPassword(@NotBlank String username, @NotBlank String password, @NotBlank String newPassword) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainer trainer = trainerDao.get(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exists"));
        trainer.getUser().setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Trainer password changed successfully with username: {}", username);
    }

    @Transactional
    public void activateTrainer(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainer trainer = trainerDao.get(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exists"));
        if (trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer profile is already active");
        }

        trainer.getUser().setActive(true);
        trainerDao.update(trainer);
        log.info("Trainer activated successfully with username: {}", username);
    }

    @Transactional
    public void deactivateTrainer(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainer trainer = trainerDao.get(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exists"));
        if (!trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer profile is already inactive");
        }

        trainer.getUser().setActive(false);
        trainerDao.update(trainer);
        log.info("Trainer deactivated successfully with username: {}", username);
    }

    public TrainingType trainingType(String type) {
        List<TrainingType> getAllTrainingType = trainingTypeDao.getAll();

        return getAllTrainingType.stream()
                .filter(t -> Objects.equals(t.getTrainingTypeName(), type))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Training type not found"));
    }
}
