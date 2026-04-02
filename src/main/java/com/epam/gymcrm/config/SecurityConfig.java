package com.epam.gymcrm.config;

import com.epam.gymcrm.util.JwtDecoderService;
import com.epam.gymcrm.util.LogoutSuccessHandler;
import com.epam.gymcrm.util.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import com.epam.gymcrm.util.JwtLogoutCheckFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final LogoutSuccessHandler customLogoutSuccessHandler;
    private final JwtService jwtService;
    private final JwtLogoutCheckFilter jwtLogoutCheckFilter;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final JwtDecoderService jwtDecoderService;

    public SecurityConfig(
            LogoutSuccessHandler customLogoutSuccessHandler,
            JwtService jwtService,
            JwtLogoutCheckFilter jwtLogoutCheckFilter,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            JwtDecoderService jwtDecoderService
    ) {
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.jwtService = jwtService;
        this.jwtLogoutCheckFilter = jwtLogoutCheckFilter;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.jwtDecoderService = jwtDecoderService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/token")
                        .successHandler(jwtService)
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST,"/trainee/profile").permitAll()
                        .requestMatchers(HttpMethod.POST,"/trainer/profile").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtLogoutCheckFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt((jwt) -> jwt
                                .decoder(jwtDecoderService.jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());


        return http.build();
    }
}
