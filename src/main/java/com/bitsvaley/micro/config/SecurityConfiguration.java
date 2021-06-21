package com.bitsvaley.micro.config;

import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.impl.UserDetailsServiceImpl;
import com.bitsvaley.micro.services.UserRoleService;
import com.bitsvaley.micro.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The SecurityConfiguration program implements security configurations
 *
 * @author  Fru Chifen
 * @version 1.0
 * @since   2021-06-10
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;


    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth)
            throws Exception {

//        auth.inMemoryAuthentication()
//                .passwordEncoder(NoOpPasswordEncoder.getInstance())
//                .withUser("admin").password("admin")
//                .roles("USER", "ADMIN");


        auth.userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/login","/landing", "/h2-console/**").permitAll()
                .antMatchers("/", "/*todo*/**").access("hasRole('ADMIN')").and()
                .formLogin()
                .defaultSuccessUrl("/welcome", true)
                .loginPage("/login")
//                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler());

        http.csrf().disable();
        http.headers().frameOptions().disable();
    }


    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSuccessHandler();
    }
}
