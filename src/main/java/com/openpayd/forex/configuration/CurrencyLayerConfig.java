package com.openpayd.forex.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class CurrencyLayerConfig {

    @Value("${currencylayer.api.key}")
    private String accessKey;

    @Value("${currencylayer.api.url}")
    private String apiUrl;

}
