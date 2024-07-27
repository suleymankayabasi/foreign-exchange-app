package com.openpayd.forex.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    private final CaffeineProperties caffeineProperties;

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        String expireAfterWrite = caffeineProperties.getExpireAfterWrite();
        long expireAfterWriteDuration = parseDuration(expireAfterWrite);

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
                .maximumSize(caffeineProperties.getMaximumSize()));

        return cacheManager;
    }

    private long parseDuration(String duration) {
        // Assuming the duration is in the format like '5m' (minutes)
        if (duration.endsWith("m")) {
            return Long.parseLong(duration.replace("m", ""));
        }
        throw new IllegalArgumentException("Unsupported duration format: " + duration);
    }
}
