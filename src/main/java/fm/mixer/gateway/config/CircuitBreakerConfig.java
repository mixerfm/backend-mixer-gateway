package fm.mixer.gateway.config;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Setter
@Configuration
@ConfigurationProperties("mixer-gateway.circuitbreaker")
public class CircuitBreakerConfig {

    private TimeLimiterProperties timeLimiter;
    private Class<? extends Throwable>[] ignoreExceptions;

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(timeLimiter.getTimeoutDuration())
                .build())
            .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .ignoreExceptions(ignoreExceptions)
                .build())
            .build());
    }

    @Getter
    @Setter
    public static class TimeLimiterProperties {
        private Duration timeoutDuration = Duration.ofSeconds(1);
    }
}
