package com.openpayd.forex.service;

import com.openpayd.forex.configuration.FixerConfig;
import com.openpayd.forex.dto.FixerLatestResponse;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FixerConfig fixerConfig;

    public FixerLatestResponse getLatestRates() throws ExternalServiceException {
        try {
            String endpoint = "/latest";
            String url = fixerConfig.getBaseUrl() + endpoint + "?access_key=" + fixerConfig.getAccessKey();
            return restTemplate.getForObject(url, FixerLatestResponse.class);
        } catch (
                HttpServerErrorException e) {
            throw new ExternalServiceException("External service error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws ExternalServiceException {
        try {
            FixerLatestResponse fixerLatestResponse = getLatestRates();
            BigDecimal fromRate = fixerLatestResponse.getRates().get(fromCurrency);
            BigDecimal toRate = fixerLatestResponse.getRates().get(toCurrency);
            return toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
        } catch (HttpClientErrorException e) {
            throw new InvalidInputException("Invalid currency code or external service client error: " + e.getMessage());
        }
    }
}