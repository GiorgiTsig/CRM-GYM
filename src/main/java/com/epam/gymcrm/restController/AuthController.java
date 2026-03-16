package com.epam.gymcrm.restController;

import com.epam.gymcrm.exception.AuthenticationFailedException;
import com.epam.gymcrm.service.UserService;
import com.epam.gymcrm.util.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        log.info("TransactionId: {}", transactionId);
        authentication.auth(username, password);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password")
    ResponseEntity<String> changePassword(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            @RequestHeader("newpassword") String newPassword,
            @RequestHeader(value = "transactionId", required = false) String transactionId
    ) {
        try {
            log.info("TransactionId: {}", transactionId);
            authentication.auth(username, password);
            userService.updatePassword(username, newPassword);

            return ResponseEntity.status(HttpStatus.OK).body("Successful authentication");
        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid credentials");
        }
    }
}
