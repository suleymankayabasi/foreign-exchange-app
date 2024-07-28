package com.openpayd.forex.client;

import com.openpayd.forex.dto.CurrencyLayerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class CurrencyLayerClientFallback implements CurrencyLayerClient {

    /**
     * Provides a fallback response for the getLiveRates method.
     *
     * @param accessKey The access key for the API request.
     * @return A fallback CurrencyLayerResponse indicating failure.
     */
    @Override
    public CurrencyLayerResponse getLiveRates(String accessKey) {
        log.warn("CurrencyLayerClient is unavailable. Executing fallback method for getLiveRates with accessKey: {}", accessKey);
        return createFallbackResponse();
    }

    /**
     * Creates a fallback CurrencyLayerResponse indicating failure.
     *
     * @return A CurrencyLayerResponse with default failure values.
     */
    private CurrencyLayerResponse createFallbackResponse() {
        CurrencyLayerResponse fallbackResponse = new CurrencyLayerResponse();
        fallbackResponse.setSuccess(false);
        fallbackResponse.setQuotes(Collections.emptyMap());
        return fallbackResponse;
    }
}
