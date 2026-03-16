package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Training;
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
        Training training = new Training(TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION);

        when(trainingFacade.addTraining(
                eq(TRAINER_USERNAME),
                eq(PASSWORD),
                eq(TRAINEE_USERNAME),
                any(Training.class)
        )).thenReturn(training);

        ResponseEntity<Void> response = trainingController.addTraining(
                TRAINEE_USERNAME,
                PASSWORD,
                TRAINER_USERNAME,
                TRAINING_NAME,
                TRAINING_DATE,
                TRAINING_DURATION
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);

        verify(trainingFacade).addTraining(
                eq(TRAINER_USERNAME),
                eq(PASSWORD),
                eq(TRAINEE_USERNAME),
                trainingCaptor.capture()
        );

        Training capturedTraining = trainingCaptor.getValue();
        assertEquals(TRAINING_NAME, capturedTraining.getName());
        assertEquals(TRAINING_DATE, capturedTraining.getDate());
        assertEquals(TRAINING_DURATION, capturedTraining.getDuration());
    }
}
