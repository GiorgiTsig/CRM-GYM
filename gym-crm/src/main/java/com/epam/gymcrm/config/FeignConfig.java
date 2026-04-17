package com.epam.gymcrm.config;

import com.epam.gymcrm.service.TokenService;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    private final TokenService tokenService;

    public FeignConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String token = tokenService.getToken();
            String correlationId = MDC.get("correlationId");
            template.header("X-Correlation-Id", correlationId);
            template.header("Authorization", "Bearer " + token);
        };
    }
}
