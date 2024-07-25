package com.openpayd.forex.service;

import com.openpayd.forex.configuration.FixerConfig;
import com.openpayd.forex.dto.FixerLatestResponse;
import com.openpayd.forex.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ExchangeRateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FixerConfig fixerConfig;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGetLatestRates() throws ExternalServiceException {
        // Mock configuration
        FixerLatestResponse mockResponse = new FixerLatestResponse();
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", new BigDecimal("1.2345"));
        rates.put("EUR", new BigDecimal("0.8765"));
        mockResponse.setRates(rates);

        // Mock restTemplate behavior
        when(restTemplate.getForObject(anyString(), eq(FixerLatestResponse.class)))
                .thenReturn(mockResponse);

        // Mock fixerConfig
        when(fixerConfig.getBaseUrl()).thenReturn("http://mock.base.url");
        when(fixerConfig.getAccessKey()).thenReturn("mock_access_key");

        // Call the service method
        FixerLatestResponse response = exchangeRateService.getLatestRates();

        // Verify restTemplate usage
        verify(restTemplate, times(1)).getForObject(anyString(), eq(FixerLatestResponse.class));

        // Assertions
        assertEquals(rates, response.getRates());
    }

    @Test
    public void shouldGetExchangeRate() throws ExternalServiceException {
        // Mocking getLatestRates() response
        FixerLatestResponse mockResponse = new FixerLatestResponse();
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", new BigDecimal("1.2345"));
        rates.put("EUR", new BigDecimal("0.8765"));
        mockResponse.setRates(rates);

        when(exchangeRateService.getLatestRates()).thenReturn(mockResponse);

        // Test exchange rate calculation
        BigDecimal expectedRate = new BigDecimal("0.710004"); // Assuming USD to EUR rate
        BigDecimal calculatedRate = exchangeRateService.getExchangeRate("USD", "EUR");

        // Assertions
        assertEquals(expectedRate.setScale(6, RoundingMode.HALF_UP), calculatedRate);
    }
}