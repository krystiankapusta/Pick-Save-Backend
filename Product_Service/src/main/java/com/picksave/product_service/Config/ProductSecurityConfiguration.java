package com.picksave.product_service.Config;


import com.picksave.security_common.Config.JwtAuthenticationFilter;
import com.picksave.security_common.Config.SecurityConfiguration;
import com.picksave.security_common.Model.Permission;
import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class ProductSecurityConfiguration extends SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;

    public ProductSecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
                                        AuthenticationProvider authenticationProvider) {
        super(jwtAuthenticationFilter, true);
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void configureAuthorization(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").hasAnyAuthority(Permission.ADMIN_READ.getPermission(), Permission.USER_READ.getPermission())
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAnyAuthority(Permission.ADMIN_CREATE.getPermission(), Permission.USER_CREATE.getPermission())
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyAuthority(Permission.ADMIN_UPDATE.getPermission(), Permission.USER_UPDATE.getPermission())
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyAuthority(Permission.ADMIN_DELETE.getPermission())
                        .requestMatchers(HttpMethod.POST, "/external-products/**").hasAnyAuthority(Permission.ADMIN_CREATE.getPermission())
                        .requestMatchers(HttpMethod.GET, "/search/**").hasAnyAuthority(Permission.ADMIN_READ.getPermission(), Permission.USER_READ.getPermission())
                        .anyRequest().authenticated()
                );
    }

    @Override
    protected AuthenticationProvider authenticationProvider() {
        return this.authenticationProvider;
    }
}
