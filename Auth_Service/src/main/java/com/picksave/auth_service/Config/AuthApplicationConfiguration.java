package com.picksave.auth_service.Config;

import com.picksave.auth_service.Repository.UserRepository;
import com.picksave.auth_service.Service.CustomUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class AuthApplicationConfiguration {
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthApplicationConfiguration(UserRepository userRepository, CustomUserDetailsService customUserDetailsService){
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public CommandLineRunner checkSecurityChains(List<SecurityFilterChain> chains) {
        return args -> {
            System.out.println("Registered SecurityFilterChains at auth-server: ✅✅✅✅" + chains.size());
            chains.forEach(c -> System.out.println(c));
        };
    }
}
