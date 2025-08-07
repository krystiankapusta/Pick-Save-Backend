package com.picksave.security_common.Config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public abstract class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final boolean enableJwtFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, boolean enableJwtFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.enableJwtFilter = enableJwtFilter;
    }

    protected abstract void configureAuthorization(HttpSecurity http) throws Exception;

    protected abstract AuthenticationProvider authenticationProvider();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) throws Exception {
        configureAuthorization(httpSecurity);
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (enableJwtFilter && jwtAuthenticationFilter != null) {
            httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${url_frontend}") String url_frontend){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(url_frontend));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
