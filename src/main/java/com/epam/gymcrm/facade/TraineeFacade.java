package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TraineeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Validated
public class TraineeFacade {

    private final TraineeService traineeService;

    public TraineeFacade(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public void createTraineeProfile(@Valid User user, @Valid Trainee trainee, @NotBlank String trainerUsernames) {
        traineeService.createTraineeProfile(user, trainee, trainerUsernames);
    }

    public boolean authenticateTrainee(@NotBlank String username, @NotBlank String password) {
        return traineeService.authenticateTrainee(username, password);
    }

    public Optional<Trainee> getTraineeProfile(@NotBlank String username) {
        return traineeService.getTrainee(username);
    }

    public void changeTraineePassword(@NotBlank String username, @NotBlank String password, @NotBlank String newPassword) {
        traineeService.changeTraineePassword(username, password, newPassword);
    }

    public void updateTraineeProfile(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull LocalDate dateOfBirth,
            @NotBlank String address
    ) {
        traineeService.updateTraineeProfile(username, password, firstName, lastName, dateOfBirth, address);
    }

    public void activateTrainee(@NotBlank String username, @NotBlank String password) {
        traineeService.activateTrainee(username, password);
    }

    public void deactivateTrainee(@NotBlank String username, @NotBlank String password) {
        traineeService.deactivateTrainee(username, password);
    }

    public void deleteTrainee(@NotBlank String username, @NotBlank String password) {
        traineeService.deleteTrainee(username, password);
    }

    public void updateTraineeTrainers(
            @NotBlank String username,
            @NotBlank String password,
            @NotNull  String trainerUsernames
    ) {
        traineeService.updateTraineeTrainers(username, password, trainerUsernames);
    }

    public List<Trainer> getUnassignedTrainersForTrainee(@NotBlank String username, @NotBlank String password) {
        return traineeService.getUnassignedTrainersForTrainee(username, password);
    }
}
