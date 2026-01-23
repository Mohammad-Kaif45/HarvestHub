package com.harvesthub.app.config;

import com.harvesthub.app.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. DISABLE CSRF (This fixes the "refresh" issue)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/farmer/**").hasRole("FARMER")
                        .requestMatchers("/retail/**").hasRole("RETAIL")
                        .requestMatchers("/wholesale/**").hasRole("WHOLESALE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // Explicitly tell Spring where to submit the form
                        .successHandler(customSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            if (role.equals("ROLE_FARMER")) {
                response.sendRedirect("/farmer/dashboard");
            } else if (role.equals("ROLE_RETAIL")) {
                response.sendRedirect("/retail/home");
            } else if (role.equals("ROLE_WHOLESALE")) {
                response.sendRedirect("/wholesale/home");
            } else {
                response.sendRedirect("/");
            }
        };
    }
}