package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.exception.EntityNotFoundException;
import com.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDtoServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUser_whenUserExists_returnsUser() {
        String username = "John.Doe";

        User user = new User();
        user.setUsername(username);

        when(userRepository.getUsersByUsername("John.Doe")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUser("John.Doe");

        assertTrue(result.isPresent());
        verify(userRepository).getUsersByUsername("John.Doe");
    }

    @Test
    void getUser_whenUserNotFound_returnsEmpty(){
        String username = "John.Doe";

        when(userRepository.getUsersByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUser(username);

        assertTrue(result.isEmpty());
        verify(userRepository).getUsersByUsername(username);
    }

    @Test
    void updatePassword_whenUserExists_updatesAndSavesUser() {
        String username = "John.Doe";
        String newPassword = "new-secret";
        User user = new User();
        user.setUsername(username);
        user.setPassword("old");

        when(userRepository.getUsersByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updatePassword(username, newPassword);

        assertEquals(newPassword, updatedUser.getPassword());
        verify(userRepository).getUsersByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_whenUserMissing_throwsEntityNotFoundException() {
        String username = "missing.user";

        when(userRepository.getUsersByUsername(username)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updatePassword(username, "new-secret"));
        verify(userRepository).getUsersByUsername(username);
    }
}