package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerFacadeTest {

    private static final String USERNAME = "trainer.user";
    private static final String PASSWORD = "password";
    private static final String TRAINING_TYPE = "Cardio";

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerFacade trainerFacade;

    @Test
    void createTrainerProfileDelegatesToService() {
        User user = new User();
        Trainer trainer = new Trainer();
        when(trainerService.createTrainerProfile(user, trainer, TRAINING_TYPE)).thenReturn(trainer);

        Trainer result = trainerFacade.createTrainerProfile(user, trainer, TRAINING_TYPE);

        assertSame(trainer, result);
        verify(trainerService).createTrainerProfile(user, trainer, TRAINING_TYPE);
    }

    @Test
    void getTrainerProfileAuthenticatesBeforeFetching() {
        Trainer trainer = new Trainer();
        when(trainerService.authenticateTrainer(USERNAME, PASSWORD)).thenReturn(true);
        when(trainerService.getTrainer(USERNAME)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerFacade.getTrainerProfile(USERNAME, PASSWORD);

        assertTrue(result.isPresent());
        assertSame(trainer, result.get());
        InOrder inOrder = inOrder(trainerService);
        inOrder.verify(trainerService).authenticateTrainer(USERNAME, PASSWORD);
        inOrder.verify(trainerService).getTrainer(USERNAME);
    }

    @Test
    void updateTrainerProfileDelegatesAllArguments() {
        trainerFacade.updateTrainerProfile(USERNAME, PASSWORD, "John", "Doe", "Strength");

        verify(trainerService).updateTrainerProfile(USERNAME, PASSWORD, "John", "Doe", "Strength");
    }

    @Test
    void authenticateTrainerReturnsServiceResult() {
        when(trainerService.authenticateTrainer(USERNAME, PASSWORD)).thenReturn(true);

        boolean authenticated = trainerFacade.authenticateTrainer(USERNAME, PASSWORD);

        assertTrue(authenticated);
        verify(trainerService).authenticateTrainer(USERNAME, PASSWORD);
    }

    @Test
    void forwardsPasswordAndActivationCommands() {
        trainerFacade.changeTrainerPassword(USERNAME, PASSWORD, "newPass");
        trainerFacade.activateTrainer(USERNAME, PASSWORD);
        trainerFacade.deactivateTrainer(USERNAME, PASSWORD);

        verify(trainerService).changeTrainerPassword(USERNAME, PASSWORD, "newPass");
        verify(trainerService).activateTrainer(USERNAME, PASSWORD);
        verify(trainerService).deactivateTrainer(USERNAME, PASSWORD);
    }
}
