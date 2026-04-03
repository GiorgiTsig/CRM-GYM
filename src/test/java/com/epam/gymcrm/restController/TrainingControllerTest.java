package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.dto.TrainingType.TrainingTypeDetailsDto;
import com.epam.gymcrm.dto.trainee.request.TrainingRequestDto;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private static final String TRAINER_USERNAME = "trainer.smith";
    private static final String TRAINING_NAME = "MMA Training";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2026, 3, 17);
    private static final Integer TRAINING_DURATION = 90;

    @Mock
    private TrainingFacade trainingFacade;

    @Mock
    private TrainingTypesFacade trainingTypesFacade;

    @InjectMocks
    private TrainingController trainingController;

    @Test
    void addTraining_shouldReturnOk_whenTrainingCreated() {
        TrainingTypeDto trainingType = new TrainingTypeDto();
        trainingType.setTrainingTypeName("MMA");
        TrainingRequestDto trainingDto = new TrainingRequestDto();
        trainingDto.setTrainerUsername(TRAINER_USERNAME);
        trainingDto.setDate(TRAINING_DATE);
        trainingDto.setDuration(TRAINING_DURATION);
        trainingDto.setName(TRAINING_NAME);
        trainingDto.setType(trainingType);

        doNothing().when(trainingFacade).addTraining(
                eq(trainingDto)
        );

        ResponseEntity<Void> response = trainingController.addTraining(trainingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());


        verify(trainingFacade).addTraining(
                eq(trainingDto)
        );

        assertEquals(TRAINING_NAME, trainingDto.getName());
        assertEquals(TRAINING_DATE, trainingDto.getDate());
        assertEquals(TRAINING_DURATION, trainingDto.getDuration());
    }

    @Test
    void getTrainingType_shouldReturnOkWithBody_whenAuthenticated() {
        TrainingTypeDetailsDto dto = new TrainingTypeDetailsDto();
        dto.setId(UUID.randomUUID());
        dto.setTrainingTypeName("MMA");
        List<TrainingTypeDetailsDto> trainingTypes = List.of(dto);

        when(trainingTypesFacade.findAll()).thenReturn(trainingTypes);

        ResponseEntity<List<TrainingTypeDetailsDto>> response = trainingController.getTrainingType();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainingTypes, response.getBody());
        verify(trainingTypesFacade).findAll();
    }
}
