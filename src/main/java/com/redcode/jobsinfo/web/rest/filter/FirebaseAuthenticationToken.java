package com.redcode.jobsinfo.web.rest.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class FirebaseAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String token;

    public FirebaseAuthenticationToken(final String token) {
        super(null, null);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
