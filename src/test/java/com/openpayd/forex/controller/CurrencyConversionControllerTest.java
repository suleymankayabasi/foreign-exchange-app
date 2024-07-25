package com.openpayd.forex.controller;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.service.CurrencyConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CurrencyConversionControllerTest {

    @InjectMocks
    private CurrencyConversionController currencyConversionController;

    @Mock
    private CurrencyConversionService currencyConversionService;

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
        request.setAmount(BigDecimal.valueOf(100.0));

        CurrencyConversionResponse expectedResponse = new CurrencyConversionResponse();
        expectedResponse.setConvertedAmount(BigDecimal.valueOf(85.0));

        when(currencyConversionService.convertCurrency(request)).thenReturn(expectedResponse);

        CurrencyConversionResponse actualResponse = currencyConversionController.convertCurrency(request);

        assertEquals(expectedResponse.getConvertedAmount(), actualResponse.getConvertedAmount());
    }

    @Test
    @DisplayName("Should handle zero amount conversion")
    public void shouldHandleZeroAmountConversion() {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(0.0));

        CurrencyConversionResponse expectedResponse = new CurrencyConversionResponse();
        expectedResponse.setConvertedAmount(BigDecimal.valueOf(0.0));

        when(currencyConversionService.convertCurrency(request)).thenReturn(expectedResponse);

        CurrencyConversionResponse actualResponse = currencyConversionController.convertCurrency(request);

        assertEquals(expectedResponse.getConvertedAmount(), actualResponse.getConvertedAmount());
    }
}