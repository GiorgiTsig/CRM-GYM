package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TraineeService;
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

    @InjectMocks
    private TraineeFacade traineeFacade;

    @Test
    void createTraineeProfileDelegatesToService() {
        User user = new User();
        Trainee trainee = new Trainee();
        when(traineeService.createTraineeProfile(user, trainee)).thenReturn(trainee);

        Trainee result = traineeFacade.createTraineeProfile(user, trainee);

        assertSame(trainee, result);
        verify(traineeService).createTraineeProfile(user, trainee);
    }

    @Test
    void getTraineeProfileAuthenticatesBeforeFetching() {
        Trainee trainee = new Trainee();
        when(traineeService.authenticateTrainee(USERNAME, PASSWORD)).thenReturn(true);
        when(traineeService.getTrainee(USERNAME)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeFacade.getTraineeProfile(USERNAME, PASSWORD);

        assertTrue(result.isPresent());
        assertSame(trainee, result.get());
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
        List<Trainer> trainers = List.of(new Trainer());
        when(traineeService.authenticateTrainee(USERNAME, PASSWORD)).thenReturn(true);
        when(traineeService.getUnassignedTrainersForTrainee(USERNAME)).thenReturn(trainers);

        List<Trainer> result = traineeFacade.getUnassignedTrainersForTrainee(USERNAME, PASSWORD);

        assertEquals(trainers, result);
        InOrder inOrder = inOrder(traineeService);
        inOrder.verify(traineeService).authenticateTrainee(USERNAME, PASSWORD);
        inOrder.verify(traineeService).getUnassignedTrainersForTrainee(USERNAME);
    }

    @Test
    void forwardsUpdateProfileAndPasswordAndActivationCalls() {
        LocalDate dob = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";

        traineeFacade.updateTraineeProfile(USERNAME, PASSWORD, "John", "Doe", dob, address);
        traineeFacade.changeTraineePassword(USERNAME, PASSWORD, "newPass");
        traineeFacade.activateTrainee(USERNAME, PASSWORD);
        traineeFacade.deactivateTrainee(USERNAME, PASSWORD);
        traineeFacade.deleteTrainee(USERNAME, PASSWORD);

        verify(traineeService).updateTraineeProfile(USERNAME, PASSWORD, "John", "Doe", dob, address);
        verify(traineeService).changeTraineePassword(USERNAME, PASSWORD, "newPass");
        verify(traineeService).activateTrainee(USERNAME, PASSWORD);
        verify(traineeService).deactivateTrainee(USERNAME, PASSWORD);
        verify(traineeService).deleteTrainee(USERNAME, PASSWORD);
    }
}
