package org.daemawiki.security;

import io.jsonwebtoken.*;
import org.daemawiki.exception.h400.InvalidTokenException;
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
public class TokenizerImpl implements Tokenizer {

    @Value("${jwt.secret}")
    private String secret;

    @Override
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

    private Mono<Jws<Claims>> parse(String token) {
        return Mono.fromCallable(() -> Jwts.parser().setSigningKey(secret)
                        .parseClaimsJws(token));
    }

    private Mono<Claims> parseClaims(String token) {
        return parse(token)
                .map(Jwt::getBody);
    }

    private UserDetails createAuthenticatedUserFromClaims(Claims claims) {
        String subject = claims.getSubject();
        return new User(subject, "", Collections.emptyList());
    }

    @Override
    public Mono<Authentication> getAuthentication(String token) {
        return parseClaims(token)
                .map(this::createAuthenticatedUserFromClaims)
                .map(details -> new UsernamePasswordAuthenticationToken(
                        details, null, details.getAuthorities()));
    }

    @Override
    public Mono<String> reissue(String token) {
        return parseClaims(token)
                .map(claims -> {
                    String user = claims.getSubject();
                    return tokenize(user);
                })
                .onErrorResume(ExpiredJwtException.class, e -> {
                    String user = e.getClaims().getSubject();

                    Date expiration = e.getClaims().getExpiration();
                    Date now = new Date();

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(expiration);
                    cal.add(Calendar.HOUR_OF_DAY, 2);

                    Date twoHoursLater = cal.getTime();

                    if (now.before(twoHoursLater)) {
                        return Mono.justOrEmpty(tokenize(user));
                    } else {
                        return Mono.error(InvalidTokenException.EXCEPTION);
                    }
                })
                .onErrorResume(JwtException.class, e ->
                        Mono.error(InvalidTokenException.EXCEPTION));
    }

}
