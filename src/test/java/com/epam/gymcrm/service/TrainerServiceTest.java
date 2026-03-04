package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.util.Authentication;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private Authentication authentication;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void createTrainerProfile_generatesCredentialsAndLinksEntities() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        Trainer trainer = new Trainer();
        TrainingType yoga = new TrainingType("YOGA");

        when(passwordGenerator.generatePassword()).thenReturn("secret");
        when(usernameGenerator.generateUsername("John", "Doe")).thenReturn("john.doe");
        when(trainingTypeRepository.findTrainingTypeByTrainingTypeName("YOGA")).thenReturn(yoga);

        Trainer result = trainerService.createTrainerProfile(user, trainer, "YOGA");

        assertEquals("secret", user.getPassword());
        assertEquals("john.doe", user.getUsername());
        assertSame(user, trainer.getUser());
        assertSame(trainer, user.getTrainer());
        assertSame(yoga, trainer.getTrainingType());
        assertSame(trainer, result);
        verify(trainerRepository).save(trainer);
    }

    @Test
    void authenticateTrainer_returnsTrueWhenTrainerExists() {
        String username = "trainer.user";
        String password = "good";

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(new Trainer()));

        assertTrue(trainerService.authenticateTrainer(username, password));
    }

    @Test
    void authenticateTrainer_returnsFalseWhenTrainerMissing() {
        String username = "trainer.user";
        String password = "good";

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.empty());

        assertFalse(trainerService.authenticateTrainer(username, password));
    }

    @Test
    void changeTrainerPassword_updatesPasswordWhenCredentialsAreValid() {
        String username = "trainer.user";
        String password = "oldPass";
        String newPassword = "newPass";

        User user = new User();
        user.setPassword(password);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.changeTrainerPassword(username, password, newPassword);

        assertEquals(newPassword, trainer.getUser().getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void changeTrainerPassword_throwsWhenCredentialsAreInvalid() {
        String username = "trainer.user";
        String password = "wrongPass";

        when(authentication.auth(username, password)).thenReturn(false);

        assertThrows(
                AuthenticationFailedException.class,
                () -> trainerService.changeTrainerPassword(username, password, "newPass")
        );
        verify(trainerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateTrainerProfile_updatesNamesAndSpecialization() {
        String username = "trainer.user";
        String password = "pass";
        Trainer trainer = new Trainer();
        User user = new User();
        trainer.setUser(user);
        TrainingType newType = new TrainingType("CARDIO");

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findTrainingTypeByTrainingTypeName("CARDIO")).thenReturn(newType);

        trainerService.updateTrainerProfile(username, password, "Jane", "Smith", "CARDIO");

        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertSame(newType, trainer.getTrainingType());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void deleteTrainer_removesTrainerAfterSuccessfulAuth() {
        String username = "trainer.user";
        String password = "pass";
        UUID trainerId = UUID.randomUUID();

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(new Trainer()));

        trainerService.deleteTrainer(trainerId, username, password);

        verify(trainerRepository).deleteTrainerById(trainerId);
    }

    @Test
    void activateTrainer_whenAlreadyActive_throwsIllegalStateException() {
        String username = "trainer.user";
        String password = "pass";
        User user = new User();
        user.setActive(true);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.activateTrainer(username, password));
    }

    @Test
    void activateTrainer_setsActiveAndSavesWhenInactive() {
        String username = "trainer.user";
        String password = "pass";
        User user = new User();
        user.setActive(false);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.activateTrainer(username, password);

        assertTrue(user.isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void deactivateTrainer_whenAlreadyInactive_throwsIllegalStateException() {
        String username = "trainer.user";
        String password = "pass";
        User user = new User();
        user.setActive(false);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.deactivateTrainer(username, password));
    }

    @Test
    void deactivateTrainer_setsInactiveAndSavesWhenActive() {
        String username = "trainer.user";
        String password = "pass";
        User user = new User();
        user.setActive(true);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(authentication.auth(username, password)).thenReturn(true);
        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deactivateTrainer(username, password);

        assertFalse(user.isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void getAllTrainers_returnsRepositoryResult() {
        var trainers = java.util.List.of(new Trainer(), new Trainer());
        when(trainerRepository.findAll()).thenReturn(trainers);

        assertEquals(trainers, trainerService.getAllTrainers());
    }

    @Test
    void trainingType_returnsRepositoryValue() {
        TrainingType yoga = new TrainingType("YOGA");
        when(trainingTypeRepository.findTrainingTypeByTrainingTypeName("YOGA")).thenReturn(yoga);

        assertSame(yoga, trainerService.trainingType("YOGA"));
    }

    @Test
    void getTrainer_whenTrainerExists_returnsTrainer() {
        String username = "Mike.Johnson";

        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername(username);
        trainer.setUser(user);

        when(trainerRepository.getTrainerByUserUsername(username))
                .thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.getTrainer(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUser().getUsername());
        verify(trainerRepository).getTrainerByUserUsername(username);
    }

    @Test
    void getTrainer_whenTrainerNotFound_returnsEmpty() {
        String username = "Mike.Johnson";

        when(trainerRepository.getTrainerByUserUsername(username))
                .thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.getTrainer(username);

        assertTrue(result.isEmpty());
        verify(trainerRepository).getTrainerByUserUsername(username);
    }

    @Test
    void deactivateTrainer_whenTrainerFailedAuth_throwException() {
        String username = "Mike.Johnson";
        String password = "pass";

        when(trainerService.authenticateTrainer(username, password)).thenReturn(false);

        assertThrows(AuthenticationFailedException.class, () -> trainerService.deactivateTrainer(username, password));
    }

    @Test
    void updateTrainerProfile_whenTrainerFailedAuth_throwException() {
        String username = "trainer.user";
        String password = "pass";
        when(trainerService.authenticateTrainer(username, password)).thenReturn(false);
        assertThrows(AuthenticationFailedException.class, () -> trainerService.updateTrainerProfile(username, password, "Jane", "Smith", "CARDIO"));
    }

    @Test
    void activateTrainer_whenTrainerFailedAuth_throwException() {
        String username = "trainer.user";
        String password = "pass";
        when(trainerService.authenticateTrainer(username, password)).thenReturn(false);
        assertThrows(AuthenticationFailedException.class, () -> trainerService.activateTrainer(username, password));
    }

    @Test
    void deleteTrainer_whenTrainerFailedAuth_throwException() {
        String username = "trainer.user";
        String password = "pass";
        UUID id = UUID.randomUUID();
        when(trainerService.authenticateTrainer(username, password)).thenReturn(false);
        assertThrows(AuthenticationFailedException.class, () -> trainerService.deleteTrainer(id, username, password));
    }
}
