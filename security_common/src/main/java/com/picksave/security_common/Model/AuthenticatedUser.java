package com.picksave.security_common.Model;

import lombok.Data;


@Data
public class AuthenticatedUser {
    private String email;
    private String role;

    public AuthenticatedUser(String email, String role) {
        this.email = email;
        this.role = role;
    }
}
