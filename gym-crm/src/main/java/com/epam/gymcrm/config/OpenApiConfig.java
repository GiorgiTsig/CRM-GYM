package com.epam.gymcrm.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    private SecurityScheme createBearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createBearerScheme()))
                .paths(new Paths()
                        .addPathItem("/auth/logout", new PathItem().post(
                                new Operation()
                                        .summary("Logout")
                                        .description("Logs out the current user. Requires Bearer token. " +
                                                "Spring Security handles this endpoint.")
                                        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                                        .responses(new ApiResponses()
                                                .addApiResponse("200", new ApiResponse().description("Logout successful"))
                                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))))))
                .info(new Info()
                        .title("My REST API")
                        .description("Some custom description of API.")
                        .license(new License().name("License of API").url("API license URL")));
    }


}
