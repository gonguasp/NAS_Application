package com.nas.configuration;

import com.nas.service.security.JpaAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    JpaAuthenticationService jpaAuthenticationService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jpaAuthenticationService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(
                "/webjars/**",
                "/css/**",
                "/js/**",
                "/login**",
                "/register**/**",
                "/forgotPassword**",
                "/registrationConfirm**",
                "/resetPassword**",
                "/v3/api-docs/**",
                "/swagger-ui**/**",
                "/swagger**/**"
        ).permitAll()
                .and().formLogin().loginPage("/login").defaultSuccessUrl("/welcome", true)
                .and().logout().logoutUrl("/logout")
                .and().csrf().disable();

        http.authorizeRequests().and().httpBasic();
        http.authorizeRequests().antMatchers("/welcome").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    public boolean isLogged() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
    }

    public UserDetails getLoggedUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}