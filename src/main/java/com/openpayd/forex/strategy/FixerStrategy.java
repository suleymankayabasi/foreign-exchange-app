package com.openpayd.forex.strategy;

import com.openpayd.forex.client.FixerClient;
import com.openpayd.forex.configuration.FixerConfig;
import com.openpayd.forex.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class FixerStrategy implements ExchangeRateStrategy {

    private final FixerClient fixerClient;
    private final FixerConfig fixerConfig;

    public FixerStrategy(@Qualifier(value = "com.openpayd.forex.client.FixerClient") FixerClient fixerClient, FixerConfig fixerConfig) {
        this.fixerClient = fixerClient;
        this.fixerConfig = fixerConfig;
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates() {
        try {
            return fixerClient.getLatestRates(fixerConfig.getAccessKey()).getRates();
        } catch (Exception e) {
            throw new ExternalServiceException("Error fetching rates from Fixer: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency, Map<String, BigDecimal> rates, int decimalPlaces) {
        BigDecimal fromRate = rates.get(fromCurrency);
        BigDecimal toRate = rates.get(toCurrency);
        return toRate.divide(fromRate, decimalPlaces, RoundingMode.HALF_UP);
    }
}
