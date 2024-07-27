package com.openpayd.forex.service;

import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.strategy.StrategyManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    private final StrategyManager strategyManager;

    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency")
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws ExternalServiceException {
        try {
            logger.debug("Fetching exchange rate for {} to {}", fromCurrency, toCurrency);
            BigDecimal exchangeRate = strategyManager.fetchExchangeRate(fromCurrency, toCurrency);
            logger.debug("Calculated exchange rate from {} to {}: {}", fromCurrency, toCurrency, exchangeRate);
            return exchangeRate;
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            throw new ExternalServiceException("Error fetching exchange rate: " + e.getMessage());
        }
    }
}
