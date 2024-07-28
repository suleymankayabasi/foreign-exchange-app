package com.openpayd.forex.service;

import com.openpayd.forex.dto.ExchangeRateData;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.strategy.StrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    @Value("${exchange.rate.decimal.places}")
    private int decimalPlaces;

    private final StrategyManager strategyManager;

        @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency")
    public ExchangeRateData getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            log.debug("Fetching exchange rate for {} to {}", fromCurrency, toCurrency);
            BigDecimal exchangeRate = strategyManager.fetchExchangeRate(fromCurrency, toCurrency, decimalPlaces);
            log.debug("Calculated exchange rate from {} to {}: {}", fromCurrency, toCurrency, exchangeRate);
            return new ExchangeRateData(fromCurrency, toCurrency, exchangeRate, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
            throw new ExternalServiceException("Error fetching exchange rate: " + e.getMessage());
        }
    }
}
