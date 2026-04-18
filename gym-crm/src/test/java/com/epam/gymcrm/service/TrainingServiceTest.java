package com.epam.gymcrm.service;

import com.epam.gymcrm.client.ReportClient;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.training.TrainingEventDto;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.repository.TrainingRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    private static final LocalDate FROM = LocalDate.of(2024, 1, 1);
    private static final LocalDate TO = LocalDate.of(2026, 3, 16);

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter counter;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private ReportClient reportClient;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void getTrainerTrainings_returnsDataWhenCredentialsAreValid() {
        List<Training> trainings = List.of(new Training());

        when(trainingRepository.findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                "trainer.user", FROM, TO, "toby")).thenReturn(trainings
        );


        List<Training> result = trainingService.getTrainerTrainings("trainer.user", FROM, TO, "toby");

        assertEquals(trainings, result);
    }

    @Test
    void createTraining_whenTraineeNotFound_throwsException() {
        String traineeUsername = "john";
        String trainerUsername = "dsa";
        Training training = new Training();

        when(trainerService.getTrainer(trainerUsername)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,  () -> trainingService.createTraining(traineeUsername, trainerUsername, training));
        verify(trainerService).getTrainer(trainerUsername);
    }

    @Test
    void createTraining_whenTraineeNotFound_throwsEntityNotFoundException() {
        String trainerUsername = "trainer";
        String traineeUsername = "trainee";
        Training training = new Training();
        Counter trainerSuccessCounter = mock(Counter.class);
        Counter traineeFailureCounter = mock(Counter.class);

        Trainer trainer = new Trainer();
        trainer.setTrainingType(new TrainingType("YOGA"));

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeService.findTraineeByUsername(traineeUsername)).thenReturn(Optional.empty());

        when(meterRegistry.counter(
                "crm_trainer_fetch_total",
                "result", "success"
        )).thenReturn(trainerSuccessCounter);

        when(meterRegistry.counter(
                "crm_trainee_fetch_total",
                "result", "failure"
        )).thenReturn(traineeFailureCounter);

        assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.createTraining(traineeUsername, trainerUsername, training)
        );

        verify(trainerService).getTrainer(trainerUsername);
        verify(traineeService).findTraineeByUsername(traineeUsername);

        verify(meterRegistry).counter(
                "crm_trainer_fetch_total",
                "result", "success"
        );
        verify(meterRegistry).counter(
                "crm_trainee_fetch_total",
                "result", "failure"
        );
        verify(trainerSuccessCounter).increment();
        verify(traineeFailureCounter).increment();

    }

    @Test
    void createTraining_whenTrainerTrainingTypeIsNull_throwsIllegalArgumentException() {
        String traineeUsername = "trainee";
        String trainerUsername = "trainer";
        Training training = new Training();
        Counter trainerSuccessCounter = mock(Counter.class);
        Counter traineeSuccessCounter = mock(Counter.class);

        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());

        when(meterRegistry.counter("crm_trainer_fetch_total", "result", "success")).thenReturn(trainerSuccessCounter);
        when(meterRegistry.counter("crm_trainee_fetch_total", "result", "success")).thenReturn(traineeSuccessCounter);
        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeService.findTraineeByUsername(traineeUsername)).thenReturn(Optional.of(trainee));

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(traineeUsername, trainerUsername, training));

        verify(trainerSuccessCounter).increment();
        verify(traineeSuccessCounter).increment();
        verify(trainingRepository, never()).save(any(Training.class));

    }

    @Test
    void createTraining_whenDataIsValid_savesTrainingAndIncrementsSuccessMetric() {
        String traineeUsername = "trainee";
        String trainerUsername = "trainer";
        Training training = new Training();
        Counter trainerSuccessCounter = mock(Counter.class);
        Counter traineeSuccessCounter = mock(Counter.class);
        Counter createSuccessCounter = mock(Counter.class);

        Trainer trainer = new Trainer();
        trainer.setTrainingType(new TrainingType("YOGA"));
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());
        TrainingType trainingType = new TrainingType("YOGA");
        TrainingEventDto eventDto = new TrainingEventDto();

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeService.findTraineeByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerService.trainingType("YOGA")).thenReturn(trainingType);
        when(meterRegistry.counter("crm_trainer_fetch_total", "result", "success")).thenReturn(trainerSuccessCounter);
        when(meterRegistry.counter("crm_trainee_fetch_total", "result", "success")).thenReturn(traineeSuccessCounter);
        when(meterRegistry.counter("crm_training_create_attempts_total", "result", "success")).thenReturn(createSuccessCounter);
        when(trainingMapper.toEventDto(training)).thenReturn(eventDto);

        trainingService.createTraining(traineeUsername, trainerUsername, training);

        verify(trainingRepository).save(training);
        verify(reportClient).sendWorkload(eventDto);
        verify(trainerSuccessCounter).increment();
        verify(traineeSuccessCounter).increment();
        verify(createSuccessCounter).increment();
    }

    @Test
    void delete_whenUsernameProvided_callsRepositoryDelete() {
        String username = "john";
        trainingService.delete(username);
        verify(trainingRepository).deleteTrainingByTraineeUserUsername(username);
    }


    @Test
    void getTraineeTrainings_whenAuthenticated_returnsTrainings() {
        String traineeUsername = "john";

        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingRepository
                .findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeNameAndTrainerUserUsername(
                        traineeUsername,
                        FROM,
                        TO,
                        "YOGA",
                        "pw"
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings(traineeUsername, FROM, TO, "pw","YOGA");

        assertEquals(trainings, result);

        verify(trainingRepository)
                .findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeNameAndTrainerUserUsername(
                        traineeUsername,
                        FROM,
                        TO,
                        "YOGA",
                        "pw"
                );
    }

    @Test
    void getTrainerTrainings_whenAuthenticated_returnsTrainings() {
        List<Training> trainings = List.of(new Training());

        when(trainingRepository
                .findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                        "trainer.username",
                        FROM,
                        TO,
                        "toby"
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("trainer.username", FROM, TO, "toby");

        assertEquals(trainings, result);

        verify(trainingRepository)
                .findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                        "trainer.username",
                        FROM,
                        TO,
                        "toby"
                );
    }

    @Test
    void createTraining_whenTrainerNotFound_throwsEntityNotFoundException() {
        String traineeUsername = "john";
        String trainerUsername = "dsa";
        Training training = new Training();

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.empty());

        when(meterRegistry.counter(
                "crm_trainer_fetch_total",
                "result", "failure"
        )).thenReturn(counter);

        assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.createTraining(traineeUsername, trainerUsername, training)
        );

        verify(trainerService).getTrainer(trainerUsername);
        verify(meterRegistry).counter(
                "crm_trainer_fetch_total",
                "result", "failure"
        );
        verify(counter).increment();
    }
}
