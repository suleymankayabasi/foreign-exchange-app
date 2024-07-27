package com.openpayd.forex.strategy;

import com.openpayd.forex.exception.ExternalServiceException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Component
public class StrategyManager {

    private static final Logger logger = LoggerFactory.getLogger(StrategyManager.class);

    private final ExchangeRateStrategy fixerStrategy;
    private final ExchangeRateStrategy currencyLayerStrategy;
    private ExchangeRateStrategy currentStrategy;

    public StrategyManager(FixerStrategy fixerStrategy, CurrencyLayerStrategy currencyLayerStrategy) {
        this.fixerStrategy = fixerStrategy;
        this.currencyLayerStrategy = currencyLayerStrategy;
        this.currentStrategy = fixerStrategy;
    }

    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency) throws ExternalServiceException {
        return currentStrategy.fetchExchangeRate(fromCurrency, toCurrency, getExchangeRates());
    }

    private Map<String, BigDecimal> getExchangeRates() throws ExternalServiceException {
        try {
            logger.debug("Requesting latest rates using current strategy");
            return currentStrategy.getExchangeRates();
        } catch (ExternalServiceException e) {
            logger.warn("Strategy failed, switching strategy. Error: {}", e.getMessage());
            if (currentStrategy == fixerStrategy) {
                currentStrategy = currencyLayerStrategy;
            } else {
                currentStrategy = fixerStrategy;
            }
            return currentStrategy.getExchangeRates();
        }
    }

}
