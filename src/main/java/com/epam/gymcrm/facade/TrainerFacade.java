package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TrainerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Component
@Validated
public class TrainerFacade {

    private final TrainerService trainerService;

    public TrainerFacade(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public Trainer createTrainerProfile(@Valid User user, @Valid Trainer trainer, @NotBlank String trainingType) {
       return trainerService.createTrainerProfile(user, trainer, trainingType);
    }

    public boolean authenticateTrainer(@NotBlank String username, @NotBlank String password) {
        return trainerService.authenticateTrainer(username, password);
    }

    public Optional<Trainer> getTrainerProfile(@NotBlank String username, @NotBlank String password) {
        trainerService.authenticateTrainer(username, password);
        return trainerService.getTrainer(username);
    }

    public void changeTrainerPassword(@NotBlank String username, @NotBlank String password, @NotBlank String newPassword) {
        trainerService.changeTrainerPassword(username, password, newPassword);
    }

    public void updateTrainerProfile(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String specialization
    ) {
        trainerService.updateTrainerProfile(username, password, firstName, lastName, specialization);
    }

    public void activateTrainer(@NotBlank String username, @NotBlank String password) {
        trainerService.activateTrainer(username, password);
    }

    public void deactivateTrainer(@NotBlank String username, @NotBlank String password) {
        trainerService.deactivateTrainer(username, password);
    }

    public List<Trainer> getAllTrainers(@NotBlank String username, @NotBlank String password) {
        trainerService.authenticateTrainer(username, password);
        return trainerService.getAllTrainers();
    }
}
