package com.openpayd.forex.service;

import com.openpayd.forex.configuration.FixerConfig;
import com.openpayd.forex.dto.FixerLatestResponse;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.exception.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

    private final RestTemplate restTemplate;
    private final FixerConfig fixerConfig;

    @Autowired
    public ExchangeRateService(RestTemplate restTemplate, FixerConfig fixerConfig) {
        this.restTemplate = restTemplate;
        this.fixerConfig = fixerConfig;
    }

    public FixerLatestResponse getLatestRates() throws ExternalServiceException {
        try {
            String endpoint = "/latest";
            String url = String.format("%s%s?access_key=%s", fixerConfig.getBaseUrl(), endpoint, fixerConfig.getAccessKey());
            logger.debug("Requesting latest rates from URL: {}", url);
            return restTemplate.getForObject(url, FixerLatestResponse.class);
        } catch (HttpServerErrorException e) {
            logger.error("Server error while fetching latest rates: {}", e.getMessage());
            throw new ExternalServiceException("External service error: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            logger.error("Client error while fetching latest rates: {}", e.getMessage());
            throw new InvalidInputException("Invalid currency code or client error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency")
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws ExternalServiceException {
        try {
            logger.debug("Fetching exchange rate for {} to {}", fromCurrency, toCurrency);
            FixerLatestResponse fixerLatestResponse = getLatestRates();
            BigDecimal fromRate = fixerLatestResponse.getRates().get(fromCurrency);
            BigDecimal toRate = fixerLatestResponse.getRates().get(toCurrency);

            if (fromRate == null || toRate == null) {
                String missingCurrency = fromRate == null ? fromCurrency : toCurrency;
                logger.warn("Currency code not found: {}", missingCurrency);
                throw new InvalidInputException("Currency code not found: " + missingCurrency);
            }

            BigDecimal exchangeRate = toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
            logger.debug("Calculated exchange rate from {} to {}: {}", fromCurrency, toCurrency, exchangeRate);
            return exchangeRate;
        } catch (HttpClientErrorException e) {
            logger.error("Client error while calculating exchange rate for {} to {}: {}", fromCurrency, toCurrency, e.getMessage());
            throw new InvalidInputException("Invalid currency code or client error: " + e.getMessage());
        }
    }
}