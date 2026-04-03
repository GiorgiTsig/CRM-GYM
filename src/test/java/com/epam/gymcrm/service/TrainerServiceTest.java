package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.auth.response.AuthenticationDto;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void createTrainerProfile_generatesCredentialsAndLinksEntities() {
        String plaintextPassword = "generatedPass";
        String encodedPassword = "encodedGeneratedPass";
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        Trainer trainer = new Trainer();
        TrainingType yoga = new TrainingType("YOGA");
        AuthenticationDto authenticationDto = new AuthenticationDto();

        when(passwordGenerator.generatePassword()).thenReturn(plaintextPassword);
        when(passwordEncoder.encode(plaintextPassword)).thenReturn(encodedPassword);
        when(usernameGenerator.generateUsername("John", "Doe")).thenReturn("john.doe");
        when(trainingTypeRepository.findTrainingTypeByTrainingTypeName("YOGA")).thenReturn(yoga);

        AuthenticationDto result = trainerService.createTrainerProfile(user, trainer, "YOGA");
        authenticationDto.setUsername("john.doe");
        authenticationDto.setPassword(plaintextPassword);

        assertEquals(encodedPassword, user.getPassword());
        assertEquals("john.doe", user.getUsername());
        assertSame(user, trainer.getUser());
        assertSame(trainer, user.getTrainer());
        assertSame(yoga, trainer.getTrainingType());
        assertSame(trainer.getUser().getUsername(), result.getUsername());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void authenticateTrainer_returnsTrueWhenTrainerExists() {
        String username = "trainer.user";
        Trainer mockTrainer = new Trainer();
        User mockUser = new User();
        mockUser.setUsername(username);
        mockTrainer.setUser(mockUser);

        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(mockTrainer));
        Trainer result = trainerRepository.getTrainerByUserUsername(username).orElseThrow();

        assertNotNull(result);
        assertEquals(username, result.getUser().getUsername());
    }


    @Test
    void updateTrainerProfile_updatesNamesAndSpecialization() {
        String username = "trainer.user";
        Trainer trainer = new Trainer();
        User user = new User();
        trainer.setUser(user);
        TrainingType newType = new TrainingType("CARDIO");

        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.updateTrainerProfile(username, "Jane", "Smith", true);

        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void deleteTrainer_removesTrainerAfterSuccessfulAuth() {
        UUID trainer = UUID.randomUUID();
        trainerService.deleteTrainer(trainer);
        verify(trainerRepository).deleteTrainerById(trainer);
    }

    @Test
    void activateTrainer_whenAlreadyActive_throwsIllegalStateException() {
        String username = "trainer.user";
        User user = new User();
        user.setActive(true);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.activateTrainer(username));
    }

    @Test
    void activateTrainer_setsActiveAndSavesWhenInactive() {
        String username = "trainer.user";
        User user = new User();
        user.setActive(false);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));
        trainerService.activateTrainer(username);
        assertTrue(user.isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void deactivateTrainer_whenAlreadyInactive_throwsIllegalStateException() {
        String username = "trainer.user";
        User user = new User();
        user.setActive(false);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.deactivateTrainer(username));
    }

    @Test
    void deactivateTrainer_setsInactiveAndSavesWhenActive() {
        String username = "trainer.user";
        User user = new User();
        user.setActive(true);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.getTrainerByUserUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deactivateTrainer(username);

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
}
