package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.dto.trainee.TrainingDto;
import com.epam.gymcrm.facade.TrainingFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private static final String TRAINEE_USERNAME = "john.doe";
    private static final String TRAINER_USERNAME = "trainer.smith";
    private static final String PASSWORD = "password123";
    private static final String TRAINING_NAME = "MMA Training";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2026, 3, 17);
    private static final Integer TRAINING_DURATION = 90;

    @Mock
    private TrainingFacade trainingFacade;

    @InjectMocks
    private TrainingController trainingController;

    @Test
    void addTraining_shouldReturnOk_whenTrainingCreated() {
        TrainingTypeDto trainingType = new TrainingTypeDto();
        trainingType.setTrainingTypeName("MMA");
        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setTrainerUsername(TRAINER_USERNAME);
        trainingDto.setDate(TRAINING_DATE);
        trainingDto.setDuration(TRAINING_DURATION);
        trainingDto.setName(TRAINING_NAME);
        trainingDto.setType(trainingType);

        doNothing().when(trainingFacade).addTraining(
                eq(TRAINEE_USERNAME),
                eq(PASSWORD),
                eq(trainingDto)
        );

        ResponseEntity<Void> response = trainingController.addTraining(
                TRAINEE_USERNAME,
                PASSWORD,
                trainingDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());


        verify(trainingFacade).addTraining(
                eq(TRAINEE_USERNAME),
                eq(PASSWORD),
                eq(trainingDto)
        );

        assertEquals(TRAINING_NAME, trainingDto.getName());
        assertEquals(TRAINING_DATE, trainingDto.getDate());
        assertEquals(TRAINING_DURATION, trainingDto.getDuration());
    }
}
