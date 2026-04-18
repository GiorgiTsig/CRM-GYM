package com.epam.gymcrm.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;

@Configuration
public class FeignConfig {

    private final OAuth2AuthorizedClientManager manager;

    public FeignConfig(OAuth2AuthorizedClientManager manager) {
        this.manager = manager;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String correlationId = MDC.get("correlationId");

            OAuth2AuthorizeRequest request =
                    OAuth2AuthorizeRequest.withClientRegistrationId("my-client")
                            .principal("feign-client")
                            .build();

            OAuth2AuthorizedClient client = manager.authorize(request);
            String token = client.getAccessToken().getTokenValue();

            if (correlationId != null) {
                template.header("X-Correlation-Id", correlationId);
            }

            template.header("Authorization", "Bearer " + token);
        };
    }
}