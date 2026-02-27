package com.epam.gymcrm.service;

import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.util.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private TrainerService trainerService;

    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeService();
        traineeService.setTraineeRepository(traineeRepository);
        traineeService.setUserService(userService);
        traineeService.setAuthentication(authentication);
        traineeService.setTrainerService(trainerService);
    }

    @Test
    void changeTraineePassword_updatesPasswordWhenCredentialsAreValid() {
        String username = "trainee.user";
        String password = "oldPass";
        String newPassword = "newPass";

        User user = new User();
        user.setPassword(password);
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(authentication.auth(username, password)).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.changeTraineePassword(username, password, newPassword);

        assertEquals(newPassword, trainee.getUser().getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void changeTraineePassword_throwsWhenCredentialsAreInvalid() {
        String username = "trainee.user";
        String password = "wrongPass";

        when(authentication.auth(username, password)).thenReturn(false);

        assertThrows(
                AuthenticationFailedException.class,
                () -> traineeService.changeTraineePassword(username, password, "newPass")
        );
        verify(traineeRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getUnassignedTrainersForTrainee_returnsOnlyUnassignedTrainers() {
        String username = "trainee.user";
        String password = "pass";

        User user = new User();
        user.setPassword(password);

        Trainer assignedTrainer = new Trainer();
        Trainer unassignedTrainer = new Trainer();

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setTrainers(new java.util.ArrayList<>(List.of(assignedTrainer)));

        when(authentication.auth(username, password)).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerService.getAllTrainers()).thenReturn(List.of(assignedTrainer, unassignedTrainer));

        List<Trainer> result = traineeService.getUnassignedTrainersForTrainee(username, password);

        assertEquals(1, result.size());
        assertEquals(unassignedTrainer, result.get(0));
    }
}
