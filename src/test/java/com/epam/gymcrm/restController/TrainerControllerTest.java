package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.auth.ActiveDto;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerTraineeListItemDto;
import com.epam.gymcrm.dto.trainer.TrainerTrainingsDto;
import com.epam.gymcrm.dto.trainer.TrainerTrainingDto;
import com.epam.gymcrm.dto.trainer.request.TrainerProfileUpdateRequestDto;
import com.epam.gymcrm.dto.trainer.request.TrainerTrainingsRequestDto;
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
        AuthenticationDto authController = new AuthenticationDto();
        authController.setUsername(USERNAME);
        authController.setPassword(PASSWORD);

        TrainerTraineeListItemDto trainerDto = new TrainerTraineeListItemDto();

        when(trainerFacade.getTrainerProfile(authController.getUsername(), authController.getPassword())).thenReturn(trainerDto);

        ResponseEntity<TrainerTraineeListItemDto> response =
                trainerController.getTrainerProfile(authController, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerDto, response.getBody());

        verify(trainerFacade).getTrainerProfile(USERNAME, PASSWORD);
    }

    @Test
    void updateTraineeProfile_shouldReturnTrainerDto_whenUpdateSuccessful() {
        TrainerProfileUpdateRequestDto trainerRequestDto = new TrainerProfileUpdateRequestDto();
        trainerRequestDto.setUsername(USERNAME);
        trainerRequestDto.setPassword(PASSWORD);
        trainerRequestDto.setFirstName(FIRST_NAME);
        trainerRequestDto.setLastName(LAST_NAME);
        trainerRequestDto.setSpecialization(SPECIALIZATION);
        trainerRequestDto.setActive(true);

        TrainerTraineeListItemDto trainerDto = new TrainerTraineeListItemDto();
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

        ResponseEntity<TrainerTraineeListItemDto> response =
                trainerController.updateTraineeProfile(
                        trainerRequestDto,
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

        TrainerTrainingDto trainingDto1 = new TrainerTrainingDto();
        TrainerTrainingDto trainingDto2 = new TrainerTrainingDto();
        List<TrainerTrainingDto> trainings = List.of(trainingDto1, trainingDto2);

        TrainerTrainingsRequestDto trainerTrainingsRequestDto = new TrainerTrainingsRequestDto();
        trainerTrainingsRequestDto.setUsername(USERNAME);
        trainerTrainingsRequestDto.setPassword(PASSWORD);
        trainerTrainingsRequestDto.setFromDate(LocalDate.of(2026, 1, 1));
        trainerTrainingsRequestDto.setToDate(LocalDate.of(2026, 2, 1));
        trainerTrainingsRequestDto.setTraineeName("Jane");

        when(trainingFacade.getTrainerTrainings(
                USERNAME,
                PASSWORD,
                requestDto.getFromDate(),
                requestDto.getToDate(),
                requestDto.getTraineeName()
        )).thenReturn(trainings);


        ResponseEntity<List<TrainerTrainingDto>> response =
                trainerController.getTrainerTrainingsList(trainerTrainingsRequestDto, null);

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
        ActiveDto activeDto = new ActiveDto();
        activeDto.setUsername(USERNAME);
        activeDto.setPassword(PASSWORD);
        activeDto.setActive(true);
        doNothing().when(trainerFacade).activateTrainer(activeDto.getUsername(), activeDto.getPassword());

        ResponseEntity<Void> response =
                trainerController.updateTraineeStatus(activeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(trainerFacade).activateTrainer(activeDto.getUsername(), activeDto.getPassword());
        verify(trainerFacade, never()).deactivateTrainer(activeDto.getUsername(), activeDto.getPassword());
    }

    @Test
    void updateTraineeStatus_shouldDeactivateTrainer_whenIsActiveFalse() {
        ActiveDto activeDto = new ActiveDto();
        activeDto.setUsername(USERNAME);
        activeDto.setPassword(PASSWORD);
        activeDto.setActive(false);
        doNothing().when(trainerFacade).deactivateTrainer(activeDto.getUsername(), activeDto.getPassword());

        ResponseEntity<Void> response =
                trainerController.updateTraineeStatus(activeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(trainerFacade).deactivateTrainer(activeDto.getUsername(), activeDto.getPassword());
        verify(trainerFacade, never()).activateTrainer(activeDto.getUsername(), activeDto.getPassword());
    }
}
