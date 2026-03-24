package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.auth.request.ActiveDto;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.request.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.response.TrainerProfileDto;
import com.epam.gymcrm.dto.trainer.response.TrainerTrainingDto;
import com.epam.gymcrm.dto.trainer.request.TrainerProfileUpdateRequestDto;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.util.Authentication;
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

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TrainerController trainerController;

    @Test
    void create_shouldReturnCreatedResponse_whenTrainerIsValid() {
        CreateTrainerDto createTrainerDto = new CreateTrainerDto();
        AuthenticationDto authenticationDto = new AuthenticationDto();

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(SPECIALIZATION);

        Trainer createdTrainer = new Trainer();
        createdTrainer.setUser(user);
        createdTrainer.setTrainingType(trainingType);

        when(trainerFacade.createTrainerProfile(createTrainerDto)).thenReturn(authenticationDto);

        ResponseEntity<AuthenticationDto> response = trainerController.create(createTrainerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(authenticationDto, response.getBody());

        verify(trainerFacade).createTrainerProfile(createTrainerDto);
    }

    @Test
    void getTrainerProfile_shouldReturnTrainerDto_whenCredentialsValid() {
        TrainerProfileDto trainerDto = new TrainerProfileDto();
        when(authentication.auth(USERNAME, PASSWORD)).thenReturn(true);

        when(trainerFacade.getTrainerProfile("trainer")).thenReturn(trainerDto);

        ResponseEntity<TrainerProfileDto> response =
                trainerController.getTrainerProfile(USERNAME, PASSWORD, "trainer", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerDto, response.getBody());

        verify(trainerFacade).getTrainerProfile("trainer");
    }

    @Test
    void updateTraineeProfile_shouldReturnTrainerDto_whenUpdateSuccessful() {
        TrainerProfileUpdateRequestDto trainerRequestDto = new TrainerProfileUpdateRequestDto();
        trainerRequestDto.setUsername(USERNAME);
        trainerRequestDto.setFirstName(FIRST_NAME);
        trainerRequestDto.setLastName(LAST_NAME);
        trainerRequestDto.setActive(true);

        TrainerProfileDto trainerDto = new TrainerProfileDto();
        trainerDto.setFirstName(FIRST_NAME);
        trainerDto.setLastName(LAST_NAME);
        trainerDto.setSpecialization(SPECIALIZATION);
        trainerDto.setActive(true);
        when(authentication.auth(USERNAME, PASSWORD)).thenReturn(true);

        when(trainerFacade.updateTrainerProfile(
                USERNAME,
                FIRST_NAME,
                LAST_NAME,
                true
        )).thenReturn(trainerDto);

        ResponseEntity<TrainerProfileDto> response =
                trainerController.updateTrainerStatus(
                        USERNAME,
                        PASSWORD,
                        trainerRequestDto,
                        null
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerDto, response.getBody());

        verify(trainerFacade).updateTrainerProfile(
                USERNAME,
                FIRST_NAME,
                LAST_NAME,
                true
        );
    }

    @Test
    void getTrainerTrainingsList_shouldReturnTrainingDtoList_whenRequestIsValid() {
        TrainerTrainingDto trainingDto1 = new TrainerTrainingDto();
        TrainerTrainingDto trainingDto2 = new TrainerTrainingDto();
        List<TrainerTrainingDto> trainings = List.of(trainingDto1, trainingDto2);
        when(authentication.auth(USERNAME, PASSWORD)).thenReturn(true);

        when(trainingFacade.getTrainerTrainings(
                "trainer.username",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                "trainee.username"
        )).thenReturn(trainings);


        ResponseEntity<List<TrainerTrainingDto>> response =
                trainerController.getTrainerTrainingsList(
                        USERNAME,
                        PASSWORD,
                        "trainer.username",
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 2, 1),
                        "trainee.username",
                        null
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(trainingDto1, trainingDto2), response.getBody());

        verify(trainingFacade).getTrainerTrainings(
                "trainer.username",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                "trainee.username"
        );
    }

    @Test
    void updateTraineeStatus_shouldActivateTrainer_whenIsActiveTrue() {
        ActiveDto activeDto = new ActiveDto();
        activeDto.setUsername(USERNAME);
        activeDto.setActive(true);
        when(authentication.auth(USERNAME, PASSWORD)).thenReturn(true);
        doNothing().when(trainerFacade).activateTrainer(activeDto.getUsername());

        ResponseEntity<Void> response =
                trainerController.updateTrainerProfile(USERNAME, PASSWORD, activeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(trainerFacade).activateTrainer(activeDto.getUsername());
        verify(trainerFacade, never()).deactivateTrainer(activeDto.getUsername());
    }

    @Test
    void updateTraineeStatus_shouldDeactivateTrainer_whenIsActiveFalse() {
        ActiveDto activeDto = new ActiveDto();
        activeDto.setUsername(USERNAME);
        activeDto.setActive(false);
        when(authentication.auth(USERNAME, PASSWORD)).thenReturn(true);
        doNothing().when(trainerFacade).deactivateTrainer(activeDto.getUsername());

        ResponseEntity<Void> response =
                trainerController.updateTrainerProfile(USERNAME, PASSWORD, activeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(trainerFacade).deactivateTrainer(activeDto.getUsername());
        verify(trainerFacade, never()).activateTrainer(activeDto.getUsername());
    }
}
