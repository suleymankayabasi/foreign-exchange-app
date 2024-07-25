package com.openpayd.forex.service;

import com.openpayd.forex.configuration.FixerConfig;
import com.openpayd.forex.dto.FixerLatestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FixerConfig fixerConfig;

    public FixerLatestResponse getLatestRates() {
        String endpoint = "/latest";
        String url = fixerConfig.getBaseUrl() + endpoint + "?access_key=" + fixerConfig.getAccessKey();
        return restTemplate.getForObject(url, FixerLatestResponse.class);
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        FixerLatestResponse fixerLatestResponse = getLatestRates();
        BigDecimal fromRate = fixerLatestResponse.getRates().get(fromCurrency);
        BigDecimal toRate = fixerLatestResponse.getRates().get(toCurrency);
        return toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
    }
}