package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.searchCriteria.TraineeTrainingSearchCriteria;
import com.epam.gymcrm.searchCriteria.TrainerTrainingSearchCriteria;
import com.epam.gymcrm.service.TrainerService;
import com.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingFacadeTest {

    private static final String TRAINER_USERNAME = "trainer.user";
    private static final String TRAINEE_USERNAME = "trainee.user";
    private static final String PASSWORD = "password";

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
        TraineeTrainingSearchCriteria criteria = new TraineeTrainingSearchCriteria();
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, criteria)).thenReturn(trainings);

        List<Training> result = trainingFacade.getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, criteria);

        assertEquals(trainings, result);
        verify(trainingService).getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, criteria);
    }

    @Test
    void getTrainerTrainingsDelegatesToService() {
        TrainerTrainingSearchCriteria criteria = new TrainerTrainingSearchCriteria();
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTrainerTrainings(TRAINER_USERNAME, PASSWORD, criteria)).thenReturn(trainings);

        List<Training> result = trainingFacade.getTrainerTrainings(TRAINER_USERNAME, PASSWORD, criteria);

        assertEquals(trainings, result);
        verify(trainingService).getTrainerTrainings(TRAINER_USERNAME, PASSWORD, criteria);
    }
}
