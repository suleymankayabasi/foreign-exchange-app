package com.openpayd.forex.strategy;

import com.openpayd.forex.client.FixerClient;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class FixerStrategy implements ExchangeRateStrategy {

    private final FixerClient fixerClient;

    @Value("${fixer.access-key}")
    private String accessKey;

    public FixerStrategy(@Qualifier("com.openpayd.forex.client.FixerClient") FixerClient fixerClient) {
        this.fixerClient = fixerClient;
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates() {
        try {
            return fixerClient.getLatestRates(accessKey).getRates();
        } catch (Exception e) {
            throw new ExternalServiceException("Error fetching rates from Fixer: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency, Map<String, BigDecimal> rates, int decimalPlaces) {
        BigDecimal fromRate = rates.get(fromCurrency);
        BigDecimal toRate = rates.get(toCurrency);

        if (fromRate == null) {
            throw new InvalidInputException("Exchange rate for the currency '" + fromCurrency + "' is not available. Please ensure the source currency code is valid and in ISO 4217 format.");
        }

        if (toRate == null) {
            throw new InvalidInputException("Exchange rate for the currency '" + toCurrency + "' is not available. Please ensure the target currency code is valid and in ISO 4217 format.");
        }

        return toRate.divide(fromRate, decimalPlaces, RoundingMode.HALF_UP);
    }
}
