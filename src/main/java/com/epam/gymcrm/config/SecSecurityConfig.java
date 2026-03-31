package com.epam.gymcrm.config;

import com.epam.gymcrm.service.CustomUserDetailsService;
import com.epam.gymcrm.util.LogoutSuccessHandler;
import com.epam.gymcrm.util.JwtUtils;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import com.epam.gymcrm.util.JwtLogoutCheckFilter;

import javax.crypto.SecretKey;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecSecurityConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final LogoutSuccessHandler customLogoutSuccessHandler;
    private final JwtUtils jwtUtils;
    private final JwtLogoutCheckFilter jwtLogoutCheckFilter;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public SecSecurityConfig(
            LogoutSuccessHandler customLogoutSuccessHandler,
            JwtUtils jwtUtils,
            JwtLogoutCheckFilter jwtLogoutCheckFilter,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) {
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.jwtUtils = jwtUtils;
        this.jwtLogoutCheckFilter = jwtLogoutCheckFilter;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/token")
                        .successHandler(jwtUtils)
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST,"/trainee/profile").permitAll()
                        .requestMatchers(HttpMethod.POST,"/trainer/profile").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtLogoutCheckFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt((jwt) -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
