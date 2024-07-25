package com.openpayd.forex.service;

import com.openpayd.forex.configuration.FixerConfig;
import com.openpayd.forex.dto.FixerLatestResponse;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

public class ExchangeRateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FixerConfig fixerConfig;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGetLatestRates_Success() {
        // Arrange
        FixerLatestResponse response = new FixerLatestResponse();
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", BigDecimal.valueOf(1.0));
        rates.put("EUR", BigDecimal.valueOf(0.85));
        response.setRates(rates);

        when(fixerConfig.getBaseUrl()).thenReturn("http://api.example.com");
        when(fixerConfig.getAccessKey()).thenReturn("test_access_key");
        when(restTemplate.getForObject(anyString(), eq(FixerLatestResponse.class))).thenReturn(response);

        // Act
        FixerLatestResponse result;
        try {
            result = exchangeRateService.getLatestRates();
        } catch (ExternalServiceException e) {
            throw new RuntimeException(e);
        }

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getRates().size());
        assertEquals(BigDecimal.valueOf(0.85), result.getRates().get("EUR"));
    }

    @Test
    public void shouldGetLatestRates_ServerError() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(FixerLatestResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        ExternalServiceException thrown = assertThrows(ExternalServiceException.class, () -> exchangeRateService.getLatestRates());
        assertEquals("External service error: 500 INTERNAL_SERVER_ERROR", thrown.getMessage());
    }

    @Test
    public void shouldGetLatestRates_ClientError() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(FixerLatestResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> exchangeRateService.getLatestRates());
        assertEquals("Invalid currency code or client error: 400 BAD_REQUEST", thrown.getMessage());
    }

    @Test
    public void shouldGetLatestRates_UnexpectedError() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(FixerLatestResponse.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> exchangeRateService.getLatestRates());
        assertEquals("Unexpected error: Unexpected error", thrown.getMessage());
    }

    @Test
    public void shouldGetExchangeRate_Success() throws ExternalServiceException {
        // Arrange
        FixerLatestResponse response = new FixerLatestResponse();
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", BigDecimal.valueOf(1.0));
        rates.put("EUR", BigDecimal.valueOf(0.85));
        response.setRates(rates);

        when(fixerConfig.getBaseUrl()).thenReturn("http://api.example.com");
        when(fixerConfig.getAccessKey()).thenReturn("test_access_key");
        when(exchangeRateService.getLatestRates()).thenReturn(response);

        // Act
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate("USD", "EUR");

        // Assert
        assertNotNull(exchangeRate);
        assertEquals(BigDecimal.valueOf(0.85), exchangeRate.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void shouldGetExchangeRate_MissingFromCurrency() throws ExternalServiceException {
        // Arrange
        FixerLatestResponse response = new FixerLatestResponse();
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", BigDecimal.valueOf(0.85));
        response.setRates(rates);

        when(fixerConfig.getBaseUrl()).thenReturn("http://api.example.com");
        when(fixerConfig.getAccessKey()).thenReturn("test_access_key");
        when(exchangeRateService.getLatestRates()).thenReturn(response);

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> exchangeRateService.getExchangeRate("USD", "EUR"));
        assertEquals("Currency code not found: USD", thrown.getMessage());
    }

    @Test
    public void shouldGetExchangeRate_MissingToCurrency() throws ExternalServiceException {
        // Arrange
        FixerLatestResponse response = new FixerLatestResponse();
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", BigDecimal.valueOf(1.0));
        response.setRates(rates);

        when(fixerConfig.getBaseUrl()).thenReturn("http://api.example.com");
        when(fixerConfig.getAccessKey()).thenReturn("test_access_key");
        when(exchangeRateService.getLatestRates()).thenReturn(response);

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> exchangeRateService.getExchangeRate("USD", "EUR"));
        assertEquals("Currency code not found: EUR", thrown.getMessage());
    }

    @Test
    public void shouldGetExchangeRate_ClientError() throws ExternalServiceException {
        // Arrange
        when(exchangeRateService.getLatestRates()).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> exchangeRateService.getExchangeRate("USD", "EUR"));
        assertEquals("Invalid currency code or client error: 400 BAD_REQUEST", thrown.getMessage());
    }
}
