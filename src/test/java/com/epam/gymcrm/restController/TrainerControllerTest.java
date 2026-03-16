package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.trainer.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerDto;
import com.epam.gymcrm.dto.trainer.TrainerTrainingsDto;
import com.epam.gymcrm.dto.trainer.TrainingDto;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.mappper.TrainerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    private TrainerMapper trainerMapper;

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

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(trainingType);

        Trainer createdTrainer = new Trainer();
        createdTrainer.setUser(user);
        createdTrainer.setTrainingType(trainingType);

        when(trainerMapper.toTrainer(createTrainerDto)).thenReturn(trainer);
        when(trainerFacade.createTrainerProfile(user, trainer, SPECIALIZATION)).thenReturn(createdTrainer);

        ResponseEntity<String> response = trainerController.create(createTrainerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registration successful " + USERNAME + " " + PASSWORD, response.getBody());

        verify(trainerMapper).toTrainer(createTrainerDto);
        verify(trainerFacade).createTrainerProfile(user, trainer, SPECIALIZATION);
    }

    @Test
    void getTrainerProfile_shouldReturnTrainerDto_whenCredentialsValid() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        TrainerDto trainerDto = new TrainerDto();

        when(trainerFacade.getTrainerProfile(USERNAME, PASSWORD)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response =
                trainerController.getTrainerProfile(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerDto, response.getBody());

        verify(trainerFacade).getTrainerProfile(USERNAME, PASSWORD);
        verify(trainerMapper).toTrainerDto(trainer);
    }

    @Test
    void updateTraineeProfile_shouldReturnTrainerDto_whenUpdateSuccessful() {
        User user = new User();
        user.setUsername(USERNAME);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        TrainerDto trainerDto = new TrainerDto();

        when(trainerFacade.updateTrainerProfile(
                USERNAME,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME,
                true,
                SPECIALIZATION
        )).thenReturn(trainer);

        when(trainerMapper.toTrainerDto(trainer)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response =
                trainerController.updateTraineeProfile(
                        USERNAME,
                        PASSWORD,
                        FIRST_NAME,
                        LAST_NAME,
                        SPECIALIZATION,
                        true,
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
        verify(trainerMapper).toTrainerDto(trainer);
    }

    @Test
    void getTrainerTrainingsList_shouldReturnTrainingDtoList_whenRequestIsValid() {
        TrainerTrainingsDto requestDto = new TrainerTrainingsDto();
        requestDto.setFromDate(LocalDate.of(2026, 1, 1));
        requestDto.setToDate(LocalDate.of(2026, 2, 1));
        requestDto.setTraineeName("Jane");

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> trainings = List.of(training1, training2);

        TrainingDto trainingDto1 = new TrainingDto();
        TrainingDto trainingDto2 = new TrainingDto();

        when(trainingFacade.getTrainerTrainings(
                USERNAME,
                PASSWORD,
                requestDto.getFromDate(),
                requestDto.getToDate(),
                requestDto.getTraineeName()
        )).thenReturn(trainings);

        when(trainerMapper.toTrainingDto(training1)).thenReturn(trainingDto1);
        when(trainerMapper.toTrainingDto(training2)).thenReturn(trainingDto2);

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
        verify(trainerMapper).toTrainingDto(training1);
        verify(trainerMapper).toTrainingDto(training2);
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
