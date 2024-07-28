package com.openpayd.forex.service;

import com.openpayd.forex.dto.ExchangeRateData;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.strategy.StrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Mock
    private StrategyManager strategyManager;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Value("${exchange.rate.decimal.places}")
    private int decimalPlaces; // Default value for tests

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache("exchangeRates")).thenReturn(cache);
    }

    @Test
    void testGetExchangeRate_Success() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);

        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency, decimalPlaces)).thenReturn(exchangeRate);

        ExchangeRateData result = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);

        assertNotNull(result);
        assertEquals(fromCurrency, result.getSourceCurrency());
        assertEquals(toCurrency, result.getTargetCurrency());
        assertEquals(exchangeRate, result.getExchangeRate());
        assertNotNull(result.getExchangeRateDate());

        verify(strategyManager, times(1)).fetchExchangeRate(fromCurrency, toCurrency, decimalPlaces);
    }

    @Test
    void testGetExchangeRate_Exception() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(strategyManager.fetchExchangeRate(fromCurrency, toCurrency, decimalPlaces)).thenThrow(new RuntimeException("Service error"));

        ExternalServiceException thrown = assertThrows(ExternalServiceException.class, () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency));

        assertEquals("Error fetching exchange rate: Service error", thrown.getMessage());
    }
}
