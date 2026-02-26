package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDaoImp;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.util.Authentication;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.*;

@Service
@Validated
public class TraineeService {

    private TraineeDaoImp traineeDao;
    private UserService  userService;
    private Authentication authentication;
    private TrainerService trainerService;
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTraineeDao(TraineeDaoImp traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerService(TrainerService trainerService) {this.trainerService = trainerService;}

    @Autowired
    public void setAuthentication(Authentication authentication) {this.authentication = authentication;}

    @Transactional
    public void createTrainee(@Valid Trainee trainee, @NotBlank String username, @NotBlank String trainerUsername) {
        User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException("Username doesn't exists"));
        Trainer trainer = trainerService.findTrainerByUsername(trainerUsername).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exists"));

        log.info("Creating trainee with firstName: {}", user.getFirstName());

        trainee.setUser(user);
        traineeDao.save(trainee);

        trainee.getTrainers().add(trainer);
        trainer.getTrainees().add(trainee);

        log.info("Trainee created successfully with id: {}, username: {}", user.getId(), user.getUsername());
    }

    @Transactional
    public void createTraineeProfile(@Valid User user, @Valid Trainee trainee, @NotBlank String trainerUsernames) {
        log.info("Creating trainee profile for {} {}", user.getFirstName(), user.getLastName());

        userService.saveUser(user);
        trainee.setUser(user);
        user.setTrainee(trainee);

        if (trainerUsernames != null && !trainerUsernames.isEmpty()) {
            Trainer trainer = trainerService.findTrainerByUsername(trainerUsernames).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exists"));
            trainee.getTrainers().add(trainer);
            trainer.getTrainees().add(trainee);
        }

        traineeDao.save(trainee);
        log.info("Trainee profile created with username: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> getTrainee(@NotBlank String username) {
        return traineeDao.get(username);
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> findTraineeByUsername(@NotBlank String username) {
        log.info("Selecting trainee with id: {}", username);
        return traineeDao.get(username);
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> findTraineeById(@NotNull UUID id) {
        log.info("Selecting trainee with UUID: {}", id);
        return traineeDao.getById(id);
    }

    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        log.info("Selecting all trainees");
        return traineeDao.getAll();
    }

    @Transactional(readOnly = true)
    public boolean authenticateTrainee(@NotBlank String username, @NotBlank String password) {
        if (!authentication.auth(username, password)) {
            return false;
        }
        return traineeDao.get(username).isPresent();
    }

    @Transactional
    public void updateTraineeTrainers(
            @NotBlank String username, @NotBlank String password,
            @NotBlank String trainerUsername
    ) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainee(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainee trainee = traineeDao.get(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));;
        Trainer trainer = trainerService.findTrainerByUsername(trainerUsername).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exists"));

        for (Trainer oldTrainer : new HashSet<>(trainee.getTrainers())) {
            oldTrainer.getTrainees().remove(trainee);
            trainee.getTrainers().remove(oldTrainer);
        }

        trainee.getTrainers().add(trainer);
        trainer.getTrainees().add(trainee);

        traineeDao.update(trainee);

        log.info("Trainee updated successfully with id: {}", trainee.getId());
    }

    @Transactional
    public void updateTraineeProfile(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull  LocalDate dateOfBirth,
            @NotBlank String address
    ) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainee(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainee trainee = traineeDao.get(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));
        User user = trainee.getUser();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        traineeDao.update(trainee);
        log.info("Trainee profile updated successfully with username: {}", username);
    }

    @Transactional
    public void changeTraineePassword(@NotBlank String username, @NotBlank String password, @NotBlank String newPassword) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainee(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainee trainee = traineeDao.get(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));
        trainee.getUser().setPassword(newPassword);
        traineeDao.update(trainee);
        log.info("Trainee password changed successfully with username: {}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainersForTrainee(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainee(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainee trainee = traineeDao.get(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));
        Set<Trainer> assigned = new HashSet<>(trainee.getTrainers());

        return trainerService.getAllTrainers().stream()
                .filter(trainer -> !assigned.contains(trainer))
                .toList();
    }

    @Transactional
    public void deleteTrainee(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainee(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        traineeDao.delete(username);
        log.info("Trainee deleted successfully with username: {}", username);
    }

    @Transactional
    public void activateTrainee(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainee(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainee trainee = traineeDao.get(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));
        if (trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee profile is already active");
        }

        trainee.getUser().setActive(true);
        traineeDao.update(trainee);
        log.info("Trainee activated successfully with username: {}", username);
    }

    @Transactional
    public void deactivateTrainee(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Passowrd");
        if (!authenticateTrainee(username, password)) {
            log.error("Username and Password are not correct: {}", username);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        Trainee trainee = traineeDao.get(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exists"));
        if (!trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee profile is already inactive");
        }

        trainee.getUser().setActive(false);
        traineeDao.update(trainee);
        log.info("Trainee deactivated successfully with username: {}", username);
    }
}
