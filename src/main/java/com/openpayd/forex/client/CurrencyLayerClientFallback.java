package com.openpayd.forex.client;

import com.openpayd.forex.dto.CurrencyLayerResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CurrencyLayerClientFallback implements CurrencyLayerClient {

    @Override
    public CurrencyLayerResponse getLiveRates(String accessKey) {
        CurrencyLayerResponse fallbackResponse = new CurrencyLayerResponse();
        fallbackResponse.setSuccess(false);
        fallbackResponse.setQuotes(Collections.emptyMap());
        return fallbackResponse;
    }
}
