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

}