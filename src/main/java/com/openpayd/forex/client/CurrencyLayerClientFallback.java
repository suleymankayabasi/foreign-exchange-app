package com.openpayd.forex.client;

import com.openpayd.forex.dto.CurrencyLayerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CurrencyLayerClientFallback implements CurrencyLayerClient {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyLayerClientFallback.class);

    /**
     * Provides a fallback response for the getLiveRates method.
     *
     * @param accessKey The access key for the API request.
     * @return A fallback CurrencyLayerResponse indicating failure.
     */
    @Override
    public CurrencyLayerResponse getLiveRates(String accessKey) {
        logger.warn("CurrencyLayerClient is unavailable. Executing fallback method for getLiveRates with accessKey: {}", accessKey);
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
