package com.openpayd.forex.controller;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.DatabaseException;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.CurrencyConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CurrencyConversionControllerTest {

    @Mock
    private CurrencyConversionService currencyConversionService;

    @InjectMocks
    private CurrencyConversionController currencyConversionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldConvertCurrency_Success() throws ExternalServiceException, DatabaseException {
        // Arrange
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setTransactionId("12345");
        response.setConvertedAmount(BigDecimal.valueOf(85.00));

        when(currencyConversionService.convertCurrency(any(CurrencyConversionRequest.class))).thenReturn(response);

        // Act
        ResponseEntity<CurrencyConversionResponse> result = currencyConversionController.convertCurrency(request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("12345", result.getBody().getTransactionId());
        assertEquals(BigDecimal.valueOf(85.00), result.getBody().getConvertedAmount());
    }

    @Test
    public void shouldConvertCurrency_ExternalServiceException() throws ExternalServiceException, DatabaseException {
        // Arrange
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        when(currencyConversionService.convertCurrency(any(CurrencyConversionRequest.class)))
                .thenThrow(new ExternalServiceException("External service error"));

        // Act
        ResponseEntity<CurrencyConversionResponse> result = currencyConversionController.convertCurrency(request);

        // Assert
        assertEquals(HttpStatus.BAD_GATEWAY, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    public void shouldConvertCurrency_DatabaseException() throws ExternalServiceException, DatabaseException {
        // Arrange
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        when(currencyConversionService.convertCurrency(any(CurrencyConversionRequest.class)))
                .thenThrow(new DatabaseException("Database error"));

        // Act
        ResponseEntity<CurrencyConversionResponse> result = currencyConversionController.convertCurrency(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    public void shouldConvertCurrency_UnhandledException() throws ExternalServiceException, DatabaseException {
        // Arrange
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        when(currencyConversionService.convertCurrency(any(CurrencyConversionRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<CurrencyConversionResponse> result = currencyConversionController.convertCurrency(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }
}
