package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.CreateTraineeDto;
import com.epam.gymcrm.dto.trainee.TraineeDto;
import com.epam.gymcrm.dto.trainee.TrainerDto;
import com.epam.gymcrm.dto.trainee.TrainerListDto;
import com.epam.gymcrm.dto.trainee.request.TrainerRequestDto;
import com.epam.gymcrm.mappper.TraineeMapper;
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

    public Trainee createTraineeProfile(CreateTraineeDto createTraineeDto) {
       Trainee traineeDto = traineeMapper.toTrainee(createTraineeDto);
       traineeService.createTraineeProfile(traineeDto);
       return  traineeDto;
    }

    public boolean authenticateTrainee(@NotBlank String username, @NotBlank String password) {
        return traineeService.authenticateTrainee(username, password);
    }

    public TraineeDto getTraineeProfile(@NotBlank String username, @NotBlank String password, @NotBlank String traineeProfile) {
        traineeService.authenticateTrainee(username, password);
        Trainee trainee = traineeService.getTrainee(traineeProfile).orElseThrow();
        return traineeMapper.toTraineeDto(trainee);
    }

    public void changeTraineePassword(@NotBlank String username, @NotBlank String password, @NotBlank String newPassword) {
        traineeService.changeTraineePassword(username, password, newPassword);
    }

    public TraineeDto updateTraineeProfile(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull LocalDate dateOfBirth,
            @NotBlank String address,
            @NotNull boolean isActive
    ) {
       Trainee trainee = traineeService.updateTraineeProfile(username, password, firstName, lastName, dateOfBirth, address, isActive);
       return traineeMapper.toTraineeDto(trainee);
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

    public List<TrainerDto> updateTraineeTrainers(
            @NotBlank String username,
            @NotBlank String password,
            Set<@NotNull String> trainerUsernames
    ) {
        List<Trainer> trainers = traineeService.updateTraineeTrainers(username, password, trainerUsernames);
        return trainers.stream().map(traineeMapper::toTrainerDto).toList();
    }

    public List<TrainerDto> getUnassignedTrainersForTrainee(@NotBlank String username, @NotBlank String password) {
        traineeService.authenticateTrainee(username, password);
        List<Trainer> trainers = trainerService.getUnassignedTrainersForTrainee(username);
        return trainers.stream().map(traineeMapper::toTrainerDto).toList();
    }
}
