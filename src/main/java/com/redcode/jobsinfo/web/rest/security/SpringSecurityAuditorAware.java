package com.redcode.jobsinfo.web.rest.security;

import com.redcode.jobsinfo.config.Constants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        //String userName = SecurityUtils.getCurrentUserName();
        String userName = null; // need to change further
        userName = (userName != null ? userName : Constants.SYSTEM_ACCOUNT);
        return Optional.of(userName);
    }
}