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
import jakarta.validation.constraints.NotNull;
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
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
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
    public Trainer createTrainerProfile(@Valid User user, @Valid Trainer trainer, @NotBlank String type) {
        log.info("Creating trainer profile for {} {}", user.getFirstName(), user.getLastName());

        String password = passwordGenerator.generatePassword();
        user.setPassword(password);

        String username = usernameGenerator.generateUsername(user.getFirstName(), user.getLastName());
        user.setUsername(username);
        user.setActive(true);

        trainer.setUser(user);
        TrainingType trainingType = trainingType(type);
        trainer.setTrainingType(trainingType);
        user.setTrainer(trainer);

        trainerRepository.save(trainer);

        log.info("Trainer profile created with username: {}", user.getUsername());
        return trainer;
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainer(@NotBlank String username) {
        return trainerRepository.getTrainerByUserUsername(username);
    }

    @Transactional
    public Trainer updateTrainerProfile(
            @NotBlank String username,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull boolean isActive
    ) {
        log.info("Checking user with Username/Password");

        Trainer trainer = trainerRepository.getTrainerByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        User user = trainer.getUser();

        user.setActive(isActive);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        trainerRepository.save(trainer);
        log.info("Trainer profile updated successfully for username: {}", username);
        return trainer;
    }

    @Transactional
    public void deleteTrainer(UUID id) {
        log.info("Checking user with Username/Password");

        log.info("Deleting Trainer with id: {}", id);
        trainerRepository.deleteTrainerById(id);
        log.info("Trainer deleted successfully with id: {}", id);
    }

    @Transactional
    public void activateTrainer(@NotBlank String username) {
        log.info("Checking user with Username/Password");

        Trainer trainer = trainerRepository.getTrainerByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        if (trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer profile is already active");
        }

        trainer.getUser().setActive(true);
        trainerRepository.save(trainer);
        log.info("Trainer activated successfully with username: {}", username);
    }

    @Transactional
    public void deactivateTrainer(@NotBlank String username) {
        log.info("Checking user with Username/Password");

        Trainer trainer = trainerRepository.getTrainerByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        if (!trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer profile is already inactive");
        }

        trainer.getUser().setActive(false);
        trainerRepository.save(trainer);
        log.info("Trainer deactivated successfully with username: {}", username);
    }


    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainersForTrainee(@NotBlank String username) {
        return trainerRepository.findUnassignedTrainersByTraineeUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        log.info("Selecting all trainers");
        return trainerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Set<Trainer> getAllTrainersUserUsername(Set<String> users) {
        return trainerRepository.findAllByUserUsernameIn(users);
    }

    @Transactional(readOnly = true)
    public TrainingType trainingType(@NonNull String type) {
        return trainingTypeRepository.findTrainingTypeByTrainingTypeName(type);
    }
}
