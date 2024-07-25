package com.openpayd.forex.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    private CaffeineProperties caffeineProperties;

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
        // Add more cases if you need to handle other time units
        throw new IllegalArgumentException("Unsupported duration format: " + duration);
    }
}
