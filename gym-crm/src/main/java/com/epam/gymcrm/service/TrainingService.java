package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.training.ActionType;
import com.epam.gymcrm.dto.training.TrainingEventDto;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.exception.EntityNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@Validated
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final MeterRegistry meterRegistry;
    private final TrainingMapper trainingMapper;
    private final ReportWorkloadService  reportWorkloadService;
    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    public TrainingService(
            TrainingRepository trainingRepository,
            TrainerService trainerService,
            TraineeService traineeService,
            MeterRegistry meterRegistry,
            ReportWorkloadService reportWorkloadService,
            TrainingMapper trainingMapper
    ) {
        this.trainingRepository = trainingRepository;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.meterRegistry = meterRegistry;
        this.reportWorkloadService = reportWorkloadService;
        this.trainingMapper = trainingMapper;
    }

    @Transactional
    public void createTraining(
            @NotBlank String traineeUsername,
            @NotBlank String trainerUsername,
            @Valid Training training
    ) {
        Trainer trainer = trainerService.getTrainer(trainerUsername)
                .orElseThrow(() -> {
                    meterRegistry.counter(
                            "crm_trainer_fetch_total",
                            "result", "failure"
                    ).increment();
                    return new EntityNotFoundException("Trainer not found");
                });

        meterRegistry.counter(
                "crm_trainer_fetch_total",
                "result", "success"
        ).increment();


        Trainee trainee = traineeService.findTraineeByUsername(traineeUsername)
                .orElseThrow(() -> {
                    meterRegistry.counter(
                            "crm_trainee_fetch_total",
                            "result", "failure"
                    ).increment();
                    return new EntityNotFoundException("Trainee not found");
                });

        meterRegistry.counter(
                "crm_trainee_fetch_total",
                "result", "success"
        ).increment();


        if (trainer.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type is required for training creation");
        }

        trainee.getTrainers().add(trainer);

        TrainingType type = trainerService.trainingType(trainer.getTrainingType().getTrainingTypeName());

        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(type);

        trainingRepository.save(training);
        TrainingEventDto trainingEventDto = trainingMapper.toEventDto(training);
        trainingEventDto.setAction(ActionType.ADD);
        reportWorkloadService.sendWorkloadSafe(trainingEventDto);
        meterRegistry.counter("crm_training_create_attempts_total", "result", "success").increment();
    }

    @Transactional
    public void delete(String username) {
        List<Training> training = findTrainingsByTraineeUsername(username);
        for (Training t : training) {
            TrainingEventDto trainingEventDto = trainingMapper.toEventDto(t);
            trainingEventDto.setAction(ActionType.DELETE);
            reportWorkloadService.sendWorkloadSafe(trainingEventDto);
        }
        trainingRepository.deleteTrainingByTraineeUserUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Training> findTrainingsByTraineeUsername(String username) {
        return trainingRepository.findByTraineeUserUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(
            @NotBlank String traineeUsername,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            String trainerUsername,
            String trainingType
    ) {
        log.info("Selecting trainee trainings with username: {}", traineeUsername);
        return trainingRepository.findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeNameAndTrainerUserUsername(
                traineeUsername,
                fromDate,
                toDate,
                trainingType,
                trainerUsername
        );
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(
            @NotBlank String trainerUsername,
            @DateTimeFormat LocalDate fromDate,
            @DateTimeFormat LocalDate toDate,
            String traineeUsername
    ) {
        log.info("Selecting trainer trainings with username: {}", trainerUsername);
        return trainingRepository.findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                trainerUsername,
                fromDate,
                toDate,
                traineeUsername
        );
    }
}
