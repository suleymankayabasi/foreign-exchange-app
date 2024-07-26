package com.openpayd.forex.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(10000))
                .slidingWindowSize(100)
                .build();
    }

    @Bean
    public CircuitBreaker fixerCircuitBreaker(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreaker.of("fixerCircuitBreaker", circuitBreakerConfig);
    }

    @Bean
    public CircuitBreaker currencyLayerCircuitBreaker(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreaker.of("currencyLayerCircuitBreaker", circuitBreakerConfig);
    }
}
