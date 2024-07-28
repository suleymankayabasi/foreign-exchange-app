package com.openpayd.forex.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class CurrencyLayerConfig {

    @Value("${currency-layer.api.key}")
    private String accessKey;

    @Value("${currency-layer.api.url}")
    private String apiUrl;

}
