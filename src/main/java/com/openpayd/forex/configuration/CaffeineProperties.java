package com.openpayd.forex.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cache.caffeine")
public class CaffeineProperties {
    private String expireAfterWrite;
    private int maximumSize;
}
