package com.picksave.auth_service.Config;


import com.picksave.security_common.Config.JwtAuthenticationFilter;
import com.picksave.security_common.Config.SecurityConfiguration;
import com.picksave.security_common.Model.Permission;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.HttpMethod;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;


@Configuration
public class AuthSecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    public AuthSecurityConfiguration(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain authServiceSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/verify", "/auth/resend-verification").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(anonymous -> {});

        return http.build();
    }

    @PostConstruct
    public void debugInit() {
        System.out.println("✅ AuthSecurityConfiguration loaded and active ✅");
    }

    @Bean
    public CommandLineRunner debugSecurityChains(List<SecurityFilterChain> chains) {
        return args -> {
            System.out.println("🔍 AUTH SERVICE SecurityFilterChains count = " + chains.size());
            chains.forEach(chain -> System.out.println(" -> " + chain));
        };
    }
}
