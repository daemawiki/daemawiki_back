package com.example.daemawiki.global.config;

import com.example.daemawiki.global.security.JwtWebFilter;
import com.example.daemawiki.global.security.Tokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final Tokenizer tokenizer;

    public SecurityConfig(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private static final String[] WHITE_LIST = {
            "/api/mail/**",
            "/api/auth/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(a -> a
                        .pathMatchers(WHITE_LIST).permitAll()
                        .anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterBefore(new JwtWebFilter(tokenizer), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

}
