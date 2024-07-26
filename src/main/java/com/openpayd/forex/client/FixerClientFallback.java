package com.openpayd.forex.client;

import com.openpayd.forex.dto.FixerLatestResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class FixerClientFallback implements FixerClient {

    @Override
    public FixerLatestResponse getLatestRates(String accessKey) {
        FixerLatestResponse fallbackResponse = new FixerLatestResponse();
        fallbackResponse.setSuccess(false);
        fallbackResponse.setRates(Collections.emptyMap());
        return fallbackResponse;
    }
}
