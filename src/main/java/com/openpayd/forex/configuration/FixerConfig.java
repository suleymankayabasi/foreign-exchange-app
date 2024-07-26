package com.openpayd.forex.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FixerConfig {

    @Value("${fixer.api.key}")
    private String accessKey;

    @Value("${fixer.api.url}")
    private String apiUrl;

}
