package com.openpayd.forex.strategy;

import com.openpayd.forex.client.CurrencyLayerClient;
import com.openpayd.forex.configuration.CurrencyLayerConfig;
import com.openpayd.forex.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class CurrencyLayerStrategy implements ExchangeRateStrategy {

    private final CurrencyLayerClient currencyLayerClient;
    private final CurrencyLayerConfig currencyLayerConfig;

    private static final BigDecimal DEFAULT_USD_RATE = BigDecimal.ONE;

    public CurrencyLayerStrategy(@Qualifier(value = "com.openpayd.forex.client.CurrencyLayerClient") CurrencyLayerClient currencyLayerClient, CurrencyLayerConfig currencyLayerConfig) {
        this.currencyLayerClient = currencyLayerClient;
        this.currencyLayerConfig = currencyLayerConfig;
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates() {
        try {
            return currencyLayerClient.getLiveRates(currencyLayerConfig.getAccessKey()).getQuotes();
        } catch (Exception e) {
            throw new ExternalServiceException("Error fetching rates from CurrencyLayer: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency, Map<String, BigDecimal> rates, int decimalPlaces) {
        String fromKey = "USD" + fromCurrency;
        String toKey = "USD" + toCurrency;

        // Retrieve the exchange rates, defaulting to 1 if not found
        BigDecimal fromRate = rates.getOrDefault(fromKey, DEFAULT_USD_RATE);
        BigDecimal toRate = rates.getOrDefault(toKey, DEFAULT_USD_RATE);

        // If the currency pair is not found and both rates are 1, it's likely an error
        if (fromRate.equals(DEFAULT_USD_RATE) && toRate.equals(DEFAULT_USD_RATE)) {
            throw new IllegalArgumentException("Currency pair not found: " + fromCurrency + " to " + toCurrency);
        }

        return toRate.divide(fromRate, decimalPlaces, RoundingMode.HALF_UP);
    }
}
