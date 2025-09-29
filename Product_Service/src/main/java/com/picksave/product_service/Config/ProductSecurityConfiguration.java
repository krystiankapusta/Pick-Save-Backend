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
                        .requestMatchers(HttpMethod.GET, "/products/**").hasAnyAuthority(Permission.ADMIN_READ.name(), Permission.USER_READ.name())
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAnyAuthority(Permission.ADMIN_CREATE.name(), Permission.USER_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyAuthority(Permission.ADMIN_UPDATE.name(), Permission.USER_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyAuthority(Permission.ADMIN_DELETE.name())
                        .requestMatchers(HttpMethod.POST, "/external-products/**").hasAnyAuthority(Permission.ADMIN_CREATE.name())
                        .anyRequest().authenticated()
                );
    }

    @Override
    protected AuthenticationProvider authenticationProvider() {
        return this.authenticationProvider;
    }
}
