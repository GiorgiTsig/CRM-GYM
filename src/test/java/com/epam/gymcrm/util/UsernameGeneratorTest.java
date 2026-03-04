package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsernameGenerator usernameGenerator;

    @Test
    void returnsBaseUsernameWhenNotTaken() {
        when(userRepository.getUsersByUsername("john.doe")).thenReturn(Optional.empty());

        String username = usernameGenerator.generateUsername("john", "doe");

        assertEquals("john.doe", username);
    }

    @Test
    void appendsSerialNumberUntilFreeUsernameFound() {
        when(userRepository.getUsersByUsername("john.doe"))
                .thenReturn(Optional.of(new User()));
        when(userRepository.getUsersByUsername("john.doe1"))
                .thenReturn(Optional.of(new User()));
        when(userRepository.getUsersByUsername("john.doe2"))
                .thenReturn(Optional.empty());

        String username = usernameGenerator.generateUsername("john", "doe");

        assertEquals("john.doe2", username);
    }
}
