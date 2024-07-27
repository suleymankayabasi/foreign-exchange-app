package com.openpayd.forex.client;

import com.openpayd.forex.dto.FixerLatestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class FixerClientFallback implements FixerClient {

    private static final Logger logger = LoggerFactory.getLogger(FixerClientFallback.class);

    /**
     * Provides a fallback response for the getLatestRates method.
     *
     * @param accessKey The access key for the API request.
     * @return A fallback FixerLatestResponse indicating failure.
     */
    @Override
    public FixerLatestResponse getLatestRates(String accessKey) {
        logger.warn("FixerClient is unavailable. Executing fallback method for getLatestRates with accessKey: {}", accessKey);
        return createFallbackResponse();
    }

    /**
     * Creates a fallback FixerLatestResponse indicating failure.
     *
     * @return A FixerLatestResponse with default failure values.
     */
    private FixerLatestResponse createFallbackResponse() {
        FixerLatestResponse fallbackResponse = new FixerLatestResponse();
        fallbackResponse.setSuccess(false);
        fallbackResponse.setRates(Collections.emptyMap());
        return fallbackResponse;
    }
}
