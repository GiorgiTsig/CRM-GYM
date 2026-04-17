package com.epam.gymcrm.client;

import com.epam.gymcrm.dto.token.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-server")
public interface AuthClient {

    @PostMapping(value = "/oauth2/token", consumes = "application/x-www-form-urlencoded")
    TokenResponse getToken(
            @RequestHeader("Authorization") String basicAuth,
            @RequestBody String body
    );
}
