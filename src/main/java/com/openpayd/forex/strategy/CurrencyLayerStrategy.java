package com.openpayd.forex.strategy;

import com.openpayd.forex.client.CurrencyLayerClient;
import com.openpayd.forex.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class CurrencyLayerStrategy implements ExchangeRateStrategy {

    private final CurrencyLayerClient currencyLayerClient;

    @Value("${currency-layer.access-key}")
    private String accessKey;

    @Value("${currency-layer.default-currency}")
    private String defaultCurrency;

    private static final BigDecimal DEFAULT_RATE = BigDecimal.ONE;

    public CurrencyLayerStrategy(@Qualifier("com.openpayd.forex.client.CurrencyLayerClient") CurrencyLayerClient currencyLayerClient) {
        this.currencyLayerClient = currencyLayerClient;
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates() {
        try {
            return currencyLayerClient.getLiveRates(accessKey).getQuotes();
        } catch (Exception e) {
            throw new ExternalServiceException("Error fetching rates from CurrencyLayer: " + e.getMessage());
        }
    }

    /**
     * Fetches the exchange rate between two currencies.
     *
     * @param fromCurrency  the source currency code
     * @param toCurrency    the target currency code
     * @param rates         a map of currency rates
     * @param decimalPlaces the number of decimal places for rounding
     * @return the exchange rate between the two currencies
     * @throws IllegalArgumentException if the currency pair is not found
     */
    public BigDecimal fetchExchangeRate(
            String fromCurrency,
            String toCurrency,
            Map<String, BigDecimal> rates,
            int decimalPlaces) {

        if (isSameCurrency(fromCurrency, toCurrency)) {
            return BigDecimal.ONE.setScale(decimalPlaces, RoundingMode.HALF_UP);
        }

        BigDecimal fromRate = getRateForCurrency(fromCurrency, rates);
        BigDecimal toRate = getRateForCurrency(toCurrency, rates);

        validateCurrencyPair(fromCurrency, toCurrency, fromRate, toRate);

        return calculateExchangeRate(fromRate, toRate, decimalPlaces);
    }

    /**
     * Checks if both currencies are the same.
     *
     * @param fromCurrency the source currency code
     * @param toCurrency   the target currency code
     * @return true if both currencies are the same, false otherwise
     */
    private boolean isSameCurrency(String fromCurrency, String toCurrency) {
        return fromCurrency.equals(toCurrency);
    }

    /**
     * Retrieves the rate for a given currency.
     *
     * @param currency the currency code
     * @param rates    a map of currency rates
     * @return the rate for the given currency, or DEFAULT_USD_RATE if not found
     */
    private BigDecimal getRateForCurrency(String currency, Map<String, BigDecimal> rates) {
        String currencyKey = defaultCurrency + currency;
        return rates.getOrDefault(currencyKey, DEFAULT_RATE);
    }

    /**
     * Validates the currency pair, ensuring both rates are not the default rate.
     *
     * @param fromCurrency the source currency code
     * @param toCurrency   the target currency code
     * @param fromRate     the rate for the source currency
     * @param toRate       the rate for the target currency
     * @throws IllegalArgumentException if both rates are the default rate
     */
    private void validateCurrencyPair(String fromCurrency, String toCurrency, BigDecimal fromRate, BigDecimal toRate) {
        if (fromRate.equals(DEFAULT_RATE) && toRate.equals(DEFAULT_RATE)) {
            throw new IllegalArgumentException(String.format("Currency pair not found: %s to %s", fromCurrency, toCurrency));
        }
    }

    /**
     * Calculates the exchange rate between two currencies.
     *
     * @param fromRate      the rate for the source currency
     * @param toRate        the rate for the target currency
     * @param decimalPlaces the number of decimal places for rounding
     * @return the calculated exchange rate
     */
    private BigDecimal calculateExchangeRate(BigDecimal fromRate, BigDecimal toRate, int decimalPlaces) {
        return toRate.divide(fromRate, decimalPlaces, RoundingMode.HALF_UP);
    }
}
