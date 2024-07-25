package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CurrencyConversionServiceTest {

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should convert currency successfully")
    public void shouldConvertCurrencySuccessfully() {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(new BigDecimal("100.0"));

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(new BigDecimal("0.85"));

        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);

        assertEquals(new BigDecimal("85.00"), response.getConvertedAmount());
        verify(conversionHistoryRepository).save(any(ConversionHistory.class));
    }

    @Test
    @DisplayName("Should handle zero amount conversion")
    public void shouldHandleZeroAmountConversion() {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(BigDecimal.ZERO);

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(new BigDecimal("0.85"));

        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getConvertedAmount());
        verify(conversionHistoryRepository).save(any(ConversionHistory.class));
    }
}