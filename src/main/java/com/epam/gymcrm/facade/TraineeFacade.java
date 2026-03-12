package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TraineeService;
import com.epam.gymcrm.service.TrainerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Validated
public class TraineeFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TraineeFacade(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public Trainee createTraineeProfile(@Valid User user, @Valid Trainee trainee) {
       return traineeService.createTraineeProfile(user, trainee);
    }

    public boolean authenticateTrainee(@NotBlank String username, @NotBlank String password) {
        return traineeService.authenticateTrainee(username, password);
    }

    public Optional<Trainee> getTraineeProfile(@NotBlank String username, @NotBlank String password) {
        traineeService.authenticateTrainee(username, password);
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
            Set<@NotNull String> trainerUsernames
    ) {
        traineeService.updateTraineeTrainers(username, password, trainerUsernames);
    }

    public List<Trainer> getUnassignedTrainersForTrainee(@NotBlank String username, @NotBlank String password) {
        traineeService.authenticateTrainee(username, password);
        return trainerService.getUnassignedTrainersForTrainee(username);
    }
}
