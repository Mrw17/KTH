package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class AppSecurityConfig  extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/index", "/register/**", "/error", "/login*", "/stylesheets/**", "/lang-logo.png")
                .permitAll()
                .antMatchers("maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css").permitAll()
                .antMatchers("/success").anonymous()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll().defaultSuccessUrl("/defaultLogginPage").failureUrl("/login?error")
                .and().logout().invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/logout-success").permitAll().and().httpBasic();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      //  auth.authenticationProvider(authProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**");
        web.ignoring().antMatchers("/css/**","/fonts/**","/libs/**");
    }



}

