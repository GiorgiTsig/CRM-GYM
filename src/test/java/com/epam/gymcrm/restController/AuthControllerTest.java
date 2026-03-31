package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.request.ChangePasswordRequestDto;
import com.epam.gymcrm.service.UserService;
import com.epam.gymcrm.util.AuthenticationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword";

    @Mock
    private AuthenticationUtil authentication;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void auth_shouldReturnOk_whenCredentialsValid() {
        when(authentication.auth(USERNAME, PASSWORD)).thenReturn(true);

        ResponseEntity<Void> response = authController.auth(USERNAME, PASSWORD, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(authentication).auth(USERNAME, PASSWORD);
    }

    @Test
    void changePassword_shouldReturnOkMessage_whenPasswordUpdated() {
        ChangePasswordRequestDto authControllerDto = new ChangePasswordRequestDto();
        authControllerDto.setNewPassword(NEW_PASSWORD);
        Authentication authentication = mock(Authentication.class);
        UserDetails username = mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(username);

        when(username.getUsername()).thenReturn(USERNAME);


        ResponseEntity<String> response =
                authController.changePassword(authentication, authControllerDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());

        verify(userService).updatePassword(USERNAME, NEW_PASSWORD);
    }
}
