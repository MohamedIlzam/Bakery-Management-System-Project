package com.example.KodikaraGroupBusinessManagementApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8081",
                "http://localhost:5173",
                "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setAllowCredentials(true);
        configuration
                .setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        // Report Creation(only for owner/admin)
                        .requestMatchers("/api/fair-delivery-reports/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")
                        .requestMatchers("/api/shop-supply-reports/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")
                        // Owner Only Manage Data
                        .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/drivers/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_OWNER", "ADMIN")
                        .requestMatchers("/api/drivers/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/shops/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_OWNER", "ADMIN")
                        .requestMatchers("/api/shops/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")
                        // Salesman should be able to see products(not create/delete/update)
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_OWNER", "ADMIN")
                        .requestMatchers("/api/products/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")
                        // Salesman needs to READ (GET) their assignments
                        .requestMatchers(HttpMethod.GET, "/api/shop-supplies/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_DRIVER", "ROLE_OWNER", "ADMIN")
                        // Salesman needs to UPDATE (PUT) and CREATE (POST) shop assignments
                        .requestMatchers(HttpMethod.POST, "/api/shop-supplies/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/shop-supplies/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_OWNER", "ADMIN")
                        // Salesman CANNOT Delete (DELETE) - Only Owner can
                        .requestMatchers("/api/shop-supplies/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")

                        // Fair delivery only for Admin
                        .requestMatchers(HttpMethod.GET, "/api/fair-deliveries/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_DRIVER", "ROLE_OWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/fair-deliveries/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_OWNER", "ADMIN")
                        .requestMatchers("/api/fair-deliveries/**").hasAnyAuthority("ROLE_OWNER", "ADMIN")

                        .requestMatchers("/api/salesman/**")
                        .hasAnyAuthority("ROLE_SALESMAN", "ROLE_DRIVER", "ROLE_OWNER", "ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}