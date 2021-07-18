package com.bitsvalley.micro.config;

import com.bitsvalley.micro.impl.UserDetailsServiceImpl;
import com.bitsvalley.micro.services.UserRoleService;
import com.bitsvalley.micro.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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
//                .roles("USER", "ADMIN","AGENT");

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
