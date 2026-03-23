package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.dto.trainer.request.CreateTrainerDto;
import com.epam.gymcrm.dto.trainer.response.TrainerProfileDto;
import com.epam.gymcrm.mapper.TrainerMapper;
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

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerFacade trainerFacade;

    @Test
    void createTrainerProfileDelegatesToService() {
        User user = new User();
        Trainer trainer = new Trainer();
        CreateTrainerDto createTrainerDto = new CreateTrainerDto();
        TrainingType type = new TrainingType();

        AuthenticationDto authenticationDto = new AuthenticationDto();

        type.setTrainingTypeName(TRAINING_TYPE);
        trainer.setUser(user);
        trainer.setTrainingType(type);
        when(trainerMapper.toTrainer(createTrainerDto)).thenReturn(trainer);
        when(trainerService.createTrainerProfile(user, trainer, TRAINING_TYPE)).thenReturn(trainer);
        when(trainerMapper.toAuth(trainer)).thenReturn(authenticationDto);

        AuthenticationDto result = trainerFacade.createTrainerProfile(createTrainerDto);

        assertSame(authenticationDto, result);
        verify(trainerService).createTrainerProfile(user, trainer, TRAINING_TYPE);
    }

    @Test
    void getTrainerProfileAuthenticatesBeforeFetching() {
        Trainer trainer = new Trainer();
        TrainerProfileDto trainerDto = new TrainerProfileDto();
        when(trainerService.authenticateTrainer(USERNAME, PASSWORD)).thenReturn(true);
        when(trainerService.getTrainer(USERNAME)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(trainerDto);

        TrainerProfileDto result = trainerFacade.getTrainerProfile(USERNAME, PASSWORD);

        assertSame(trainerDto, result);
        InOrder inOrder = inOrder(trainerService);
        inOrder.verify(trainerService).authenticateTrainer(USERNAME, PASSWORD);
        inOrder.verify(trainerService).getTrainer(USERNAME);
    }

    @Test
    void updateTrainerProfileDelegatesAllArguments() {
        trainerFacade.updateTrainerProfile(USERNAME, PASSWORD, "John", "Doe", true,"Strength");

        verify(trainerService).updateTrainerProfile(USERNAME, PASSWORD, "John", "Doe", true,"Strength");
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
