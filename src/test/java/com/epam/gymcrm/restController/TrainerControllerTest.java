package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerTrainingsDto;
import com.epam.gymcrm.dto.trainer.TrainingDto;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private static final String USERNAME = "trainer.user";
    private static final String PASSWORD = "password123";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final String SPECIALIZATION = "MMA";

    @Mock
    private TrainerFacade trainerFacade;

    @Mock
    private TrainingFacade trainingFacade;

    @InjectMocks
    private TrainerController trainerController;

    @Test
    void create_shouldReturnCreatedResponse_whenTrainerIsValid() {
        CreateTrainerDto createTrainerDto = new CreateTrainerDto();

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(SPECIALIZATION);

        Trainer createdTrainer = new Trainer();
        createdTrainer.setUser(user);
        createdTrainer.setTrainingType(trainingType);

        when(trainerFacade.createTrainerProfile(createTrainerDto)).thenReturn(createdTrainer);

        ResponseEntity<String> response = trainerController.create(createTrainerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registration successful " + USERNAME + " " + PASSWORD, response.getBody());

        verify(trainerFacade).createTrainerProfile(createTrainerDto);
    }

    @Test
    void getTrainerProfile_shouldReturnTrainerDto_whenCredentialsValid() {
        TrainerDto trainerDto = new TrainerDto();

        when(trainerFacade.getTrainerProfile(USERNAME, PASSWORD)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response =
                trainerController.getTrainerProfile(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerDto, response.getBody());

        verify(trainerFacade).getTrainerProfile(USERNAME, PASSWORD);
    }

    @Test
    void updateTraineeProfile_shouldReturnTrainerDto_whenUpdateSuccessful() {
        User user = new User();
        user.setUsername(USERNAME);

        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setFirstName(FIRST_NAME);
        trainerDto.setLastName(LAST_NAME);
        trainerDto.setSpecialization(SPECIALIZATION);
        trainerDto.setActive(true);

        when(trainerFacade.updateTrainerProfile(
                USERNAME,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME,
                true,
                SPECIALIZATION
        )).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response =
                trainerController.updateTraineeProfile(
                        USERNAME,
                        PASSWORD,
                        trainerDto,
                        null
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerDto, response.getBody());

        verify(trainerFacade).updateTrainerProfile(
                USERNAME,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME,
                true,
                SPECIALIZATION
        );
    }

    @Test
    void getTrainerTrainingsList_shouldReturnTrainingDtoList_whenRequestIsValid() {
        TrainerTrainingsDto requestDto = new TrainerTrainingsDto();
        requestDto.setFromDate(LocalDate.of(2026, 1, 1));
        requestDto.setToDate(LocalDate.of(2026, 2, 1));
        requestDto.setTraineeName("Jane");

        TrainingDto trainingDto1 = new TrainingDto();
        TrainingDto trainingDto2 = new TrainingDto();
        List<com.epam.gymcrm.dto.trainer.TrainingDto> trainings = List.of(trainingDto1, trainingDto2);

        when(trainingFacade.getTrainerTrainings(
                USERNAME,
                PASSWORD,
                requestDto.getFromDate(),
                requestDto.getToDate(),
                requestDto.getTraineeName()
        )).thenReturn(trainings);


        ResponseEntity<List<TrainingDto>> response =
                trainerController.getTrainerTrainingsList(USERNAME, PASSWORD, requestDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(trainingDto1, trainingDto2), response.getBody());

        verify(trainingFacade).getTrainerTrainings(
                USERNAME,
                PASSWORD,
                requestDto.getFromDate(),
                requestDto.getToDate(),
                requestDto.getTraineeName()
        );
    }

    @Test
    void updateTraineeStatus_shouldActivateTrainer_whenIsActiveTrue() {
        doNothing().when(trainerFacade).activateTrainer(USERNAME, PASSWORD);

        ResponseEntity<Void> response =
                trainerController.updateTraineeStatus(USERNAME, PASSWORD, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(trainerFacade).activateTrainer(USERNAME, PASSWORD);
        verify(trainerFacade, never()).deactivateTrainer(USERNAME, PASSWORD);
    }

    @Test
    void updateTraineeStatus_shouldDeactivateTrainer_whenIsActiveFalse() {
        doNothing().when(trainerFacade).deactivateTrainer(USERNAME, PASSWORD);

        ResponseEntity<Void> response =
                trainerController.updateTraineeStatus(USERNAME, PASSWORD, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(trainerFacade).deactivateTrainer(USERNAME, PASSWORD);
        verify(trainerFacade, never()).activateTrainer(USERNAME, PASSWORD);
    }
}
