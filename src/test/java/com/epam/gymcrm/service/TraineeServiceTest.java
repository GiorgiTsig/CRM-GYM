package com.epam.gymcrm.service;

import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.util.Authentication;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void createTraineeProfile_populatesCredentialsAndLinksEntities() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        user.setTrainee(trainee);

        when(passwordGenerator.generatePassword()).thenReturn("generatedPass");
        when(usernameGenerator.generateUsername("John", "Doe")).thenReturn("john.doe");
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.createTraineeProfile(trainee);

        assertEquals("generatedPass", user.getPassword());
        assertEquals("john.doe", user.getUsername());
        assertEquals(user, trainee.getUser());
        assertEquals(trainee, user.getTrainee());
        assertEquals(trainee, result);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void authenticateTrainee_returnsTrueWhenCredentialsValidAndUserExists() {
        when(authentication.auth("john", "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername("john")).thenReturn(Optional.of(new Trainee()));

        assertTrue(traineeService.authenticateTrainee("john", "pw"));
    }

    @Test
    void authenticateTrainee_throwsWhenCredentialsInvalid() {
        when(authentication.auth("john", "bad")).thenReturn(false);

        assertThrows(AuthenticationFailedException.class, () -> traineeService.authenticateTrainee("john", "bad"));
        verify(traineeRepository, never()).getTraineeByUserUsername("john");
    }

    @Test
    void updateTraineeProfile_updatesFieldsAndSaves() {
        User user = new User();
        user.setFirstName("Old");
        user.setLastName("Name");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(authentication.auth("user", "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername("user"))
                .thenReturn(Optional.of(trainee));

        traineeService.updateTraineeProfile(
                "user",
                "pw",
                "New",
                "Surname",
                java.time.LocalDate.of(1990, 1, 1),
                "New address",
                true
        );

        assertEquals("New", user.getFirstName());
        assertEquals("Surname", user.getLastName());
        assertEquals(java.time.LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("New address", trainee.getAddress());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTraineeTrainers_replacesOldAndAddsNew() {
        String username = "trainee.user";
        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setActive(true);
        trainee.setUser(traineeUser);

        Trainer oldTrainer = new Trainer();
        oldTrainer.setId(UUID.randomUUID());
        User oldUser = new User();
        oldUser.setUsername("old");
        oldTrainer.setUser(oldUser);

        Trainer newTrainer = new Trainer();
        newTrainer.setId(UUID.randomUUID());
        User newUser = new User();
        newUser.setUsername("new");
        newTrainer.setUser(newUser);

        trainee.setTrainers(new ArrayList<>(List.of(oldTrainer)));

        when(authentication.auth(username, "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.of(trainee), Optional.of(trainee));

        when(trainerService.getAllTrainersUserUsername(Set.of("old")))
                .thenReturn(Set.of(oldTrainer));
        when(trainerService.getAllTrainersUserUsername(Set.of("new")))
                .thenReturn(Set.of(newTrainer));

        traineeService.updateTraineeTrainers(username, "pw", Set.of("new"));

        Set<String> resultUsernames = trainee.getTrainers().stream()
                .map(Trainer::getUser)
                .map(User::getUsername)
                .collect(Collectors.toSet());

        assertFalse(resultUsernames.contains("old"));
        assertTrue(resultUsernames.contains("new"));

        verify(trainerService).getAllTrainersUserUsername(Set.of("old"));
        verify(trainerService).getAllTrainersUserUsername(Set.of("new"));
        verify(traineeRepository).save(trainee);
    }

    @Test
    void deleteTrainee_removesById() {
        Trainee trainee = new Trainee();
        trainee.setId(UUID.randomUUID());
        when(authentication.auth("user", "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername("user")).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("user", "pw");

        verify(traineeRepository).deleteTraineeById(trainee.getId());
    }

    @Test
    void activateTrainee_setsActiveWhenInactive() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(false);
        trainee.setUser(user);

        when(authentication.auth("user", "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername("user")).thenReturn(Optional.of(trainee));

        traineeService.activateTrainee("user", "pw");

        assertTrue(user.isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void activateTrainee_throwsWhenAlreadyActive() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(true);
        trainee.setUser(user);

        when(authentication.auth("user", "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername("user")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> traineeService.activateTrainee("user", "pw"));
        verify(traineeRepository, never()).save(trainee);
    }

    @Test
    void deactivateTrainee_setsInactiveWhenActive() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(true);
        trainee.setUser(user);

        when(authentication.auth("user", "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername("user")).thenReturn(Optional.of(trainee));

        traineeService.deactivateTrainee("user", "pw");

        assertFalse(user.isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void deactivateTrainee_throwsWhenAlreadyInactive() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(false);
        trainee.setUser(user);

        when(authentication.auth("user", "pw")).thenReturn(true);
        when(traineeRepository.getTraineeByUserUsername("user")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> traineeService.deactivateTrainee("user", "pw"));
        verify(traineeRepository, never()).save(trainee);
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
    void findTraineeByUsername_whenTraineeExists_returnsTrainee() {
        String username = "trainee.user";
        Trainee trainee = new Trainee();

        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.findTraineeByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
        verify(traineeRepository).getTraineeByUserUsername(username);
    }

    @Test
    void findTraineeByUsername_whenTraineeMissing_returnsEmptyOptional() {
        String username = "trainee.user";

        when(traineeRepository.getTraineeByUserUsername(username)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.findTraineeByUsername(username);

        assertTrue(result.isEmpty());
        verify(traineeRepository).getTraineeByUserUsername(username);
    }
}
