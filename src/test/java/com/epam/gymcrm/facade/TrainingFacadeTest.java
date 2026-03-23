package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dto.trainee.TraineeTrainingDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.dto.trainer.TrainerTrainingDto;
import com.epam.gymcrm.mappper.TraineeMapper;
import com.epam.gymcrm.mappper.TrainerMapper;
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
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainingFacade trainingFacade;

    @Test
    void addTrainingAuthenticatesTrainerThenCreatesTraining() {
        TrainingRequestDto trainingDto = new TrainingRequestDto();
        trainingDto.setName("Martial Art");
        trainingDto.setDate(FROM);
        trainingDto.setDuration(90);
        trainingDto.setTrainerUsername(TRAINER_USERNAME);

        when(trainerService.authenticateTrainer(trainingDto.getUsername(), trainingDto.getPassword())).thenReturn(true);

        trainingFacade.addTraining(trainingDto);

        InOrder inOrder = inOrder(trainerService, trainingService);
        inOrder.verify(trainerService).authenticateTrainer(trainingDto.getUsername(), trainingDto.getPassword());
    }

    @Test
    void getTraineeTrainingsDelegatesToService() {
        Training training = new Training();
        List<Training> trainings = List.of(training);
        TraineeTrainingDto trainingDto = new TraineeTrainingDto();
        List<TraineeTrainingDto> expectedDtoList = List.of(trainingDto);
        when(trainingService.getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, FROM, TO, "MMA"))
                .thenReturn(trainings);

        when(traineeMapper.toTrainingDto(training)).thenReturn(trainingDto);

        List<TraineeTrainingDto> result = trainingFacade.getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, FROM, TO, "MMA");
        assertEquals(expectedDtoList, result);
        verify(trainingService).getTraineeTrainings(TRAINEE_USERNAME, PASSWORD, FROM, TO, "MMA");
    }
    @Test
    void getTrainerTrainingsDelegatesToService() {
        Training training = new Training();
        List<Training> trainings = List.of(training);

        TrainerTrainingDto trainingDto = new TrainerTrainingDto();
        List<TrainerTrainingDto> expectedDtoList = List.of(trainingDto);

        when(trainingService.getTrainerTrainings(TRAINER_USERNAME, PASSWORD, FROM, TO, "Toby"))
                .thenReturn(trainings);

        when(trainerMapper.toTrainingDto(training)).thenReturn(trainingDto);

        List<TrainerTrainingDto> result =
                trainingFacade.getTrainerTrainings(TRAINER_USERNAME, PASSWORD, FROM, TO, "Toby");

        assertEquals(expectedDtoList, result);
        verify(trainingService).getTrainerTrainings(TRAINER_USERNAME, PASSWORD, FROM, TO, "Toby");
    }
}
