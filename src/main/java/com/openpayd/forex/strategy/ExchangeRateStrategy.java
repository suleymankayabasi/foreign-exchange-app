package com.openpayd.forex.strategy;

import com.openpayd.forex.exception.ExternalServiceException;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRateStrategy {
    Map<String, BigDecimal> getExchangeRates() throws ExternalServiceException;
    BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency, Map<String, BigDecimal> rates);
}
