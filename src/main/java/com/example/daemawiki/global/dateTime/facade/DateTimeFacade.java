package com.example.daemawiki.global.dateTime.facade;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class DateTimeFacade {

    public Mono<String> getKor() {
        return Mono.fromCallable(() -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            return zonedDateTime.toString();
        });
    }

}
