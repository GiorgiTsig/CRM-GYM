package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.trainee.CreateTraineeDto;
import com.epam.gymcrm.dto.trainee.TraineeDto;
import com.epam.gymcrm.dto.trainee.TrainerDto;
import com.epam.gymcrm.mappper.TraineeMapper;
import com.epam.gymcrm.service.TraineeService;
import com.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TraineeFacadeTest {

    private static final String USERNAME = "jdoe";
    private static final String PASSWORD = "pass";

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeFacade traineeFacade;

    @Test
    void createTraineeProfileDelegatesToService() {
        User user = new User();
        Trainee trainee = new Trainee();
        CreateTraineeDto createTraineeDto = new CreateTraineeDto();
        trainee.setUser(user);
        when(traineeMapper.toTrainee(createTraineeDto)).thenReturn(trainee);
        when(traineeService.createTraineeProfile(trainee)).thenReturn(trainee);

        Trainee result = traineeFacade.createTraineeProfile(createTraineeDto);

        assertSame(trainee, result);
        verify(traineeService).createTraineeProfile(trainee);
    }

    @Test
    void getTraineeProfileAuthenticatesBeforeFetching() {
        Trainee trainee = new Trainee();
        TraineeDto traineeDto = new TraineeDto();
        when(traineeService.authenticateTrainee(USERNAME, PASSWORD)).thenReturn(true);
        when(traineeService.getTrainee(USERNAME)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = traineeFacade.getTraineeProfile(USERNAME, PASSWORD);

        assertSame(traineeDto, result);
        InOrder inOrder = inOrder(traineeService);
        inOrder.verify(traineeService).authenticateTrainee(USERNAME, PASSWORD);
        inOrder.verify(traineeService).getTrainee(USERNAME);
    }

    @Test
    void updateTraineeTrainersDelegatesAllArguments() {
        Set<String> newTrainers = Set.of("t1", "t2");

        traineeFacade.updateTraineeTrainers(USERNAME, PASSWORD, newTrainers);

        verify(traineeService).updateTraineeTrainers(USERNAME, PASSWORD, newTrainers);
    }

    @Test
    void getUnassignedTrainersAuthenticatesAndReturnsList() {
        Trainer trainer = new Trainer();
        List<Trainer> trainers = List.of(trainer);

        TrainerDto trainerDto = new TrainerDto();
        List<TrainerDto> expectedDtoList = List.of(trainerDto);

        when(traineeService.authenticateTrainee(USERNAME, PASSWORD)).thenReturn(true);
        when(trainerService.getUnassignedTrainersForTrainee(USERNAME)).thenReturn(trainers);
        when(traineeMapper.toTrainerDto(trainer)).thenReturn(trainerDto);

        List<TrainerDto> result = traineeFacade.getUnassignedTrainersForTrainee(USERNAME, PASSWORD);
        assertEquals(expectedDtoList, result);

        InOrder inOrder = inOrder(traineeService, trainerService);
        inOrder.verify(traineeService).authenticateTrainee(USERNAME, PASSWORD);
        inOrder.verify(trainerService).getUnassignedTrainersForTrainee(USERNAME);
    }

    @Test
    void forwardsUpdateProfileAndPasswordAndActivationCalls() {
        LocalDate dob = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";

        traineeFacade.updateTraineeProfile(USERNAME, PASSWORD, "John", "Doe", dob, address, true);
        traineeFacade.changeTraineePassword(USERNAME, PASSWORD, "newPass");
        traineeFacade.activateTrainee(USERNAME, PASSWORD);
        traineeFacade.deactivateTrainee(USERNAME, PASSWORD);
        traineeFacade.deleteTrainee(USERNAME, PASSWORD);

        verify(traineeService).updateTraineeProfile(USERNAME, PASSWORD, "John", "Doe", dob, address, true);
        verify(traineeService).changeTraineePassword(USERNAME, PASSWORD, "newPass");
        verify(traineeService).activateTrainee(USERNAME, PASSWORD);
        verify(traineeService).deactivateTrainee(USERNAME, PASSWORD);
        verify(traineeService).deleteTrainee(USERNAME, PASSWORD);
    }
}
