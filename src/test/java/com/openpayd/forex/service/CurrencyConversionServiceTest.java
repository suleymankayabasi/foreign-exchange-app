package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Captor
    private ArgumentCaptor<ConversionHistory> conversionHistoryCaptor;

    private CurrencyConversionRequest request;
    private BigDecimal exchangeRate;
    private BigDecimal convertedAmount;

    @BeforeEach
    void setUp() {
        request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        exchangeRate = BigDecimal.valueOf(0.9);
        convertedAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    @Test
    void shouldReturnCurrencyConversionResponseWhenSuccessful() throws ExternalServiceException {
        // Given
        when(exchangeRateService.getExchangeRate(anyString(), anyString())).thenReturn(exchangeRate);
        when(conversionHistoryRepository.save(any(ConversionHistory.class))).thenAnswer(invocation -> {
            ConversionHistory savedHistory = invocation.getArgument(0);
            savedHistory.setTransactionId(UUID.randomUUID().toString());
            return savedHistory;
        });

        // When
        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        assertEquals(convertedAmount, response.getConvertedAmount());

        // Verify interaction with ExchangeRateService
        verify(exchangeRateService).getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency());

        // Verify interaction with ConversionHistoryRepository
        verify(conversionHistoryRepository).save(conversionHistoryCaptor.capture());
        ConversionHistory capturedHistory = conversionHistoryCaptor.getValue();
        assertEquals(request.getSourceCurrency(), capturedHistory.getSourceCurrency());
        assertEquals(request.getTargetCurrency(), capturedHistory.getTargetCurrency());
        assertEquals(request.getAmount(), capturedHistory.getAmount());
        assertEquals(convertedAmount, capturedHistory.getConvertedAmount());
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenExchangeRateServiceFails() throws ExternalServiceException {
        // Given
        when(exchangeRateService.getExchangeRate(anyString(), anyString())).thenThrow(new ExternalServiceException("Service unavailable"));

        // When & Then
        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> currencyConversionService.convertCurrency(request)
        );

        assertEquals("Failed to retrieve exchange rate: Service unavailable", exception.getMessage());
    }
}
