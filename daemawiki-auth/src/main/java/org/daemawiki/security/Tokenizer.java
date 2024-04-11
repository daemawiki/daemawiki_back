package org.daemawiki.security;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;

public interface Tokenizer {
    Mono<Tuple2<String, LocalDateTime>> createToken(String user);
    Mono<Tuple2<String, LocalDateTime>> reissue(String token);
    Mono<Authentication> getAuthentication(String token);

}
