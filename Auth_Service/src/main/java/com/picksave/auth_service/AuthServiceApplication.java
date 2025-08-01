package com.picksave.auth_service;

import com.picksave.security_common.Service.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication(scanBasePackages = {
		"com.picksave.auth_service",
		"com.picksave.security_common"
})
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
