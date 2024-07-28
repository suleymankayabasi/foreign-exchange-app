package com.openpayd.forex.strategy;

import com.openpayd.forex.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class StrategyManager {

    private final ExchangeRateStrategy fixerStrategy;
    private final ExchangeRateStrategy currencyLayerStrategy;
    private ExchangeRateStrategy currentStrategy;

    public StrategyManager(FixerStrategy fixerStrategy, CurrencyLayerStrategy currencyLayerStrategy) {
        // Constructor with null checks for strategies
        this.fixerStrategy = Objects.requireNonNull(fixerStrategy, "FixerStrategy cannot be null");
        this.currencyLayerStrategy = Objects.requireNonNull(currencyLayerStrategy, "CurrencyLayerStrategy cannot be null");
        this.currentStrategy = fixerStrategy; // Start with a default strategy
    }

    /**
     * Fetches the exchange rate between two currencies.
     *
     * @param fromCurrency The source currency code.
     * @param toCurrency   The target currency code.
     * @param decimalPlaces Number of decimal places to format the rate.
     * @return The exchange rate as a BigDecimal.
     * @throws IllegalArgumentException if currency codes are null or empty.
     */
    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency, int decimalPlaces) {
        validateCurrencyCode(fromCurrency, "Source currency code cannot be null or empty");
        validateCurrencyCode(toCurrency, "Target currency code cannot be null or empty");

        Map<String, BigDecimal> exchangeRates = getExchangeRates();

        // Log the selected strategy
        log.debug("Using {} to fetch rates.", currentStrategy.getClass().getSimpleName());

        // Use fetchExchangeRate from the current strategy, passing the non-null parameters
        return currentStrategy.fetchExchangeRate(fromCurrency, toCurrency, exchangeRates, decimalPlaces);
    }

    /**
     * Validates the currency code.
     *
     * @param currencyCode The currency code to validate.
     * @param errorMessage The error message for validation failure.
     * @throws IllegalArgumentException if the currency code is null or empty.
     */
    private void validateCurrencyCode(String currencyCode, String errorMessage) {
        if (Objects.isNull(currencyCode) || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Retrieves exchange rates using the current strategy.
     *
     * @return A map of currency pairs to their exchange rates.
     * @throws ExternalServiceException if an error occurs while fetching exchange rates.
     */
    private Map<String, BigDecimal> getExchangeRates() {
        try {
            Map<String, BigDecimal> exchangeRates = currentStrategy.getExchangeRates();

            if (Objects.isNull(exchangeRates) || exchangeRates.isEmpty()) {
                throw new ExternalServiceException("Exchange rates map is null or empty");
            }

            return exchangeRates;
        } catch (ExternalServiceException e) {
            handleStrategyFailure(e);
            Map<String, BigDecimal> exchangeRates = currentStrategy.getExchangeRates();

            if (Objects.isNull(exchangeRates) || exchangeRates.isEmpty()) {
                throw new ExternalServiceException("Exchange rates map is null or empty after strategy switch");
            }

            return exchangeRates; // Retry with the new strategy
        }
    }

    /**
     * Handles strategy failure by switching to an alternative strategy.
     *
     * @param e The exception encountered during strategy execution.
     */
    private void handleStrategyFailure(ExternalServiceException e) {
        log.warn("Strategy failed with error: {}. Switching strategy.", e.getMessage());
        switchStrategy();
    }

    /**
     * Switches the current strategy to the alternative one.
     */
    private void switchStrategy() {
        currentStrategy = (currentStrategy == fixerStrategy) ? currencyLayerStrategy : fixerStrategy;
    }
}