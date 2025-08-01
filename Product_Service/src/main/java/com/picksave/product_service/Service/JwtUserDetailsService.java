package com.picksave.product_service.Service;

import com.picksave.security_common.Service.AuthUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements AuthUserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(email)
                .password("")
                .authorities(new ArrayList<>())
                .build();
    }
}
