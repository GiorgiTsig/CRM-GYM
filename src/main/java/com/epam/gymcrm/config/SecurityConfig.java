package com.epam.gymcrm.config;

import com.epam.gymcrm.util.LogoutSuccessHandler;
import com.epam.gymcrm.util.JwtService;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final LogoutSuccessHandler customLogoutSuccessHandler;
    private final JwtService jwtService;
    private final JwtLogoutCheckFilter jwtLogoutCheckFilter;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Value("${security.cors.allowed-origin}")
    private List<String> corsAllowedOrigin;

    @Value("${security.cors.methods}")
    private List<String> methods;

    @Value("${security.cors.headers}")
    private List<String> header;

    @Value("${security.cors.credentials}")
    private boolean credentials;

    public SecurityConfig(
            LogoutSuccessHandler customLogoutSuccessHandler,
            JwtService jwtService,
            JwtLogoutCheckFilter jwtLogoutCheckFilter,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) {
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.jwtService = jwtService;
        this.jwtLogoutCheckFilter = jwtLogoutCheckFilter;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/token")
                        .successHandler(jwtService)
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST, "/trainee/profile").permitAll()
                        .requestMatchers(HttpMethod.POST, "/trainer/profile").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtLogoutCheckFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt((jwt) -> jwt
                                .decoder(jwtService.jwtDecoder())
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(credentials);
        config.setAllowedOrigins(corsAllowedOrigin);
        config.setAllowedMethods(methods);
        config.setAllowedHeaders(header);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
