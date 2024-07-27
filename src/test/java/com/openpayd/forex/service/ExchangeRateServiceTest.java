package com.openpayd.forex.service;

import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.strategy.StrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExchangeRateServiceTest {

    @Mock
    private StrategyManager strategyManager;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnExchangeRateWhenSuccessful() throws ExternalServiceException {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);

        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put(fromCurrency + "_" + toCurrency, expectedRate);

        when(strategyManager.getExchangeRates()).thenReturn(rates);
        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency, rates)).thenReturn(expectedRate);

        // When
        BigDecimal actualRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);

        // Then
        assertEquals(expectedRate, actualRate, "The exchange rate should be " + expectedRate);

        verify(strategyManager).getExchangeRates();
        verify(strategyManager).fetchExchangeRate(fromCurrency, toCurrency, rates);
    }

    @Test
    public void shouldThrowExternalServiceExceptionWhenErrorOccurs() throws ExternalServiceException {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(strategyManager.getExchangeRates()).thenThrow(new RuntimeException("Simulated error"));

        // When
        ExternalServiceException thrownException = assertThrows(ExternalServiceException.class, () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency));

        // Then
        assertTrue(thrownException.getMessage().contains("Error fetching exchange rate"));

        verify(strategyManager).getExchangeRates();
    }
}
