package com.infoworks.lab.webapp.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TO Enable Post/Put/Delete http Methods:
        /*http
            .csrf()
            .disable()
            .requiresChannel()
            .anyRequest()
            .requiresSecure();*/
        //
        http
            .requiresChannel()
            .anyRequest()
            .requiresSecure();
    }
}
