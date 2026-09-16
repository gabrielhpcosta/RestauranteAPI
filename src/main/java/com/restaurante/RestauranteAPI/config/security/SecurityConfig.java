package com.restaurante.RestauranteAPI.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // LOGIN
                        .requestMatchers("/auth/**", "/health").permitAll()


                        // USUÁRIOS
                        .requestMatchers(HttpMethod.GET, "/usuarios/**")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/usuarios")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/usuarios/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**")
                        .hasRole("ADMIN")


                        // CLIENTES
                        .requestMatchers(HttpMethod.GET, "/clientes/**")
                        .hasAnyRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/clientes")
                        .hasAnyRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/clientes/**")
                        .hasAnyRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/clientes/**")
                        .hasRole("ADMIN")


                        // ACTUATOR
                        .requestMatchers("/actuator/health", "/actuator/info")
                        .permitAll()


                        // SWAGGER
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        )
                        .permitAll()


                        // OUTROS ENDPOINTS
                        .anyRequest().authenticated()
                )

                // teste
                .exceptionHandling(handling -> handling

                        // teste 401
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Token ausente, inválido ou expirado"
                                )
                        )

                        // teste 403
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "Sem permissão para este recurso"
                                )
                        )
                )

                // JWT FILTER
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
