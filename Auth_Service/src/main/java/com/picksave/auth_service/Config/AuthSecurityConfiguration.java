package com.picksave.auth_service.Config;


import com.picksave.security_common.Config.JwtAuthenticationFilter;
import com.picksave.security_common.Config.SecurityConfiguration;
import com.picksave.security_common.Model.Permission;
import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;



@Configuration
public class AuthSecurityConfiguration extends SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    public AuthSecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
                                     AuthenticationProvider authenticationProvider) {
        super(jwtAuthenticationFilter);
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void configureAuthorization(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/verify", "/auth/resend-verification").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/**").hasAnyAuthority(Permission.ADMIN_READ.name(), Permission.USER_READ.name())
                        .requestMatchers(HttpMethod.POST, "/auth/**").hasAnyAuthority(Permission.ADMIN_CREATE.name(), Permission.USER_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/auth/**").hasAnyAuthority(Permission.ADMIN_UPDATE.name(), Permission.USER_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/auth/**").hasAnyAuthority(Permission.ADMIN_DELETE.name(), Permission.USER_DELETE.name())
                        .anyRequest().authenticated()
                );
    }

    @Override
    protected AuthenticationProvider authenticationProvider() {
        return this.authenticationProvider;
    }
}
