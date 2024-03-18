import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@EnableWebFlux
@Configuration
public class AppConfig {

    @Bean
    public Scheduler scheduler() {
        return Schedulers.boundedElastic();
    }

}
