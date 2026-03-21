package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.dto.trainee.*;
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

        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("12345");

        Trainee createdTrainee = new Trainee();
        createdTrainee.setUser(user);

        when(traineeFacade.createTraineeProfile(traineeDto)).thenReturn(createdTrainee);

        ResponseEntity<String> response = traineeController.create(traineeDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registration successful John.Doe 12345", response.getBody());

        verify(traineeFacade).createTraineeProfile(traineeDto);
    }

    @Test
    void traineeProfile_shouldReturnTraineeDto_whenCredentialsValid(){
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName(USERNAME);

        when(traineeFacade.getTraineeProfile(USERNAME, PASSWORD)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response =
                traineeController.traineeProfile(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeDto, response.getBody());

        verify(traineeFacade).getTraineeProfile(USERNAME, PASSWORD);
    }


    @Test
    void updateTraineeProfile_shouldReturnUpdatedTraineeDto_whenProfileUpdated() {
        String firstName = "john";
        String lastName = "moh";
        LocalDate dateOfBirth = LocalDate.of(2026, 2, 2);
        String address = "Fr";

        Trainee trainee = new Trainee();
        User user = new User();

        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        trainee.setUser(user);
        trainee.getUser().setFirstName(firstName);
        trainee.getUser().setLastName(firstName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        TraineeDto traineeDto = new TraineeDto();

        traineeDto.setFirstName(USERNAME);
        traineeDto.setFirstName(firstName);
        traineeDto.setLastName(lastName);
        traineeDto.setAddress(address);
        traineeDto.setDateOfBirth(dateOfBirth);
        traineeDto.setActive(true);


        when(traineeFacade.updateTraineeProfile(USERNAME, PASSWORD, firstName, lastName, LocalDate.of(2026, 2, 2), address, true)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response = traineeController.updateTraineeProfile(USERNAME, PASSWORD, null, traineeDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeDto, response.getBody());

        verify(traineeFacade).updateTraineeProfile(USERNAME, PASSWORD, firstName, lastName, LocalDate.of(2026, 2, 2), address, true);
    }

    @Test
    void deleteTraineeProfile_ShouldReturnOk_whenCredentialsValid() {
        doNothing()
                .when(traineeFacade)
                .deleteTrainee(USERNAME, PASSWORD);

        ResponseEntity<Void> response =
                traineeController.deleteTraineeProfile(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getActiveTrainersNotAssignedToTrainee_shouldReturnTrainerDtoList_whenCredentialsValid() {
        TrainerDto trainerDto1 = new TrainerDto();
        UserDto userDto = new UserDto();
        trainerDto1.setUser(userDto);
        trainerDto1.getUser().setUsername("trainer.one");

        TrainerDto trainerDto2 = new TrainerDto();
        UserDto userDto1 = new UserDto();
        trainerDto2.setUser(userDto1);
        trainerDto2.getUser().setUsername("trainer.two");

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
        TrainerListDto trainerListDto = new TrainerListDto();
        trainerListDto.setTrainerUsernames(trainerUsernames);
        TrainerDto dto1 = new TrainerDto();
        UserDto userDto = new UserDto();
        dto1.setUser(userDto);
        dto1.getUser().setUsername("trainer.one");

        TrainerDto dto2 = new TrainerDto();
        UserDto userDto1 = new UserDto();
        dto2.setUser(userDto1);
        dto2.getUser().setUsername("trainer.two");

        List<TrainerDto> trainers = List.of(dto1, dto2);

        when(traineeFacade.updateTraineeTrainers(USERNAME, PASSWORD, trainerListDto))
                .thenReturn(trainers);

        ResponseEntity<List<TrainerDto>> response =
                traineeController.updateTraineeTrainers(USERNAME, PASSWORD, trainerListDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(dto1, dto2), response.getBody());

        verify(traineeFacade).updateTraineeTrainers(USERNAME, PASSWORD, trainerListDto);
    }

    @Test
    void getTraineeTrainingsList_shouldReturnTrainingDtoList_whenRequestIsValid() {
        TraineeTrainingsDto requestDto = new TraineeTrainingsDto();
        requestDto.setFromDate(LocalDate.of(2026, 1, 1));
        requestDto.setToDate(LocalDate.of(2026, 2, 1));
        requestDto.setTrainingType("MMA");

        TrainingDto trainingDto1 = new TrainingDto();
        TrainingDto trainingDto2 = new TrainingDto();
        List<TrainingDto> trainings = List.of(trainingDto1, trainingDto2);

        when(trainingFacade.getTraineeTrainings(
                USERNAME,
                PASSWORD,
                requestDto.getFromDate(),
                requestDto.getToDate(),
                requestDto.getTrainingType()
        )).thenReturn(trainings);


        ResponseEntity<List<TrainingDto>> response =
                traineeController.getTraineeTrainingsList(USERNAME, PASSWORD, requestDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(trainingDto1, trainingDto2), response.getBody());

        verify(trainingFacade).getTraineeTrainings(
                USERNAME,
                PASSWORD,
                requestDto.getFromDate(),
                requestDto.getToDate(),
                requestDto.getTrainingType()
        );
    }

    @Test
    void updateTraineeStatus_shouldActivateTrainee_whenIsActiveTrue() {
        doNothing().when(traineeFacade).activateTrainee(USERNAME, PASSWORD);

        ResponseEntity<Void> response =
                traineeController.updateTraineeStatus(USERNAME, PASSWORD, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(traineeFacade).activateTrainee(USERNAME, PASSWORD);
        verify(traineeFacade, never()).deactivateTrainee(USERNAME, PASSWORD);
    }

    @Test
    void updateTraineeStatus_shouldDeactivateTrainee_whenIsActiveFalse() {
        doNothing().when(traineeFacade).deactivateTrainee(USERNAME, PASSWORD);

        ResponseEntity<Void> response =
                traineeController.updateTraineeStatus(USERNAME, PASSWORD, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(traineeFacade).deactivateTrainee(USERNAME, PASSWORD);
        verify(traineeFacade, never()).activateTrainee(USERNAME, PASSWORD);
    }
}