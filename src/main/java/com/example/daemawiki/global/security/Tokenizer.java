package com.example.daemawiki.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
        calendar.add(Calendar.HOUR, 2);
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

    public Boolean verify(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token);
    }

    private Claims parseClaims(String token) {
        return parse(token).getBody();
    }

    private UserDetails createAuthenticatedUserFromClaims(Claims claims) {
        String subject = claims.getSubject();
        return new User(subject, "", Collections.emptyList());
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        UserDetails details = createAuthenticatedUserFromClaims(claims);
        return new UsernamePasswordAuthenticationToken(
                details, null, details.getAuthorities());
    }

}
