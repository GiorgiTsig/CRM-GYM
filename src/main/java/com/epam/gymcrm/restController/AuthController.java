package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.request.ChangePasswordRequestDto;
import com.epam.gymcrm.service.UserService;
import com.epam.gymcrm.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/auth")
@Tag(name = "Auth", description = "Authentication and password management operations")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationUtil authenticationUtil;
    private final UserService userService;

    public AuthController(
            UserService userService,
            AuthenticationUtil authenticationUtil
    ) {
        this.authenticationUtil = authenticationUtil;
        this.userService = userService;
    }

    @GetMapping("/login")
    @Operation(summary = "Authenticate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> auth(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        authenticationUtil.auth(authUsername, authPassword);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    @Operation(summary = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password change request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<String> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequestDto credentials,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);

        UserDetails username = (UserDetails) authentication.getPrincipal();
        userService.updatePassword(username.getUsername(), credentials.getNewPassword());

        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
    }
}
