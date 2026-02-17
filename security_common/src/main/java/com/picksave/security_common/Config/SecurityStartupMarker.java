package com.picksave.security_common.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecurityStartupMarker {
    private static final Logger log = LoggerFactory.getLogger(SecurityStartupMarker.class);

    public SecurityStartupMarker() {
        log.info("✅ security_common package loaded by Spring Boot");
    }
}
