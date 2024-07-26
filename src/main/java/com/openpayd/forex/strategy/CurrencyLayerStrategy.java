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

    public CurrencyLayerStrategy(@Qualifier(value = "com.openpayd.forex.client.CurrencyLayerClient") CurrencyLayerClient currencyLayerClient, CurrencyLayerConfig currencyLayerConfig) {
        this.currencyLayerClient = currencyLayerClient;
        this.currencyLayerConfig = currencyLayerConfig;
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates() throws ExternalServiceException {
        try {
            return currencyLayerClient.getLiveRates(currencyLayerConfig.getAccessKey()).getQuotes();
        } catch (Exception e) {
            throw new ExternalServiceException("Error fetching rates from CurrencyLayer: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency, Map<String, BigDecimal> rates) {
        String key = fromCurrency + toCurrency;
        BigDecimal rate = rates.get(key);
        return rate.setScale(6, RoundingMode.HALF_UP);
    }
}
