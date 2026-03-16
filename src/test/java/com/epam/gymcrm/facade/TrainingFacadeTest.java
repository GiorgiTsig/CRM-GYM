package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingFacadeTest {

    private static final String TRAINER_USERNAME = "trainer.user";
    private static final String TRAINEE_USERNAME = "trainee.user";
    private static final String PASSWORD = "password";
    private static final LocalDate FROM = LocalDate.of(2024, 1, 1);
    private static final LocalDate TO = LocalDate.of(2026, 3, 16);

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainingFacade trainingFacade;

    @Test
    void addTrainingAuthenticatesTrainerThenCreatesTraining() {
        Training training = new Training();
        when(trainerService.authenticateTrainer(TRAINEE_USERNAME, PASSWORD)).thenReturn(true);
        when(trainingService.createTraining(TRAINER_USERNAME, TRAINEE_USERNAME, training)).thenReturn(training);

        Training result = trainingFacade.addTraining(TRAINER_USERNAME, PASSWORD, TRAINEE_USERNAME, training);

        assertSame(training, result);
        InOrder inOrder = inOrder(trainerService, trainingService);
        inOrder.verify(trainerService).authenticateTrainer(TRAINEE_USERNAME, PASSWORD);
        inOrder.verify(trainingService).createTraining(TRAINER_USERNAME, TRAINEE_USERNAME, training);
    }

    @Test
    void getTraineeTrainingsDelegatesToService() {
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, FROM, TO, "MMA")).thenReturn(trainings);

        List<Training> result = trainingFacade.getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, FROM, TO, "MMA");

        assertEquals(trainings, result);
        verify(trainingService).getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, FROM, TO, "MMA");
    }

    @Test
    void getTrainerTrainingsDelegatesToService() {
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTrainerTrainings(TRAINER_USERNAME, PASSWORD, FROM, TO, "Toby")).thenReturn(trainings);

        List<Training> result = trainingFacade.getTrainerTrainings(TRAINER_USERNAME, PASSWORD, FROM, TO, "Toby");

        assertEquals(trainings, result);
        verify(trainingService).getTrainerTrainings(TRAINER_USERNAME, PASSWORD, FROM, TO, "Toby");
    }
}
