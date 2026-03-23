package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dto.trainee.response.TraineeTrainingDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.dto.trainer.response.TrainerTrainingDto;
import com.epam.gymcrm.mapper.TraineeMapper;
import com.epam.gymcrm.mapper.TrainerMapper;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Component
@Validated
public class TrainingFacade {

    private final TrainingService trainingService;
    private TrainerService trainerService;
    private TraineeMapper traineeMapper;
    private TrainerMapper trainerMapper;

    public TrainingFacade(
            TrainingService trainingService,
            TrainerService trainerService,
            TraineeMapper traineeMapper,
            TrainerMapper trainerMapper
    ) {
        this.trainingService = trainingService;
        this.trainerService = trainerService;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
    }

    public void addTraining(
            @Valid TrainingRequestDto trainingRequestDto
    ) {
       trainerService.authenticateTrainer(trainingRequestDto.getUsername(), trainingRequestDto.getPassword());
       trainingService.createTraining(
               trainingRequestDto.getTraineeUsername(),
               trainingRequestDto.getTrainerUsername(),
               new Training(
                       trainingRequestDto.getName(),
                       trainingRequestDto.getDate(),
                       trainingRequestDto.getDuration()
               )
       );
    }

    public List<TraineeTrainingDto> getTraineeTrainings(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String traineeUsername,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            String trainerUsername,
            String trainingType
    ) {
        List<Training> trainings = trainingService.getTraineeTrainings(username, password, traineeUsername, fromDate, toDate, trainerUsername, trainingType);
        return (List<TraineeTrainingDto>) trainings.stream().map(training -> traineeMapper.toTrainingDto(training)).toList();
    }

    public List<TrainerTrainingDto> getTrainerTrainings(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String trainerUsername,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            String traineeName
    ) {
        List<Training> trainings =  trainingService.getTrainerTrainings(username, password, trainerUsername, fromDate, toDate, traineeName);
        return trainings.stream().map(training -> trainerMapper.toTrainingDto(training)).toList();
    }
}
