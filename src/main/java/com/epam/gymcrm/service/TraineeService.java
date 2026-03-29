package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class TraineeService {

    private TraineeRepository traineeRepository;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;
    private TrainerService trainerService;
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
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
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Trainee createTraineeProfile(@Valid Trainee trainee) {
        log.info("Creating trainee profile for {} {}", trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        String password = passwordGenerator.generatePassword();
        trainee.getUser().setPassword(passwordEncoder.encode(password));

        String username = usernameGenerator.generateUsername(trainee.getUser().getFirstName(), trainee.getUser().getLastName());
        trainee.getUser().setUsername(username);
        trainee.getUser().setActive(true);

        traineeRepository.save(trainee);
        log.info("Trainee profile created with username: {}, password: {}", trainee.getUser().getUsername(), password);

        return trainee;
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> getTrainee(@NotBlank String username) {
        return traineeRepository.getTraineeByUserUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> findTraineeByUsername(@NotBlank String username) {
        log.info("Selecting trainee with id: {}", username);
        return traineeRepository.getTraineeByUserUsername(username);
    }

    @Transactional
    public List<Trainer> updateTraineeTrainers(
            @NotBlank String username,
            Set<@NotNull String> trainerUsernames
    ) {

        Trainee trainee = traineeRepository.getTraineeByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));

        //Current trainers are those stored in the DB. The new trainer is provided by the user, and the unmatched old trainer will be deleted.
        var assignedTrainerUsernames = trainee.getTrainers().stream().map(Trainer::getUser).map(User::getUsername).collect(Collectors.toSet());
        var trainerUsernamesToAdd = trainerUsernames.stream().filter(trainer -> !assignedTrainerUsernames.contains(trainer)).collect(Collectors.toSet());
        var trainerUsernamesToRemove = assignedTrainerUsernames.stream().filter(trainer -> !trainerUsernames.contains(trainer)).collect(Collectors.toSet());

        Set<Trainer> trainersToRemove = trainerService.getAllTrainersUserUsername(trainerUsernamesToRemove);
        Set<Trainer> trainersToAdd = trainerService.getAllTrainersUserUsername(trainerUsernamesToAdd);

        //If it is empty, it will automatically be cleaned
        trainee.getTrainers().removeAll(trainersToRemove);
        trainee.getTrainers().addAll(trainersToAdd);

        traineeRepository.save(trainee);
        log.info("Trainee trainers updated successfully: {}", trainee.getId());
        return trainee.getTrainers();
    }

    @Transactional
    public Trainee updateTraineeProfile(
            @NotBlank String username,
            @NotBlank String firstName,
            @NotBlank String lastName,
            LocalDate dateOfBirth,
            String address,
            @NotNull boolean isActive
    ) {
        log.info("Checking user with Username/Password");

        Trainee trainee = traineeRepository.getTraineeByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));
        User user = trainee.getUser();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(isActive);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        traineeRepository.save(trainee);
        log.info("Trainee profile updated successfully with username: {}", username);
        return trainee;
    }


    @Transactional
    public void deleteTrainee(@NotBlank String username) {
        log.info("Checking user with Username/Password");

        Trainee trainee = traineeRepository.getTraineeByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));

        traineeRepository.deleteTraineeById(trainee.getId());
        log.info("Trainee deleted successfully with username: {}", username);
    }

    @Transactional
    public void activateTrainee(@NotBlank String username) {
        log.info("Checking user with Username/Password");

        Trainee trainee = traineeRepository.getTraineeByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));
        if (trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee profile is already active");
        }

        trainee.getUser().setActive(true);
        traineeRepository.save(trainee);
        log.info("Trainee activated successfully with username: {}", username);
    }

    @Transactional
    public void deactivateTrainee(@NotBlank String username) {
        log.info("Checking user with Username/Password");

        Trainee trainee = traineeRepository.getTraineeByUserUsername(username).orElseThrow(() -> new EntityNotFoundException("User doesn't exist"));
        if (!trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee profile is already inactive");
        }

        trainee.getUser().setActive(false);
        traineeRepository.save(trainee);
        log.info("Trainee deactivated successfully with username: {}", username);
    }
}
