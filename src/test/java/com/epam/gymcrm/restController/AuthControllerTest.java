package com.epam.gymcrm.restController;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.auth.AuthenticationDto;
import com.epam.gymcrm.service.UserService;
import com.epam.gymcrm.util.Authentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword";

    @Mock
    private Authentication authentication;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void auth_shouldReturnOk_whenCredentialsValid() {
        AuthenticationDto authControllerDto = new AuthenticationDto();
        authControllerDto.setUsername(USERNAME);
        authControllerDto.setPassword(PASSWORD);

        when(authentication.auth(authControllerDto.getUsername(), authControllerDto.getPassword())).thenReturn(true);

        ResponseEntity<Void> response = authController.auth(authControllerDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(authentication).auth(authControllerDto.getUsername(), authControllerDto.getPassword());
    }

    @Test
    void changePassword_shouldReturnOkMessage_whenPasswordUpdated() {
        AuthenticationDto authControllerDto = new AuthenticationDto();
        authControllerDto.setUsername(USERNAME);
        authControllerDto.setPassword(PASSWORD);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        doReturn(true).when(authentication).auth(USERNAME, PASSWORD);
        doReturn(user).when(userService).updatePassword(USERNAME, NEW_PASSWORD);

        ResponseEntity<String> response =
                authController.changePassword(authControllerDto, NEW_PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());

        verify(authentication).auth(USERNAME, PASSWORD);
        verify(userService).updatePassword(USERNAME, NEW_PASSWORD);
    }
}
