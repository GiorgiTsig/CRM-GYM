package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.ActiveDto;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainee.request.TraineeTrainingsRequestDto;
import com.epam.gymcrm.dto.trainee.request.TraineeUpdateRequestDto;
import com.epam.gymcrm.dto.trainee.request.TraineeTrainerAssignmentRequestDto;
import com.epam.gymcrm.dto.trainee.request.CreateTraineeDto;
import com.epam.gymcrm.dto.trainee.response.TraineeProfileDto;
import com.epam.gymcrm.dto.trainee.response.TraineeTrainingDto;
import com.epam.gymcrm.dto.trainee.response.TrainerDto;
import com.epam.gymcrm.facade.TraineeFacade;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {
    @Mock
    private TraineeFacade traineeFacade;

    @Mock
    private TrainingFacade trainingFacade;

    @InjectMocks
    private TraineeController traineeController;

    private static final String USERNAME = "john";
    private static final String PASSWORD = "123";

    @Test
    void create_shouldReturnCreatedResponse() {
        CreateTraineeDto traineeDto = new CreateTraineeDto();
        traineeDto.setFirstName("John");
        traineeDto.setLastName("Doe");

        AuthenticationDto authenticationDto = new AuthenticationDto();

        when(traineeFacade.createTraineeProfile(traineeDto)).thenReturn(authenticationDto);

        ResponseEntity<AuthenticationDto> response = traineeController.create(traineeDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(authenticationDto, response.getBody());

        verify(traineeFacade).createTraineeProfile(traineeDto);
    }

    @Test
    void traineeProfile_shouldReturnTraineeDto_whenCredentialsValid(){
        String traineeProfile = "ad";
        TraineeProfileDto traineeDto = new TraineeProfileDto();
        traineeDto.setFirstName(USERNAME);

        when(traineeFacade.getTraineeProfile(USERNAME, PASSWORD, traineeProfile)).thenReturn(traineeDto);

        ResponseEntity<TraineeProfileDto> response =
                traineeController.traineeProfile(USERNAME, PASSWORD, traineeProfile, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeDto, response.getBody());

        verify(traineeFacade).getTraineeProfile(USERNAME, PASSWORD, traineeProfile);
    }


    @Test
    void updateTraineeProfile_shouldReturnUpdatedTraineeDto_whenProfileUpdated() {
        String firstName = "john";
        String lastName = "moh";
        LocalDate dateOfBirth = LocalDate.of(2026, 2, 2);
        String address = "Fr";

        TraineeUpdateRequestDto traineeUpdateRequestDto = new TraineeUpdateRequestDto();

        traineeUpdateRequestDto.setUsername(USERNAME);
        traineeUpdateRequestDto.setPassword(PASSWORD);
        traineeUpdateRequestDto.setFirstName(firstName);
        traineeUpdateRequestDto.setLastName(lastName);
        traineeUpdateRequestDto.setDateOfBirth(dateOfBirth);
        traineeUpdateRequestDto.setAddress(address);
        traineeUpdateRequestDto.setActive(true);

        TraineeProfileDto traineeDto = new TraineeProfileDto();

        traineeDto.setFirstName(USERNAME);
        traineeDto.setFirstName(firstName);
        traineeDto.setLastName(lastName);
        traineeDto.setAddress(address);
        traineeDto.setDateOfBirth(dateOfBirth);
        traineeDto.setActive(true);


        when(traineeFacade.updateTraineeProfile(USERNAME, PASSWORD, firstName, lastName, LocalDate.of(2026, 2, 2), address, true)).thenReturn(traineeDto);

        ResponseEntity<TraineeProfileDto> response = traineeController.updateTraineeProfile(traineeUpdateRequestDto, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeDto, response.getBody());

        verify(traineeFacade).updateTraineeProfile(USERNAME, PASSWORD, firstName, lastName, LocalDate.of(2026, 2, 2), address, true);
    }

    @Test
    void deleteTraineeProfile_ShouldReturnOk_whenCredentialsValid() {
        AuthenticationDto authController = new AuthenticationDto();
        authController.setUsername(USERNAME);
        authController.setPassword(PASSWORD);

        doNothing()
                .when(traineeFacade)
                .deleteTrainee(USERNAME, PASSWORD);

        ResponseEntity<Void> response =
                traineeController.deleteTraineeProfile(authController, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getActiveTrainersNotAssignedToTrainee_shouldReturnTrainerDtoList_whenCredentialsValid() {
        TrainerDto trainerDto1 = new TrainerDto();
        trainerDto1.setUsername("trainer.one");

        TrainerDto trainerDto2 = new TrainerDto();
        trainerDto2.setUsername("trainer.two");

        List<TrainerDto> trainers = List.of(trainerDto1, trainerDto2);

        when(traineeFacade.getUnassignedTrainersForTrainee(USERNAME, PASSWORD)).thenReturn(trainers);

        ResponseEntity<List<TrainerDto>> response =
                traineeController.getActiveTrainersNotAssignedToTrainee(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(trainerDto1, trainerDto2), response.getBody());

        verify(traineeFacade).getUnassignedTrainersForTrainee(USERNAME, PASSWORD);
    }

    @Test
    void updateTraineeTrainers_shouldReturnTrainerDtoList_whenUpdateSuccessful() {
        Set<String> trainerUsernames = Set.of("trainer.one", "trainer.two");
        TraineeTrainerAssignmentRequestDto trainerList = new TraineeTrainerAssignmentRequestDto();
        trainerList.setUsername(USERNAME);
        trainerList.setPassword(PASSWORD);
        trainerList.setTrainerUsernames(trainerUsernames);
        TrainerDto dto1 = new TrainerDto();
        dto1.setUsername("trainer.one");

        TrainerDto dto2 = new TrainerDto();
        dto2.setUsername("trainer.two");

        List<TrainerDto> trainers = List.of(dto1, dto2);

        when(traineeFacade.updateTraineeTrainers(USERNAME, PASSWORD, trainerUsernames))
                .thenReturn(trainers);

        ResponseEntity<List<TrainerDto>> response =
                traineeController.updateTraineeTrainers(trainerList, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(dto1, dto2), response.getBody());

        verify(traineeFacade).updateTraineeTrainers(USERNAME, PASSWORD, trainerUsernames);
    }

    @Test
    void getTraineeTrainingsList_shouldReturnTrainingDtoList_whenRequestIsValid() {
        TraineeTrainingsRequestDto traineeTrainingsRequestDtoDto = new TraineeTrainingsRequestDto();
        traineeTrainingsRequestDtoDto.setFromDate(LocalDate.of(2026, 1, 1));
        traineeTrainingsRequestDtoDto.setToDate(LocalDate.of(2026, 2, 1));
        traineeTrainingsRequestDtoDto.setTrainerUsername("pw");
        traineeTrainingsRequestDtoDto.setTrainingType("MMA");

        TraineeTrainingDto trainingDto1 = new TraineeTrainingDto();
        TraineeTrainingDto trainingDto2 = new TraineeTrainingDto();
        List<TraineeTrainingDto> trainings = List.of(trainingDto1, trainingDto2);

        when(trainingFacade.getTraineeTrainings(
                USERNAME,
                PASSWORD,
                traineeTrainingsRequestDtoDto.getTraineeUsername(),
                traineeTrainingsRequestDtoDto.getFromDate(),
                traineeTrainingsRequestDtoDto.getToDate(),
                traineeTrainingsRequestDtoDto.getTrainerUsername(),
                traineeTrainingsRequestDtoDto.getTrainingType()
        )).thenReturn(trainings);


        ResponseEntity<List<TraineeTrainingDto>> response =
                traineeController.getTraineeTrainingsList(USERNAME, PASSWORD, traineeTrainingsRequestDtoDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(trainingDto1, trainingDto2), response.getBody());

        verify(trainingFacade).getTraineeTrainings(
                USERNAME,
                PASSWORD,
                traineeTrainingsRequestDtoDto.getTraineeUsername(),
                traineeTrainingsRequestDtoDto.getFromDate(),
                traineeTrainingsRequestDtoDto.getToDate(),
                traineeTrainingsRequestDtoDto.getTrainerUsername(),
                traineeTrainingsRequestDtoDto.getTrainingType()
        );
    }

    @Test
    void updateTraineeStatus_shouldActivateTrainee_whenIsActiveTrue() {
        ActiveDto authController = new ActiveDto();
        authController.setUsername(USERNAME);
        authController.setPassword(PASSWORD);
        authController.setActive(true);

        doNothing().when(traineeFacade).activateTrainee(authController.getUsername(), authController.getPassword());

        ResponseEntity<Void> response =
                traineeController.updateTraineeStatus(authController);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(traineeFacade).activateTrainee(USERNAME, PASSWORD);
        verify(traineeFacade, never()).deactivateTrainee(USERNAME, PASSWORD);
    }

    @Test
    void updateTraineeStatus_shouldDeactivateTrainee_whenIsActiveFalse() {
        ActiveDto authController = new ActiveDto();
        authController.setUsername(USERNAME);
        authController.setPassword(PASSWORD);
        authController.setActive(false);

        doNothing().when(traineeFacade).deactivateTrainee(authController.getUsername(), authController.getPassword());

        ResponseEntity<Void> response =
                traineeController.updateTraineeStatus(authController);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(traineeFacade).deactivateTrainee(authController.getUsername(), authController.getPassword());
        verify(traineeFacade, never()).activateTrainee(authController.getUsername(), authController.getPassword());
    }
}