package com.epam.gymcrm.service;

import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.searchCriteria.TraineeTrainingSearchCriteria;
import com.epam.gymcrm.searchCriteria.TrainerTrainingSearchCriteria;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService();
        trainingService.setTrainingRepository(trainingRepository);
        trainingService.setTrainerService(trainerService);
        trainingService.setTraineeService(traineeService);
    }


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
        when(trainingRepository.findTrainingByTrainerUserUsernameOrDateBetweenAndTraineeUserFirstName(
                username, criteria.getFromDate(), criteria.getToDate(),
                criteria.getTraineeName())).thenReturn(trainings
        );


        List<Training> result = trainingService.getTrainerTrainings(username, password, criteria);

        assertEquals(trainings, result);
    }
}
