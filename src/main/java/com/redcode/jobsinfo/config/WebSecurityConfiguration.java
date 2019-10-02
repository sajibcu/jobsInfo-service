package com.redcode.jobsinfo.config;

import com.redcode.jobsinfo.common.utils.SecurityUtils;
import com.redcode.jobsinfo.web.rest.filter.FirebaseAuthenticationTokenFilter;
import com.redcode.jobsinfo.web.rest.security.Http401UnauthorizedEntryPoint;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDetailsService userDetailsService;

    private FirebaseAuthenticationProvider authenticationProvider;


    private final CorsFilter corsFilter;
    private final Http401UnauthorizedEntryPoint unauthorizedEntryPoint;

    @Autowired
    public WebSecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService, CorsFilter corsFilter, Http401UnauthorizedEntryPoint unauthorizedEntryPoint, FirebaseAuthenticationProvider authenticationProvider) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.corsFilter = corsFilter;
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
        this.authenticationProvider = authenticationProvider;
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }


    public FirebaseAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        FirebaseAuthenticationTokenFilter authenticationTokenFilter = new FirebaseAuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        authenticationTokenFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
        });
        return authenticationTokenFilter;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                //.antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/swagger-ui/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/v2/api-docs/**")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf()
                .disable()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedEntryPoint)
                .accessDeniedHandler(unauthorizedEntryPoint)
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers( "/api/users/**").permitAll()
                .antMatchers("/api/**").authenticated()
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return SecurityUtils.getPasswordEncoder();
    }

}
