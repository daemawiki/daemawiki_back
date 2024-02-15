package com.example.daemawiki.global.security;

import com.example.daemawiki.global.exception.h400.InvalidTokenException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@Component
public class Tokenizer {

    @Value("${jwt.secret}")
    private String secret;

    public Mono<String> createToken(String user) {
        return Mono.fromCallable(() -> tokenize(user));
    }

    private String tokenize(String user) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 3);
        Date expiresAt = calendar.getTime();

        Claims claims = Jwts.claims()
                .setSubject(user);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("url")
                .setExpiration(expiresAt)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Mono<Boolean> verify(String token) {
        return parse(token)
                .map(jws -> true)
                .onErrorReturn(false);
    }

    private Mono<Jws<Claims>> parse(String token) {
        return Mono.fromCallable(() -> Jwts.parser().setSigningKey(secret)
                        .parseClaimsJws(token))
                .onErrorMap(e -> InvalidTokenException.EXCEPTION);
    }

    private Mono<Claims> parseClaims(String token) {
        return parse(token)
                .map(Jwt::getBody);
    }

    private UserDetails createAuthenticatedUserFromClaims(Claims claims) {
        String subject = claims.getSubject();
        return new User(subject, "", Collections.emptyList());
    }

    public Mono<Authentication> getAuthentication(String token) {
        return parseClaims(token)
                .map(this::createAuthenticatedUserFromClaims)
                .map(details -> new UsernamePasswordAuthenticationToken(
                        details, null, details.getAuthorities()));
    }

    public Mono<String> reissue(String token) {
        return parseClaims(token)
                .map(claims -> {
                    String user = claims.getSubject();
                    return tokenize(user);
                })
                .onErrorResume(ExpiredJwtException.class, e -> {
                    String user = e.getClaims().getSubject();
                    return Mono.justOrEmpty(tokenize(user));
                })
                .onErrorResume(JwtException.class, e ->
                        Mono.error(InvalidTokenException.EXCEPTION));
    }

}
