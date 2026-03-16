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
import java.util.HashSet;
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
        when(traineeService.authenticateTrainee("trainee.user", "bad")).thenReturn(false);

        assertThrows(
                AuthenticationFailedException.class,
                () -> trainingService.getTraineeTrainings("trainee.user", "bad", FROM, TO, "YOGA")
        );
    }

    @Test
    void getTrainerTrainings_returnsDataWhenCredentialsAreValid() {
        String username = "trainer.user";
        String password = "pass";

        List<Training> trainings = List.of(new Training());

        when(trainerService.authenticateTrainer(username, password)).thenReturn(true);
        when(trainingRepository.findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                username, FROM, TO, "toby")).thenReturn(trainings
        );


        List<Training> result = trainingService.getTrainerTrainings(username, password, FROM, TO, "toby");

        assertEquals(trainings, result);
    }

    @Test
    void createTraining_whenTraineeNotFound_throwsException() {
        String traineeUsername = "john";
        String password = "bad";
        Training training = new Training();

        when(trainerService.getTrainer(traineeUsername)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,  () -> trainingService.createTraining(traineeUsername, password, training));
        verify(trainerService).getTrainer(traineeUsername);
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
                () -> trainingService.createTraining(trainerUsername, traineeUsername, training));

        verify(trainerService).getTrainer(trainerUsername);
        verify(traineeService).findTraineeByUsername(traineeUsername);
    }

    @Test
    void createTraining_whenTrainerTrainingTypeIsNull_throwsIllegalArgumentException() {
        String trainerUsername = "trainer";
        String traineeUsername = "trainee";
        Training training = new Training();

        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeService.findTraineeByUsername(traineeUsername)).thenReturn(Optional.of(trainee));

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(trainerUsername, traineeUsername, training));

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
        String password = "bad";

        when(traineeService.authenticateTrainee(traineeUsername, password)).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.getTraineeTrainings(traineeUsername, password, FROM, TO, "YOGA"));

        verify(traineeService).authenticateTrainee(traineeUsername, password);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void getTraineeTrainings_whenAuthenticated_returnsTrainings() {
        String traineeUsername = "john";
        String password = "good";

        List<Training> trainings = List.of(new Training(), new Training());

        when(traineeService.authenticateTrainee(traineeUsername, password)).thenReturn(true);
        when(trainingRepository
                .findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeName(
                        traineeUsername,
                        FROM,
                        TO,
                        "YOGA"
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings(traineeUsername, password, FROM, TO, "YOGA");

        assertEquals(trainings, result);

        verify(traineeService).authenticateTrainee(traineeUsername, password);
        verify(trainingRepository)
                .findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeName(
                        traineeUsername,
                        FROM,
                        TO,
                        "YOGA"
                );
    }

    @Test
    void getTrainerTrainings_whenAuthenticationFails_throwsAuthenticationFailedException() {
        String trainerUsername = "mike";
        String password = "bad";

        when(trainerService.authenticateTrainer(trainerUsername, password)).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.getTrainerTrainings(trainerUsername, password, FROM, TO, "toby"));

        verify(trainerService).authenticateTrainer(trainerUsername, password);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void getTrainerTrainings_whenAuthenticated_returnsTrainings() {
        String trainerUsername = "mike";
        String password = "good";

        List<Training> trainings = List.of(new Training());

        when(trainerService.authenticateTrainer(trainerUsername, password)).thenReturn(true);
        when(trainingRepository
                .findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                        trainerUsername,
                        FROM,
                        TO,
                        "toby"
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings(trainerUsername, password, FROM, TO, "toby");

        assertEquals(trainings, result);

        verify(trainerService).authenticateTrainer(trainerUsername, password);
        verify(trainingRepository)
                .findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserUsername(
                        trainerUsername,
                        FROM,
                        TO,
                        "toby"
                );
    }

    @Test
    void createTraining_whenTrainerNotFound_throwsEntityNotFoundException() {
        String username = "trainer";
        String password = "bad";
        Training training = new Training();

        when(trainerService.getTrainer(username)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainingService.createTraining(username, password, training));
    }
}
