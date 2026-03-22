package com.example.hr_managment_system.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/error"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/employee/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/employee/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/employee/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/employee/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/payroll/process").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/payroll/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/performance/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/performance/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/attendance/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/attendance/**").hasAnyRole("ADMIN", "MANAGER")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

