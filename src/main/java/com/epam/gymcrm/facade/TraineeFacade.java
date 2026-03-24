package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.request.CreateTraineeDto;
import com.epam.gymcrm.dto.trainee.response.TraineeProfileDto;
import com.epam.gymcrm.dto.trainee.response.TrainerDto;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.mapper.TraineeMapper;
import com.epam.gymcrm.service.TraineeService;
import com.epam.gymcrm.service.TrainerService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@Validated
public class TraineeFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TraineeMapper traineeMapper;

    public TraineeFacade(TraineeService traineeService, TrainerService trainerService, TraineeMapper traineeMapper) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.traineeMapper = traineeMapper;
    }

    public AuthenticationDto createTraineeProfile(CreateTraineeDto createTraineeDto) {
       Trainee trainee = traineeMapper.toTrainee(createTraineeDto);
       Trainee createdTrainee = traineeService.createTraineeProfile(trainee);
       return traineeMapper.toAuth(createdTrainee);
    }

    public TraineeProfileDto getTraineeProfile(@NotBlank String traineeProfile) {
        Trainee trainee = traineeService.getTrainee(traineeProfile).orElseThrow(() -> new EntityNotFoundException("Trainee doesn't exist"));
        return traineeMapper.toTraineeDto(trainee);
    }

    public TraineeProfileDto updateTraineeProfile(
            @NotBlank String username,
            @NotBlank String firstName,
            @NotBlank String lastName,
            LocalDate dateOfBirth,
            String address,
            @NotNull boolean isActive
    ) {
       Trainee trainee = traineeService.updateTraineeProfile(username, firstName, lastName, dateOfBirth, address, isActive);
       return traineeMapper.toTraineeDto(trainee);
    }

    public void activateTrainee(@NotBlank String username) {
        traineeService.activateTrainee(username);
    }

    public void deactivateTrainee(@NotBlank String username) {
        traineeService.deactivateTrainee(username);
    }

    public void deleteTrainee(@NotBlank String username) {
        traineeService.deleteTrainee(username);
    }

    public List<TrainerDto> updateTraineeTrainers(
            @NotBlank String username,
            Set<@NotNull String> trainerUsernames
    ) {
        List<Trainer> trainers = traineeService.updateTraineeTrainers(username, trainerUsernames);
        return trainers.stream().map(traineeMapper::toTrainerDto).toList();
    }

    public List<TrainerDto> getUnassignedTrainersForTrainee(@NotBlank String username) {
        List<Trainer> trainers = trainerService.getUnassignedTrainersForTrainee(username);
        return trainers.stream().map(traineeMapper::toTrainerDto).toList();
    }
}
