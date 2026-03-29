package com.epam.gymcrm.util;

import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtLogoutCheckFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(JwtLogoutCheckFilter.class);
    private final UserService userService;

    public JwtLogoutCheckFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String username = jwt.getSubject();
            Instant issuedAt = jwt.getIssuedAt();

            User user = userService.getUser(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            if (user.getLastLogout() != null && !issuedAt.isAfter(user.getLastLogout())) {
                    sendError(response);
                    return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Helper to ensure consistent error responses in the Filter chain
     */
    private void sendError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", "Token has been revoked via logout"));
    }
}
