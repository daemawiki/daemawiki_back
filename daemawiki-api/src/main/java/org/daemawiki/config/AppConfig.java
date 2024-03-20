package org.daemawiki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class AppConfig {

    /**
     * boundedElastic 스케줄러를 빈으로 등록
     *
     * @return boundedElastic 스케줄러
     */
    @Bean
    public Scheduler scheduler() {
        return Schedulers.boundedElastic();
    }

}

