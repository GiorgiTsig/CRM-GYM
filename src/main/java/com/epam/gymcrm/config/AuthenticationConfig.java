package com.epam.gymcrm.config;

import com.epam.gymcrm.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticationConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService customUserDetailsService
    ) {
        DaoAuthenticationProvider authenticationProvider = daoAuthenticationProvider(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(encoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        return new DaoAuthenticationProvider(userDetailsService);
    }
}
