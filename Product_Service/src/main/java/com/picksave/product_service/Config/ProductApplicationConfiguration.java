package com.picksave.product_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
public class ProductApplicationConfiguration {

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                throw new UnsupportedOperationException("Error: authentication not supported in product_service");
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return false;
            }
        };
    }
}