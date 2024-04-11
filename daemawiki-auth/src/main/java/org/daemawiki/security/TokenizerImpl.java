package org.daemawiki.security;

import io.jsonwebtoken.*;
import org.daemawiki.config.SecurityProperties;
import org.daemawiki.exception.h400.InvalidTokenException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@Component
public class TokenizerImpl implements Tokenizer {
    private final SecurityProperties securityProperties;

    public TokenizerImpl(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Tuple2<String, LocalDateTime>> createToken(String user) {
        return Mono.fromCallable(() -> tokenize(user));
    }

    private Tuple2<String, LocalDateTime> tokenize(String user) {
        Claims claims = Jwts.claims()
                .setSubject(user);

        LocalDateTime now = LocalDateTime.now();
        Date nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        return Tuples.of(Jwts.builder()
                        .setClaims(claims)
                        .setIssuer(securityProperties.getIssuer())
                        .setIssuedAt(nowDate)
                        .setNotBefore(nowDate)
                        .setExpiration(Date.from(now.plusHours(securityProperties.getExpiration()).atZone(ZoneId.systemDefault()).toInstant()))
                        .signWith(SignatureAlgorithm.HS256, securityProperties.getSecret())
                        .setHeaderParam("typ", "JWT")
                        .setHeaderParam("alg", "HS256")
                        .setHeaderParam("kid", "daemawiki")
                        .compact(),
                now);
    }
    
    private Mono<Jws<Claims>> parse(String token) {
        return Mono.fromCallable(() -> Jwts.parser().setSigningKey(securityProperties.getSecret())
                        .parseClaimsJws(token));
    }

    private Mono<Claims> parseClaims(String token) {
        return parse(token)
                .map(Jwt::getBody);
    }

    private UserDetails createAuthenticatedUserBySubject(String subject) {
        return new User(subject, "", Collections.emptyList());
    }

    @Override
    public Mono<Authentication> getAuthentication(String token) {
        return parseClaims(token)
                .map(claims -> createAuthenticatedUserBySubject(claims.getSubject()))
                .map(details -> new UsernamePasswordAuthenticationToken(
                        details, null, details.getAuthorities()));
    }
    
    private boolean validateIssuer(Claims claims) {
        return claims.getIssuer()
                .equals(securityProperties.getIssuer());
    }

    @Override
    public Mono<Tuple2<String, LocalDateTime>> reissue(String token) {
        return parseClaims(token)
                .filter(this::validateIssuer)
                .switchIfEmpty(Mono.error(InvalidTokenException.EXCEPTION))
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
