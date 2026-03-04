package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.searchCriteria.TraineeTrainingSearchCriteria;
import com.epam.gymcrm.searchCriteria.TrainerTrainingSearchCriteria;
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
                () -> trainingService.getTraineeTrainings("trainee.user", "bad", new TraineeTrainingSearchCriteria())
        );
    }

    @Test
    void getTrainerTrainings_returnsDataWhenCredentialsAreValid() {
        String username = "trainer.user";
        String password = "pass";
        TrainerTrainingSearchCriteria criteria = new TrainerTrainingSearchCriteria();
        List<Training> trainings = List.of(new Training());

        when(trainerService.authenticateTrainer(username, password)).thenReturn(true);
        when(trainingRepository.findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserFirstName(
                username, criteria.getFromDate(), criteria.getToDate(),
                criteria.getTraineeName())).thenReturn(trainings
        );


        List<Training> result = trainingService.getTrainerTrainings(username, password, criteria);

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
    void createTraining_whenValidData_setsFieldsAndSavesTraining() {
        String trainerUsername = "trainer";
        String traineeUsername = "trainee";
        Training training = new Training();

        Trainer trainer = new Trainer();
        trainer.setTrainees(new HashSet<>());
        trainer.setTrainingType(new TrainingType("YOGA"));

        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());

        TrainingType resolvedType = new TrainingType("YOGA");

        when(trainerService.getTrainer(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeService.findTraineeByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerService.trainingType("YOGA")).thenReturn(resolvedType);

        Training result = trainingService.createTraining(trainerUsername, traineeUsername, training);

        assertTrue(trainer.getTrainees().contains(trainee));

        assertSame(trainer, result.getTrainerId());
        assertSame(trainee, result.getTraineeId());
        assertSame(resolvedType, result.getType());
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
        TraineeTrainingSearchCriteria criteria = new TraineeTrainingSearchCriteria();
        criteria.setFromDate(LocalDate.of(2026, 1, 1));
        criteria.setToDate(LocalDate.of(2026, 2, 1));
        criteria.setTrainingType("YOGA");

        when(traineeService.authenticateTrainee(traineeUsername, password)).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.getTraineeTrainings(traineeUsername, password, criteria));

        verify(traineeService).authenticateTrainee(traineeUsername, password);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void getTraineeTrainings_whenAuthenticated_returnsTrainings() {
        String traineeUsername = "john";
        String password = "good";
        TraineeTrainingSearchCriteria criteria = new TraineeTrainingSearchCriteria();
        criteria.setFromDate(LocalDate.of(2026, 1, 1));
        criteria.setToDate(LocalDate.of(2026, 2, 1));
        criteria.setTrainingType("YOGA");

        List<Training> trainings = List.of(new Training(), new Training());

        when(traineeService.authenticateTrainee(traineeUsername, password)).thenReturn(true);
        when(trainingRepository
                .findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeName(
                        traineeUsername,
                        criteria.getFromDate(),
                        criteria.getToDate(),
                        criteria.getTrainingType()
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings(traineeUsername, password, criteria);

        assertEquals(trainings, result);

        verify(traineeService).authenticateTrainee(traineeUsername, password);
        verify(trainingRepository)
                .findTrainingByTraineeUserUsernameAndDateBetweenAndTrainerTrainingTypeTrainingTypeName(
                        traineeUsername,
                        criteria.getFromDate(),
                        criteria.getToDate(),
                        criteria.getTrainingType()
                );
    }

    @Test
    void getTrainerTrainings_whenAuthenticationFails_throwsAuthenticationFailedException() {
        String trainerUsername = "mike";
        String password = "bad";
        TrainerTrainingSearchCriteria criteria = new TrainerTrainingSearchCriteria();
        criteria.setFromDate(LocalDate.of(2026, 1, 1));
        criteria.setToDate(LocalDate.of(2026, 2, 1));
        criteria.setTraineeName("John");

        when(trainerService.authenticateTrainer(trainerUsername, password)).thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> trainingService.getTrainerTrainings(trainerUsername, password, criteria));

        verify(trainerService).authenticateTrainer(trainerUsername, password);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    void getTrainerTrainings_whenAuthenticated_returnsTrainings() {
        String trainerUsername = "mike";
        String password = "good";
        TrainerTrainingSearchCriteria criteria = new TrainerTrainingSearchCriteria();
        criteria.setFromDate(LocalDate.of(2026, 1, 1));
        criteria.setToDate(LocalDate.of(2026, 2, 1));
        criteria.setTraineeName("John");

        List<Training> trainings = List.of(new Training());

        when(trainerService.authenticateTrainer(trainerUsername, password)).thenReturn(true);
        when(trainingRepository
                .findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserFirstName(
                        trainerUsername,
                        criteria.getFromDate(),
                        criteria.getToDate(),
                        criteria.getTraineeName()
                )).thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings(trainerUsername, password, criteria);

        assertEquals(trainings, result);

        verify(trainerService).authenticateTrainer(trainerUsername, password);
        verify(trainingRepository)
                .findTrainingByTrainerUserUsernameAndDateBetweenAndTraineeUserFirstName(
                        trainerUsername,
                        criteria.getFromDate(),
                        criteria.getToDate(),
                        criteria.getTraineeName()
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
