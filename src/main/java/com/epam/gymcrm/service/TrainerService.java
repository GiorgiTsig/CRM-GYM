package com.epam.gymcrm.service;

import com.epam.gymcrm.repository.TrainingTypeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.util.Authentication;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class TrainerService {

    private TrainingTypeRepository trainingTypeRepository;
    private TrainerRepository trainerRepository;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private Authentication authentication;
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Transactional
    public void createTrainerProfile(@Valid User user, @Valid Trainer trainer, @NotBlank String type) {
        log.info("Creating trainer profile for {} {}", user.getFirstName(), user.getLastName());

        String password = passwordGenerator.generatePassword();
        user.setPassword(password);

        String username = usernameGenerator.generateUsername(user.getFirstName(), user.getLastName());
        user.setUsername(username);

        trainer.setUser(user);
        TrainingType trainingType = trainingType(type);
        trainer.setTrainingType(trainingType);
        user.setTrainer(trainer);

        trainerRepository.save(trainer);

        log.info("Trainer profile created with username: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainer(@NotBlank String username) {
        return trainerRepository.getTrainerByUserUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean authenticateTrainer(@NotBlank String username, @NotBlank String password) {
        if (!authentication.auth(username, password)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
        return trainerRepository.getTrainerByUserUsername(username).isPresent();
    }

    @Transactional
    public void updateTrainerProfile(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String specialization
    ) {
        log.info("Checking user with Username/Password");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
        }

        Trainer trainer = trainerRepository.getTrainerByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        User user = trainer.getUser();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainer.setTrainingType(trainingType(specialization));

        trainerRepository.save(trainer);
        log.info("Trainer profile updated successfully for username: {}", username);
    }

    @Transactional
    public void deleteTrainer(UUID id, @NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Password");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
        }

        log.info("Deleting Trainer with id: {}", id);
        trainerRepository.deleteTrainerById(id);
        log.info("Trainer deleted successfully with id: {}", id);
    }

    @Transactional
    public void changeTrainerPassword(@NotBlank String username, @NotBlank String password, @NotBlank String newPassword) {
        log.info("Checking user with Username/Password");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
        }

        Trainer trainer = trainerRepository.getTrainerByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        trainer.getUser().setPassword(newPassword);
        trainerRepository.save(trainer);
        log.info("Trainer password changed successfully with username: {}", username);
    }

    @Transactional
    public void activateTrainer(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Password");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
        }

        Trainer trainer = trainerRepository.getTrainerByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        if (trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer profile is already active");
        }

        trainer.getUser().setActive(true);
        trainerRepository.save(trainer);
        log.info("Trainer activated successfully with username: {}", username);
    }

    @Transactional
    public void deactivateTrainer(@NotBlank String username, @NotBlank String password) {
        log.info("Checking user with Username/Password");
        if (!authenticateTrainer(username, password)) {
            log.error("Username and Password are not correct: {}", username);
        }

        Trainer trainer = trainerRepository.getTrainerByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        if (!trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer profile is already inactive");
        }

        trainer.getUser().setActive(false);
        trainerRepository.save(trainer);
        log.info("Trainer deactivated successfully with username: {}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        log.info("Selecting all trainers");
        return trainerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TrainingType trainingType(@NonNull String type) {
        return trainingTypeRepository.findTrainingTypeByTrainingTypeName(type);
    }
}
