package com.openpayd.forex.strategy;

import com.openpayd.forex.exception.ExternalServiceException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Data
@Component
public class StrategyManager {

    private final ExchangeRateStrategy fixerStrategy;
    private final ExchangeRateStrategy currencyLayerStrategy;
    private ExchangeRateStrategy currentStrategy;

    public StrategyManager(FixerStrategy fixerStrategy, CurrencyLayerStrategy currencyLayerStrategy) {
        this.fixerStrategy = fixerStrategy;
        this.currencyLayerStrategy = currencyLayerStrategy;
        this.currentStrategy = fixerStrategy;
    }

    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency, int decimalPlaces) {
        return currentStrategy.fetchExchangeRate(fromCurrency, toCurrency, getExchangeRates(), decimalPlaces);
    }

    private Map<String, BigDecimal> getExchangeRates() {
        try {
            log.debug("Requesting latest rates using current strategy");
            return currentStrategy.getExchangeRates();
        } catch (ExternalServiceException e) {
            log.warn("Strategy failed, switching strategy. Error: {}", e.getMessage());
            if (currentStrategy == fixerStrategy) {
                currentStrategy = currencyLayerStrategy;
            } else {
                currentStrategy = fixerStrategy;
            }
            return currentStrategy.getExchangeRates();
        }
    }

}
