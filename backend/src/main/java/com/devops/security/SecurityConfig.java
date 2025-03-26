package com.devops.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 安全配置类
 * @author yux
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/api/users/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/users/**")).hasRole("ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/api/envs/**")).hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers(new AntPathRequestMatcher("/api/servers/**")).hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers(new AntPathRequestMatcher("/api/versions/**")).hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers(new AntPathRequestMatcher("/api/deploy/**")).hasAnyRole("ADMIN", "OPERATOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
} 