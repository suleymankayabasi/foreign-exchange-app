package com.openpayd.forex.controller;

import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ExchangeRateControllerTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGetExchangeRate_Success() throws ExternalServiceException {
        // Arrange
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(exchangeRate);

        // Act
        ResponseEntity<BigDecimal> result = exchangeRateController.getExchangeRate(fromCurrency, toCurrency);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(exchangeRate, result.getBody());
    }

    @Test
    public void shouldGetExchangeRate_ExternalServiceException() throws ExternalServiceException {
        // Arrange
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new ExternalServiceException("External service error"));

        // Act
        ResponseEntity<BigDecimal> result = exchangeRateController.getExchangeRate(fromCurrency, toCurrency);

        // Assert
        assertEquals(HttpStatus.BAD_GATEWAY, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    public void shouldGetExchangeRate_UnhandledException() throws ExternalServiceException {
        // Arrange
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<BigDecimal> result = exchangeRateController.getExchangeRate(fromCurrency, toCurrency);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }
}
