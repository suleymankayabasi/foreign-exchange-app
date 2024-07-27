package com.openpayd.forex.service;

import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.strategy.StrategyManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private StrategyManager strategyManager;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    void shouldReturnExchangeRateWhenStrategyManagerReturnsRate() throws ExternalServiceException {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);

        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency)).thenReturn(expectedRate);

        // When
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);

        // Then
        assertNotNull(exchangeRate, "Exchange rate should not be null");
        assertEquals(expectedRate, exchangeRate, "Exchange rate should match the expected value");

        verify(strategyManager, times(1)).fetchExchangeRate(fromCurrency, toCurrency);
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenStrategyManagerThrowsExternalServiceException() throws ExternalServiceException {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String expectedErrorMessage = "Service is down";

        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new ExternalServiceException(expectedErrorMessage));

        // When
        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency),
                "Should throw ExternalServiceException"
        );

        // Then
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(strategyManager, times(1)).fetchExchangeRate(fromCurrency, toCurrency);
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenStrategyManagerThrowsRuntimeException() throws ExternalServiceException {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new RuntimeException("Unexpected error"));

        //  When
        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency),
                "Should throw ExternalServiceException"
        );

        assertTrue(exception.getMessage().contains("Error fetching exchange rate:"), "Exception message should contain expected prefix");

        // Then
        verify(strategyManager, times(1)).fetchExchangeRate(fromCurrency, toCurrency);
    }

    @Test
    void shouldLogDebugMessagesWhenFetchingExchangeRate() throws ExternalServiceException {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);

        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency)).thenReturn(expectedRate);

        // When
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);

        // Then
        assertNotNull(exchangeRate, "Exchange rate should not be null");
        assertEquals(expectedRate, exchangeRate, "Exchange rate should match the expected value");
    }

    @Test
    void shouldHandleNullExchangeRateGracefully() throws ExternalServiceException {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency)).thenReturn(null);

        // When
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);

        // Then
        assertNull(exchangeRate, "Exchange rate should be null");

        verify(strategyManager, times(1)).fetchExchangeRate(fromCurrency, toCurrency);
    }
}
