package com.epam.gymcrm.restController;

import com.epam.gymcrm.dto.auth.request.ChangePasswordRequestDto;
import com.epam.gymcrm.service.UserService;
import com.epam.gymcrm.util.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private Authentication authentication;
    private UserService userService;

    @Autowired
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    ResponseEntity<Void> auth(
            @RequestHeader("username") String authUsername,
            @RequestHeader("password") String authPassword,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        authentication.auth(authUsername, authPassword);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequestDto credentials,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        authentication.auth(credentials.getUsername(), credentials.getPassword());
        userService.updatePassword(credentials.getUsername(), credentials.getNewPassword());

        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
    }
}
