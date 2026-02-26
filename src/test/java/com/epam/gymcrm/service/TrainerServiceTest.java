package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainerDaoImp;
import com.epam.gymcrm.dao.TrainingTypeDao;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.util.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDaoImp trainerDao;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private TrainingTypeDao trainingTypeDao;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setUserService(userService);
        trainerService.setAuthentication(authentication);
        trainerService.setTrainingTypeDao(trainingTypeDao);
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
        when(trainerDao.get(username)).thenReturn(Optional.of(trainer));

        trainerService.changeTrainerPassword(username, password, newPassword);

        assertEquals(newPassword, trainer.getUser().getPassword());
        verify(trainerDao).update(trainer);
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
        verify(trainerDao, never()).update(org.mockito.ArgumentMatchers.any());
    }
}
