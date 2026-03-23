package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.exception.AuthenticationFailedException;
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
    private static final String PASSWORD = "password";
    private static final LocalDate FROM = LocalDate.of(2024, 1, 1);
    private static final LocalDate TO = LocalDate.of(2026, 3, 16);

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void getTraineeTrainings_throwsWhenTraineeCredentialsAreInvalid() {
        when(traineeService.authenticateTrainee("user", "bad")).thenReturn(false);

        assertThrows(
                AuthenticationFailedException.class,
                () -> trainingService.getTraineeTrainings("user", "bad", "trainee.user",FROM, TO, "pw","YOGA")
        );
    }

    @Test
    void getTrainerTrainings_returnsDataWhenCredentialsAreValid() {
        String username = "user";
        String password = "pass";

        List<Training> trainings = List.of(new Training());

        when(trainerService.authenticateTrainer(username, password)).thenReturn(true);
        when(trainingRepository.findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                "trainer.user", FROM, TO, "toby")).thenReturn(trainings
        );


        List<Training> result = trainingService.getTrainerTrainings(username, password, "trainer.user", FROM, TO, "toby");

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

        Trainer trainer = new Trainer();
        trainer.setTrainingType(new TrainingType("YOGA"));

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeService.findTraineeByUsername(traineeUsername)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.createTraining(traineeUsername, trainerUsername, training));

        verify(trainerService).getTrainer(trainerUsername);
        verify(traineeService).findTraineeByUsername(traineeUsername);
    }

    @Test
    void createTraining_whenTrainerTrainingTypeIsNull_throwsIllegalArgumentException() {
        String traineeUsername = "trainee";
        String trainerUsername = "trainer";
        Training training = new Training();

        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeService.findTraineeByUsername(traineeUsername)).thenReturn(Optional.of(trainee));

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(traineeUsername, trainerUsername, training));

    }

    @Test
    void delete_whenUsernameProvided_callsRepositoryDelete() {
        String username = "john";
        trainingService.delete(username);
        verify(trainingRepository).deleteTrainingByTraineeUserUsername(username);
    }


    @Test
    void getTraineeTrainings_whenAuthenticationFails_throwsAuthenticationFailedException() {
        String traineeUsername = "john";
        String username = "sd";
        String password = "bad";

        when(traineeService.authenticateTrainee(username, password)).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.getTraineeTrainings(username, password, traineeUsername, FROM, TO, "pw","YOGA"));

        verify(traineeService).authenticateTrainee(username, password);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void getTraineeTrainings_whenAuthenticated_returnsTrainings() {
        String traineeUsername = "john";
        String user = "s";
        String password = "good";

        List<Training> trainings = List.of(new Training(), new Training());

        when(traineeService.authenticateTrainee(user, password)).thenReturn(true);
        when(trainingRepository
                .findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeNameAndTrainerUserUsername(
                        traineeUsername,
                        FROM,
                        TO,
                        "YOGA",
                        "pw"
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings(user, password, traineeUsername, FROM, TO, "pw","YOGA");

        assertEquals(trainings, result);

        verify(traineeService).authenticateTrainee(user, password);
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
    void getTrainerTrainings_whenAuthenticationFails_throwsAuthenticationFailedException() {
        String username = "mike";
        String password = "bad";

        when(trainerService.authenticateTrainer(username, password)).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.getTrainerTrainings(username, password,"trainer.user", FROM, TO, "toby"));

        verify(trainerService).authenticateTrainer(username, password);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void getTrainerTrainings_whenAuthenticated_returnsTrainings() {
        String username = "mike";
        String password = "good";

        List<Training> trainings = List.of(new Training());

        when(trainerService.authenticateTrainer(username, password)).thenReturn(true);
        when(trainingRepository
                .findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                        "trainer.username",
                        FROM,
                        TO,
                        "toby"
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings(username, password, "trainer.username", FROM, TO, "toby");

        assertEquals(trainings, result);

        verify(trainerService).authenticateTrainer(username, password);
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
        String traineeUsername = "trainer";
        String trainerUsername = "bad";
        Training training = new Training();

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainingService.createTraining(traineeUsername, trainerUsername, training));
    }
}
