package org.daemawiki.security;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface Tokenizer {
    Mono<String> createToken(String user);
    Mono<String> reissue(String token);
    Mono<Authentication> getAuthentication(String token);

}
