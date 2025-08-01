package com.picksave.auth_service.Config;

import com.picksave.security_common.Service.JwtProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class JwtConfigLogger {

    private final JwtProperties jwtProperties;

    public JwtConfigLogger(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void logJwtProperties() {
        System.out.println("JWT Secret Key: " + jwtProperties.getSecretKey());
        System.out.println("JWT Expiration Time: " + jwtProperties.getExpirationTime());
    }
}