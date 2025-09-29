package com.picksave.security_common.Config;


import com.picksave.security_common.Model.AuthenticatedUser;
import com.picksave.security_common.Service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    public JwtAuthenticationFilter(@Qualifier("handlerExceptionResolver")
            HandlerExceptionResolver handlerExceptionResolver,
            JwtService jwtService
    ) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            logger.info("JWT: {}", jwt);
            logger.info("Extracted email: {}", userEmail);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(userEmail != null && authentication == null) {

                if(jwtService.isTokenValid(jwt)) {
                    logger.info("JWT is valid. Setting authentication context.");

                    Claims claims = jwtService.extractAllClaims(jwt);
                    String role = claims.get("role", String.class);
                    List<String> authoritiesFromToken = claims.get("authorities", List.class);
                    List<SimpleGrantedAuthority> authorities = authoritiesFromToken.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();
                    logger.info("Authorities from token: {}", authorities);

                    AuthenticatedUser principal = new AuthenticatedUser(userEmail, role);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Setting authentication with authorities: {}", authToken.getAuthorities());

                    if(jwtService.tokenWillExpireSoon(jwt)){
                        String newJwt = jwtService.generateToken(userEmail);
                        response.setHeader("Authorization", "Bearer " + newJwt);
                    }
                } else {
                    logger.warn("JWT is not valid for user: {}", userEmail);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            logger.error("JWT filter error", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
