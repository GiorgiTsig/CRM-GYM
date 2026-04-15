package com.epam.gymcrm.config;

import com.epam.gymcrm.service.CustomUserDetailsService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collection;

@Configuration
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationConverter(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public @Nullable AbstractAuthenticationToken convert(@NonNull Jwt source) {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(source);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(source.getSubject());
        return new PreAuthenticatedAuthenticationToken(userDetails, source, authorities);
    }
}
