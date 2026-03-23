package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerTraineeListItemDto;
import com.epam.gymcrm.mappper.TrainerMapper;
import com.epam.gymcrm.service.TrainerService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@Validated
public class TrainerFacade {

    private final TrainerService trainerService;
    private final TrainerMapper  trainerMapper;

    public TrainerFacade(TrainerService trainerService, TrainerMapper trainerMapper) {
        this.trainerService = trainerService;
        this.trainerMapper = trainerMapper;
    }

    public AuthenticationDto createTrainerProfile(CreateTrainerDto createTrainerDto) {
       Trainer totrainer = trainerMapper.toTrainer(createTrainerDto);
       Trainer trainer = trainerService.createTrainerProfile(totrainer.getUser(), totrainer, totrainer.getTrainingType().getTrainingTypeName());
       return trainerMapper.toAuth(trainer);
    }

    public boolean authenticateTrainer(@NotBlank String username, @NotBlank String password) {
        return trainerService.authenticateTrainer(username, password);
    }

    public TrainerTraineeListItemDto getTrainerProfile(@NotBlank String username, @NotBlank String password) {
        trainerService.authenticateTrainer(username, password);
        Trainer trainer = trainerService.getTrainer(username).orElseThrow();
        return trainerMapper.toTrainerDto(trainer);
    }

    public void changeTrainerPassword(@NotBlank String username, @NotBlank String password, @NotBlank String newPassword) {
        trainerService.changeTrainerPassword(username, password, newPassword);
    }

    public TrainerTraineeListItemDto updateTrainerProfile(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull boolean isActive,
            @NotBlank String specialization
    ) {
       Trainer trainer = trainerService.updateTrainerProfile(username, password, firstName, lastName, isActive, specialization);
       return trainerMapper.toTrainerDto(trainer);
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
