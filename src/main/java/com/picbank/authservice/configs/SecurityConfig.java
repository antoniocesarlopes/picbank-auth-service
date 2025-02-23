package com.picbank.authservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the authentication and authorization of API endpoints.
 * <p>
 * This class configures Spring Security settings, including CSRF protection,
 * authentication requirements, and public API endpoints.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * - Disables CSRF protection for simplicity.
     * - Allows public access to API documentation and authentication endpoints.
     * - Requires authentication for all other requests.
     * </p>
     *
     * @param http The {@link HttpSecurity} object to configure security settings.
     * @return A configured {@link SecurityFilterChain} instance.
     * @throws Exception If an error occurs while building the security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/auth/register",
                                "/auth/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
