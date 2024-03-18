package org.daemawiki;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @GetMapping("/test/{t}")
    public Mono<String> e(@PathVariable String t) {
        return Mono.just(t);
    }

}
