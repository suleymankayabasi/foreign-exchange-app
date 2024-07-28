package com.openpayd.forex.configuration.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${caffeine.cache.expire-after-write}")
    private long expireAfterWriteMinutes;

    @Value("${caffeine.cache.maximum-size}")
    private long maximumSize;

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES)
                .maximumSize(maximumSize));

        return cacheManager;
    }
}
