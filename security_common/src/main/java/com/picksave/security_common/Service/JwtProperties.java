package com.picksave.security_common.Service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "security.jwt")
@Validated
@Getter
@Setter
public class JwtProperties {
    @NotBlank
    private String secretKey;

    @Positive
    private long expirationTime;

}
