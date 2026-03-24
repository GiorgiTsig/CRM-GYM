package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.dto.trainee.response.TraineeTrainingDto;
import com.epam.gymcrm.dto.trainer.response.TrainerTrainingDto;
import com.epam.gymcrm.mapper.TraineeMapper;
import com.epam.gymcrm.mapper.TrainerMapper;
import com.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private static final LocalDate FROM = LocalDate.of(2024, 1, 1);
    private static final LocalDate TO = LocalDate.of(2026, 3, 16);

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainingFacade trainingFacade;


    @Test
    void getTraineeTrainingsDelegatesToService() {
        Training training = new Training();
        List<Training> trainings = List.of(training);
        TraineeTrainingDto trainingDto = new TraineeTrainingDto();
        List<TraineeTrainingDto> expectedDtoList = List.of(trainingDto);
        when(trainingService.getTraineeTrainings("trainee.username", FROM, TO, TRAINER_USERNAME,"MMA"))
                .thenReturn(trainings);

        when(traineeMapper.toTrainingDto(training)).thenReturn(trainingDto);

        List<TraineeTrainingDto> result = trainingFacade.getTraineeTrainings("trainee.username", FROM, TO, TRAINER_USERNAME,"MMA");
        assertEquals(expectedDtoList, result);
        verify(trainingService).getTraineeTrainings("trainee.username" ,FROM, TO, TRAINER_USERNAME, "MMA");
    }
    @Test
    void getTrainerTrainingsDelegatesToService() {
        Training training = new Training();
        List<Training> trainings = List.of(training);

        TrainerTrainingDto trainingDto = new TrainerTrainingDto();
        List<TrainerTrainingDto> expectedDtoList = List.of(trainingDto);

        when(trainingService.getTrainerTrainings(TRAINER_USERNAME, FROM, TO, "Toby"))
                .thenReturn(trainings);

        when(trainerMapper.toTrainingDto(training)).thenReturn(trainingDto);

        List<TrainerTrainingDto> result =
                trainingFacade.getTrainerTrainings(TRAINER_USERNAME, FROM, TO, "Toby");

        assertEquals(expectedDtoList, result);
        verify(trainingService).getTrainerTrainings(TRAINER_USERNAME, FROM, TO, "Toby");
    }
}
