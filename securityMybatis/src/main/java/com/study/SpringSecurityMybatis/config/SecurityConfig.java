package com.study.SpringSecurityMybatis.config;

import com.study.SpringSecurityMybatis.security.filter.SecurityFilter;
import com.study.SpringSecurityMybatis.security.handler.AuthenticationHandler;
import com.study.SpringSecurityMybatis.security.handler.Oauth2SuccessHandler;
import com.study.SpringSecurityMybatis.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationHandler authenticationHandler;
    @Autowired
    private SecurityFilter SecurityFilter;
    @Autowired
    private Oauth2SuccessHandler oauth2SuccessHandler;
    @Autowired
    private OAuth2Service oAuth2Service;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable();
        http.httpBasic().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();

        http.oauth2Login()
                        .successHandler(oauth2SuccessHandler)
                                .userInfoEndpoint()
                                        .userService(oAuth2Service);

        http.exceptionHandling().authenticationEntryPoint(authenticationHandler);

        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers("/auth/**", "/h2-console/**")
                .permitAll()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(SecurityFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
