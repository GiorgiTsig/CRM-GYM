package com.epam.gymcrm.service;

import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.util.Authentication;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private Authentication authentication;
    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TraineeService traineeService;

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

        Trainer unassignedTrainer = new Trainer();

        when(traineeRepository.findUnassignedTrainersByTraineeUsername(username))
                .thenReturn(List.of(unassignedTrainer));

        List<Trainer> result = traineeService.getUnassignedTrainersForTrainee(username);

        assertEquals(1, result.size());
        assertEquals(unassignedTrainer, result.get(0));
    }
}
