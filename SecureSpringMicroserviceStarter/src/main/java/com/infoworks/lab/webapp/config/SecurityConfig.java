package com.infoworks.lab.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${app.disable.security}")
    private boolean disableSecurity;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public static final String[] URL_WHITELIST = {
            "/v2/api-docs"
            , "/swagger-ui.html"
            , "/swagger-ui.html/**"
            , "/webjars/springfox-swagger-ui/**"
            , "/swagger-resources/**"
            , "/swagger-resources/configuration/**"
            , "/actuator/health"
            , "/actuator/prometheus"
            , "/h2-console/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .requiresChannel().anyRequest().requiresSecure() //enable for Https
                .and()
                //.authorizeRequests().anyRequest().authenticated() //enable to restrict all
                .authorizeRequests().antMatchers("/**").permitAll() //enable to open all
                .and()
                .addFilterBefore(
                        (disableSecurity ? new ByPassAuthorizationFilter() : new AuthorizationFilter())
                        , BasicAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(URL_WHITELIST);
    }

}
