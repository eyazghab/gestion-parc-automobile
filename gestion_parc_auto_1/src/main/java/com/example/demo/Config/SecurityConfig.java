package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()              // désactive la protection CSRF
            .authorizeHttpRequests()
            .anyRequest().permitAll()      // permet toutes les requêtes sans authentification
            .and()
            .httpBasic().disable()         // désactive le login HTTP Basic
            .formLogin().disable();        // désactive la page de login par formulaire

        return http.build();
    }
}
