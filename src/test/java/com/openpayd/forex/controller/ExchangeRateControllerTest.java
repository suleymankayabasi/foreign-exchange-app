package com.openpayd.forex.controller;

import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ExchangeRateControllerTest {

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return correct exchange rate for valid currencies")
    public void shouldReturnCorrectExchangeRateForValidCurrencies() throws ExternalServiceException {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal expectedRate = new BigDecimal("0.85");

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(expectedRate);

        BigDecimal actualRate = exchangeRateController.getExchangeRate(fromCurrency, toCurrency);

        assertEquals(expectedRate, actualRate);
    }

    @Test
    @DisplayName("Should return zero when fromCurrency is invalid")
    public void shouldReturnZeroWhenFromCurrencyIsInvalid() throws ExternalServiceException {
        String fromCurrency = "INVALID";
        String toCurrency = "EUR";
        BigDecimal expectedRate = BigDecimal.ZERO;

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(expectedRate);

        BigDecimal actualRate = exchangeRateController.getExchangeRate(fromCurrency, toCurrency);

        assertEquals(expectedRate, actualRate);
    }

    @Test
    @DisplayName("Should return zero when toCurrency is invalid")
    public void shouldReturnZeroWhenToCurrencyIsInvalid() throws ExternalServiceException {
        String fromCurrency = "USD";
        String toCurrency = "INVALID";
        BigDecimal expectedRate = BigDecimal.ZERO;

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(expectedRate);

        BigDecimal actualRate = exchangeRateController.getExchangeRate(fromCurrency, toCurrency);

        assertEquals(expectedRate, actualRate);
    }
}
