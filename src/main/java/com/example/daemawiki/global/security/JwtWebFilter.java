package com.example.daemawiki.global.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtWebFilter implements WebFilter {
    private final Tokenizer tokenizer;

    public JwtWebFilter(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return resolveToken(exchange.getRequest())
                .flatMap(token -> {
                    if (tokenizer.verify(token)) {
                        Authentication authentication = tokenizer.getAuthentication(token);
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                    }
                    return chain.filter(exchange);
                }).switchIfEmpty(chain.filter(exchange));
    }

    private Mono<String> resolveToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7));
    }

}