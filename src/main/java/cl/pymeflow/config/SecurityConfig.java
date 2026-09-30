package cl.pymeflow.config;

import cl.pymeflow.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"No autenticado\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Acceso denegado\"}"
                            );
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        // Login público
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // Necesario para el manejo correcto de errores
                        .requestMatchers("/error")
                        .permitAll()

                        // Gestión de usuarios:
                        // solo administradores de empresa
                        .requestMatchers("/api/usuarios/**")
                        .hasRole("ADMIN_EMPRESA")

                        // Crear nuevas empresas queda bloqueado
                        // hasta implementar onboarding de tenants
                        .requestMatchers(HttpMethod.POST, "/api/empresas")
                        .denyAll()

                        // Resto de operaciones de empresa:
                        // solo administradores
                        .requestMatchers("/api/empresas/**")
                        .hasRole("ADMIN_EMPRESA")

                        // Cualquier otro endpoint requiere autenticación
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}