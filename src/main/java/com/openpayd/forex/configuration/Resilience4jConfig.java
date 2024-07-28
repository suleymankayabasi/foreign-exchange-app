package com.openpayd.forex.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Value("${circuit-breaker.failure-rate-threshold}")
    private float failureRateThreshold;

    @Value("${circuit-breaker.wait-duration-in-open-state}")
    private long waitDurationInOpenState;

    @Value("${circuit-breaker.sliding-window-size}")
    private int slidingWindowSize;

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenState))
                .slidingWindowSize(slidingWindowSize)
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
