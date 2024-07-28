package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionData;
import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.ExchangeRateData;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConversionServiceTest {

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @Value("${exchange.rate.decimal.places}")
    private int decimalPlaces; // Default value for tests

    private static final String FIXED_TRANSACTION_ID = "ef6d5303-02b3-4804-890c-08330052b1bd";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertCurrency_Success() throws ExternalServiceException {
        UUID mockUuid = UUID.fromString(FIXED_TRANSACTION_ID);
        try (MockedStatic<UUID> mockedUUID = mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(mockUuid);

            CurrencyConversionRequest request = new CurrencyConversionRequest();
            request.setAmount(BigDecimal.valueOf(100));
            request.setSourceCurrency("USD");
            request.setTargetCurrency("EUR");

            ExchangeRateData exchangeRateData = new ExchangeRateData();
            exchangeRateData.setExchangeRate(BigDecimal.valueOf(0.85));
            when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(exchangeRateData);

            when(conversionHistoryRepository.save(any(ConversionHistory.class))).thenAnswer(invocation -> {
                ConversionHistory history = invocation.getArgument(0);
                assertEquals(FIXED_TRANSACTION_ID, history.getTransactionId()); // Verify static ID
                return null;
            });

            CurrencyConversionData result = currencyConversionService.convertCurrency(request);

            assertNotNull(result);
            assertEquals("USD", result.getSourceCurrency());
            assertEquals("EUR", result.getTargetCurrency());
            assertEquals(BigDecimal.valueOf(85.00).setScale(decimalPlaces, RoundingMode.HALF_UP), result.getConvertedAmount());
            assertNotNull(result.getTransactionId());
            assertEquals(FIXED_TRANSACTION_ID, result.getTransactionId());

            verify(exchangeRateService, times(1)).getExchangeRate("USD", "EUR");
            verify(conversionHistoryRepository, times(1)).save(any(ConversionHistory.class));
        }
    }

    @Test
    void testConvertCurrency_ThrowsExternalServiceException_WhenExchangeRateServiceFails() throws ExternalServiceException {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenThrow(new ExternalServiceException("Service unavailable"));

        Exception exception = assertThrows(ExternalServiceException.class, () -> currencyConversionService.convertCurrency(request));

        assertEquals("Failed to retrieve exchange rate: Service unavailable", exception.getMessage());
        verify(exchangeRateService, times(1)).getExchangeRate("USD", "EUR");
        verify(conversionHistoryRepository, times(0)).save(any(ConversionHistory.class));
    }

    @Test
    void testCalculateConvertedAmount_UsesDecimalPlaces() {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");

        BigDecimal exchangeRate = BigDecimal.valueOf(0.8567); // Unrounded exchange rate

         BigDecimal expectedConvertedAmount = exchangeRate.multiply(request.getAmount())
                .setScale(decimalPlaces, RoundingMode.HALF_UP);

        BigDecimal convertedAmount = currencyConversionService.calculateConvertedAmount(request, exchangeRate);

        assertEquals(expectedConvertedAmount, convertedAmount, "Converted amount should match the expected value.");
    }

    @Test
    void testSaveConversionHistory_SavesCorrectly() {
        // Mock UUID.randomUUID() to return a fixed value
        UUID mockUuid = UUID.fromString(FIXED_TRANSACTION_ID);
        try (MockedStatic<UUID> mockedUUID = mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(mockUuid);

            CurrencyConversionRequest request = new CurrencyConversionRequest();
            request.setAmount(BigDecimal.valueOf(100));
            request.setSourceCurrency("USD");
            request.setTargetCurrency("EUR");

            BigDecimal convertedAmount = BigDecimal.valueOf(85.00);

            when(conversionHistoryRepository.save(any(ConversionHistory.class))).thenAnswer(invocation -> {
                ConversionHistory history = invocation.getArgument(0);
                assertEquals(FIXED_TRANSACTION_ID, history.getTransactionId()); // Verify static ID
                assertEquals("USD", history.getSourceCurrency());
                assertEquals("EUR", history.getTargetCurrency());
                assertEquals(BigDecimal.valueOf(100), history.getAmount());
                assertEquals(convertedAmount, history.getConvertedAmount());
                assertNotNull(history.getTransactionDate());
                return null;
            });

            String resultTransactionId = currencyConversionService.saveConversionHistory(request, convertedAmount);

            assertNotNull(resultTransactionId);
            assertEquals(FIXED_TRANSACTION_ID, resultTransactionId); // Verify static ID
        }
    }
}
