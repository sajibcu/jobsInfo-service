package com.redcode.jobsinfo.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.redcode.jobsinfo.database.entity.model.FirebaseUserDetails;
import com.redcode.jobsinfo.web.rest.filter.FirebaseAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;


@Component
public class FirebaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {


    private FirebaseAuth firebaseAuth;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public FirebaseAuthenticationProvider(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (FirebaseAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {


    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        final FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;

        FirebaseToken decodedToken = null;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(authenticationToken.getToken());
            authentication.setDetails(username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("email: " + decodedToken.getEmail() + "## uid: " + decodedToken.getUid());
            return new FirebaseUserDetails(decodedToken.getEmail(), decodedToken.getUid());
        } catch (FirebaseAuthException e) {
            throw new SessionAuthenticationException(e.getMessage());
        }
    }
}