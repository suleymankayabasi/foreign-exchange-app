package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.DatabaseException;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CurrencyConversionServiceTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldConvertCurrency_Success() throws ExternalServiceException, DatabaseException {
        // Arrange
        CurrencyConversionRequest request = createRequest("USD", "EUR", BigDecimal.valueOf(100));
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);
        BigDecimal convertedAmount = BigDecimal.valueOf(85.00);

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(exchangeRate);
        when(conversionHistoryRepository.save(any(ConversionHistory.class))).thenReturn(new ConversionHistory());

        // Act
        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);

        // Assert
        assertNotNull(response);
        assertEquals(convertedAmount.setScale(2, RoundingMode.HALF_UP), response.getConvertedAmount());
        verify(exchangeRateService).getExchangeRate("USD", "EUR");
        verify(conversionHistoryRepository).save(any(ConversionHistory.class));
    }

    @Test
    public void shouldConvertCurrency_InvalidRequest() {
        // Arrange
        CurrencyConversionRequest request = createRequest("", "EUR", BigDecimal.valueOf(100));

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> currencyConversionService.convertCurrency(request));
        assertEquals("Source currency cannot be empty", thrown.getMessage());
    }

    @Test
    public void shouldConvertCurrency_InvalidTargetCurrency() {
        // Arrange
        CurrencyConversionRequest request = createRequest("USD", "", BigDecimal.valueOf(100));

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> currencyConversionService.convertCurrency(request));
        assertEquals("Target currency cannot be empty", thrown.getMessage());
    }

    @Test
    public void shouldConvertCurrency_AmountLessThanOrEqualToZero() {
        // Arrange
        CurrencyConversionRequest request = createRequest("USD", "EUR", BigDecimal.ZERO);

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> currencyConversionService.convertCurrency(request));
        assertEquals("Amount must be greater than zero", thrown.getMessage());
    }

    @Test
    public void shouldConvertCurrency_ExternalServiceException() throws ExternalServiceException {
        // Arrange
        CurrencyConversionRequest request = createRequest("USD", "EUR", BigDecimal.valueOf(100));

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenThrow(new ExternalServiceException("External service error"));

        // Act & Assert
        ExternalServiceException thrown = assertThrows(ExternalServiceException.class, () -> currencyConversionService.convertCurrency(request));
        assertEquals("Failed to retrieve exchange rate: External service error", thrown.getMessage());
    }

    @Test
    public void shouldConvertCurrency_DatabaseException() throws ExternalServiceException {
        // Arrange
        CurrencyConversionRequest request = createRequest("USD", "EUR", BigDecimal.valueOf(100));
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(exchangeRate);

        // Use doAnswer to throw checked exception
        doAnswer(invocation -> {
            throw new DatabaseException("Database error");
        }).when(conversionHistoryRepository).save(any(ConversionHistory.class));

        // Act & Assert
        DatabaseException thrown = assertThrows(DatabaseException.class, () -> currencyConversionService.convertCurrency(request));
        assertEquals("Database error", thrown.getMessage());
    }

    @Test
    public void shouldConvertCurrency_BoundaryConditions() throws ExternalServiceException, DatabaseException {
        // Test with very small amount
        CurrencyConversionRequest requestSmall = createRequest("USD", "EUR", BigDecimal.valueOf(0.01));
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);
        BigDecimal convertedAmountSmall = BigDecimal.valueOf(0.01).multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(exchangeRate);
        when(conversionHistoryRepository.save(any(ConversionHistory.class))).thenReturn(new ConversionHistory());

        // Act
        CurrencyConversionResponse responseSmall = currencyConversionService.convertCurrency(requestSmall);

        // Assert
        assertNotNull(responseSmall);
        assertEquals(convertedAmountSmall, responseSmall.getConvertedAmount());
    }

    private CurrencyConversionRequest createRequest(String sourceCurrency, String targetCurrency, BigDecimal amount) {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency(sourceCurrency);
        request.setTargetCurrency(targetCurrency);
        request.setAmount(amount);
        return request;
    }
}
