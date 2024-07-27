package com.openpayd.forex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.CurrencyConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyConversionController.class)
public class CurrencyConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConversionService currencyConversionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldConvertCurrencySuccessfully() throws Exception, ExternalServiceException {
        // Given
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setTargetCurrency("USD");
        request.setSourceCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setTransactionId("test-id");
        response.setConvertedAmount(BigDecimal.valueOf(85));

        when(currencyConversionService.convertCurrency(any(CurrencyConversionRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/currency-conversion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("test-id"))
                .andExpect(jsonPath("$.convertedAmount").value(85));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        // Given
        String invalidRequest = "{}";

        // When & Then
        mockMvc.perform(post("/api/currency-conversion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception, ExternalServiceException {
        // Given
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setTargetCurrency("USD");
        request.setSourceCurrency("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        when(currencyConversionService.convertCurrency(any(CurrencyConversionRequest.class)))
                .thenThrow(new ExternalServiceException("Service error"));

        // When & Then
        mockMvc.perform(post("/api/currency-conversion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
