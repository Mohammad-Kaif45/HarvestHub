package com.harvesthub.app.config;

import com.harvesthub.app.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 🟢 FIX IS HERE: Added "/verify-otp"
                        .requestMatchers("/", "/login", "/register", "/register/save", "/verify-otp", "/style.css", "/css/**", "/images/**").permitAll()

                        .requestMatchers("/farmer/**").hasRole("FARMER")
                        .requestMatchers("/retail/**").hasRole("RETAIL")
                        .requestMatchers("/wholesale/**").hasRole("WHOLESALE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
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