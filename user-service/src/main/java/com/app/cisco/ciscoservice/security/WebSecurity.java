package com.app.cisco.ciscoservice.security;

import com.app.cisco.ciscoservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private Environment environment;

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(Environment environment, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.environment = environment;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.addFilter(digestAuthenticationFilter())              // register digest entry point
                .exceptionHandling().authenticationEntryPoint(digestEntryPoint())       // on exception ask for digest authentication
                .and()
                .authorizeRequests()
                .antMatchers( "/users/register").permitAll() // /home will be accessible directly, no need of any authentication
                .and()
                .httpBasic()                      // it indicate basic authentication is requires
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();

    }

    DigestAuthenticationFilter digestAuthenticationFilter() throws Exception {
        DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
        digestAuthenticationFilter.setUserDetailsService(userService);
        digestAuthenticationFilter.setAuthenticationEntryPoint(digestEntryPoint());
        return digestAuthenticationFilter;
    }

    @Bean
    DigestAuthenticationEntryPoint digestEntryPoint() {
        DigestAuthenticationEntryPoint bauth = new DigestAuthenticationEntryPoint();
        bauth.setRealmName("Digest WF Realm");
        bauth.setKey("MySecureKey");
        return bauth;
    }



}
