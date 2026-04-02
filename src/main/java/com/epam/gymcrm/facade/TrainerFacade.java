package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.request.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.response.TrainerProfileDto;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.mapper.TrainerMapper;
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
       return trainerService.createTrainerProfile(
               totrainer.getUser(),
               totrainer,
               totrainer.getTrainingType().getTrainingTypeName()
       );
    }

    public TrainerProfileDto getTrainerProfile(@NotBlank String username) {
        Trainer trainer = trainerService.getTrainer(username).orElseThrow(() -> new EntityNotFoundException("Trainer doesn't exist"));
        return trainerMapper.toTrainerDto(trainer);
    }

    public TrainerProfileDto updateTrainerProfile(
            @NotBlank String username,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull boolean isActive
    ) {
       Trainer trainer = trainerService.updateTrainerProfile(username, firstName, lastName, isActive);
       return trainerMapper.toTrainerDto(trainer);
    }

    public void activateTrainer(@NotBlank String username) {
        trainerService.activateTrainer(username);
    }

    public void deactivateTrainer(@NotBlank String username) {
        trainerService.deactivateTrainer(username);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }
}
