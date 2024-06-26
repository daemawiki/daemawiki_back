package org.daemawiki.config;

import org.daemawiki.domain.user.model.User;
import org.daemawiki.security.JwtWebFilter;
import org.daemawiki.security.Tokenizer;
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


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(a -> a
                        .pathMatchers(
                                "/api/auth/**",
                                "/api/mail/**").permitAll()
                        .pathMatchers("/api/admin/**").hasAnyRole(
                                User.Role.ADMIN.name(),
                                User.Role.MANAGER.name(),
                                User.Role.OPERATOR.name())
                        .anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterBefore(new JwtWebFilter(tokenizer), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

}
