package com.openpayd.forex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpayd.forex.dto.ExchangeRateRequest;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnExchangeRateSuccessfully() throws Exception, ExternalServiceException {
        // Given
        ExchangeRateRequest request = new ExchangeRateRequest("USD", "EUR");
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);
        when(exchangeRateService.getExchangeRate(eq("USD"), eq("EUR"))).thenReturn(exchangeRate);

        // When & Then
        mockMvc.perform(get("/api/exchange-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(exchangeRate));
    }

    @Test
    void shouldReturnBadRequestWhenEmptyCurrencyCode() throws Exception {
        // Given
        ExchangeRateRequest request = new ExchangeRateRequest("", "");

        // When & Then
        mockMvc.perform(get("/api/exchange-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnBadRequestWhenInvalidCurrencyCodeFormat() throws Exception {
        // Given
        ExchangeRateRequest request = new ExchangeRateRequest("US", "EURO"); // Invalid format

        // When & Then
        mockMvc.perform(get("/api/exchange-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception, ExternalServiceException {
        // Given
        ExchangeRateRequest request = new ExchangeRateRequest("USD", "EUR");

        when(exchangeRateService.getExchangeRate(eq("USD"), eq("EUR"))).thenThrow(new ExternalServiceException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/exchange-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestFieldsAreMissing() throws Exception {
        // Given
        ExchangeRateRequest request = new ExchangeRateRequest(null, null); // Missing required fields

        // When & Then
        mockMvc.perform(get("/api/exchange-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

}
