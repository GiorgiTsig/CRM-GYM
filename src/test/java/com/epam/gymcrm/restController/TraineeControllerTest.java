package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.CreateUserDto;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.dto.trainee.*;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.mappper.TraineeMapper;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {
    @Mock
    private TraineeFacade traineeFacade;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainingFacade trainingFacade;

    @InjectMocks
    private TraineeController traineeController;

    private static final String USERNAME = "john";
    private static final String PASSWORD = "123";

    @Test
    void create_shouldReturnCreatedResponse() {
        CreateTraineeDto traineeDto = new CreateTraineeDto();
        CreateUserDto userDto = new CreateUserDto();
        traineeDto.setUser(userDto);
        traineeDto.getUser().setFirstName("John");
        traineeDto.getUser().setLastName("Doe");

        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("12345");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Trainee createdTrainee = new Trainee();
        createdTrainee.setUser(user);

        when(traineeMapper.toTrainee(traineeDto)).thenReturn(trainee);
        when(traineeFacade.createTraineeProfile(user, trainee)).thenReturn(createdTrainee);

        ResponseEntity<String> response = traineeController.create(traineeDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registration successful John.Doe 12345", response.getBody());

        verify(traineeMapper).toTrainee(traineeDto);
        verify(traineeFacade).createTraineeProfile(user, trainee);
    }

    @Test
    void traineeProfile_shouldReturnTraineeDto_whenCredentialsValid(){
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        trainee.setUser(user);

        UserDto userDto = new UserDto();
        TraineeDto traineeDto = new TraineeDto();
        userDto.setUsername(USERNAME);
        traineeDto.setUser(userDto);

        when(traineeFacade.getTraineeProfile(USERNAME, PASSWORD)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response =
                traineeController.traineeProfile(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeDto, response.getBody());

        verify(traineeFacade).getTraineeProfile(USERNAME, PASSWORD);
        verify(traineeMapper).toTraineeDto(trainee);
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
        UserDto userDto = new UserDto();

        userDto.setUsername(USERNAME);
        traineeDto.setUser(userDto);
        traineeDto.setAddress(address);
        traineeDto.setDateOfBirth(dateOfBirth);


        when(traineeFacade.updateTraineeProfile(USERNAME, PASSWORD, firstName, lastName, LocalDate.of(2026, 2, 2), address, true)).thenReturn(trainee);
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response = traineeController.updateTraineeProfile(USERNAME, PASSWORD, firstName, lastName, dateOfBirth, address, true, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(traineeDto, response.getBody());

        verify(traineeFacade).updateTraineeProfile(USERNAME, PASSWORD, firstName, lastName, LocalDate.of(2026, 2, 2), address, true);
        verify(traineeMapper).toTraineeDto(trainee);
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
        Trainer trainer1 = new Trainer();
        User user1 = new User();
        user1.setUsername("trainer.one");
        trainer1.setUser(user1);

        Trainer trainer2 = new Trainer();
        User user2 = new User();
        user2.setUsername("trainer.two");
        trainer2.setUser(user2);

        List<Trainer> trainers = List.of(trainer1, trainer2);

        TrainerDto trainerDto1 = new TrainerDto();
        UserDto userDto = new UserDto();
        trainerDto1.setUser(userDto);
        trainerDto1.getUser().setUsername("trainer.one");

        TrainerDto trainerDto2 = new TrainerDto();
        UserDto userDto1 = new UserDto();
        trainerDto2.setUser(userDto1);
        trainerDto2.getUser().setUsername("trainer.two");

        when(traineeFacade.getUnassignedTrainersForTrainee(USERNAME, PASSWORD))
                .thenReturn(trainers);
        when(traineeMapper.toTrainerDto(trainer1)).thenReturn(trainerDto1);
        when(traineeMapper.toTrainerDto(trainer2)).thenReturn(trainerDto2);

        ResponseEntity<List<TrainerDto>> response =
                traineeController.getActiveTrainersNotAssignedToTrainee(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(trainerDto1, trainerDto2), response.getBody());

        verify(traineeFacade).getUnassignedTrainersForTrainee(USERNAME, PASSWORD);
        verify(traineeMapper).toTrainerDto(trainer1);
        verify(traineeMapper).toTrainerDto(trainer2);
    }

    @Test
    void updateTraineeTrainers_shouldReturnTrainerDtoList_whenUpdateSuccessful() {

        Set<String> trainerUsernames = Set.of("trainer.one", "trainer.two");

        Trainer trainer1 = new Trainer();
        User user1 = new User();
        user1.setUsername("trainer.one");
        trainer1.setUser(user1);

        Trainer trainer2 = new Trainer();
        User user2 = new User();
        user2.setUsername("trainer.two");
        trainer2.setUser(user2);

        List<Trainer> trainers = List.of(trainer1, trainer2);

        TrainerDto dto1 = new TrainerDto();
        UserDto userDto = new UserDto();
        dto1.setUser(userDto);
        dto1.getUser().setUsername("trainer.one");

        TrainerDto dto2 = new TrainerDto();
        UserDto userDto1 = new UserDto();
        dto2.setUser(userDto);
        dto2.getUser().setUsername("trainer.two");

        when(traineeFacade.updateTraineeTrainers(USERNAME, PASSWORD, trainerUsernames))
                .thenReturn(trainers);

        when(traineeMapper.toTrainerDto(trainer1)).thenReturn(dto1);
        when(traineeMapper.toTrainerDto(trainer2)).thenReturn(dto2);

        ResponseEntity<List<TrainerDto>> response =
                traineeController.updateTraineeTrainers(USERNAME, PASSWORD, trainerUsernames, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(dto1, dto2), response.getBody());

        verify(traineeFacade).updateTraineeTrainers(USERNAME, PASSWORD, trainerUsernames);
        verify(traineeMapper).toTrainerDto(trainer1);
        verify(traineeMapper).toTrainerDto(trainer2);
    }

    @Test
    void getTraineeTrainingsList_shouldReturnTrainingDtoList_whenRequestIsValid() {
        TraineeTrainingsDto requestDto = new TraineeTrainingsDto();
        requestDto.setFromDate(LocalDate.of(2026, 1, 1));
        requestDto.setToDate(LocalDate.of(2026, 2, 1));
        requestDto.setTrainingType("MMA");

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> trainings = List.of(training1, training2);

        TrainingDto trainingDto1 = new TrainingDto();
        TrainingDto trainingDto2 = new TrainingDto();

        when(trainingFacade.getTraineeTrainings(
                USERNAME,
                PASSWORD,
                requestDto.getFromDate(),
                requestDto.getToDate(),
                requestDto.getTrainingType()
        )).thenReturn(trainings);

        when(traineeMapper.toTrainingDto(training1)).thenReturn(trainingDto1);
        when(traineeMapper.toTrainingDto(training2)).thenReturn(trainingDto2);

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
        verify(traineeMapper).toTrainingDto(training1);
        verify(traineeMapper).toTrainingDto(training2);
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