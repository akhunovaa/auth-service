package com.botmasterzzz.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import com.botmasterzzz.auth.listener.CustomAccessDeniedHandler;
import com.botmasterzzz.auth.listener.RefererRedirectionAuthenticationSuccessHandler;

import javax.sql.DataSource;

@EnableWebSecurity(debug = true)
@Configuration
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    ///** Exception handler - Custom - Authentication entry point */
    //@Autowired
    //private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /** Exception handler - Custom - Access denied */
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler ;

    @Autowired
    private DataSource dataSource;

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }


    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username, password, is_enabled from sec_user where username=?")
                .authoritiesByUsernameQuery("select username, role_name from sec_user_role where username=?");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.sessionManagement().maximumSessions(100).maxSessionsPreventsLogin(true);
        http.authorizeRequests()
                .antMatchers("/login", "/login/**", "/oauth/authorize", "/oauth/token", "/oauth/check_token", "/oauth/confirm_access", "/oauth/error")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .successHandler(new RefererRedirectionAuthenticationSuccessHandler());
        http.csrf().disable().exceptionHandling().accessDeniedHandler(this.customAccessDeniedHandler);
        http.authorizeRequests().and().formLogin();
        http.authorizeRequests().and().logout().logoutUrl("/j_spring_security_logout");
    }
}