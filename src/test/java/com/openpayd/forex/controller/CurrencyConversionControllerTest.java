package com.openpayd.forex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpayd.forex.dto.CurrencyConversionData;
import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.mapper.CurrencyConversionMapper;
import com.openpayd.forex.service.CurrencyConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyConversionController.class)
public class CurrencyConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConversionService currencyConversionService;

    @MockBean
    private CurrencyConversionMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    private CurrencyConversionRequest request;
    private CurrencyConversionResponse response;

    @BeforeEach
    public void setup() {
        request = new CurrencyConversionRequest("USD", "EUR", new BigDecimal("100.00"));
        response = CurrencyConversionResponse.builder()
                .transactionId("abc123")
                .convertedAmount(BigDecimal.valueOf(85.00))
                .transactionDate(LocalDateTime.of(2021, 7, 1, 12, 0, 0))
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .exchangeRate(BigDecimal.valueOf(0.85))
                .build();
    }

    @Test
    public void testConvertCurrency_Success() throws Exception {
        // Mock the service and mapper
        when(currencyConversionService.convertCurrency(any())).thenReturn(new CurrencyConversionData());
        when(mapper.toResponse(any())).thenReturn(response);

        // Perform the request
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Verify the response
        result.andExpect(status().isOk());
    }


    @Test
    public void testConvertCurrency_BadRequest() throws Exception {
        // Test invalid request
        CurrencyConversionRequest invalidRequest = new CurrencyConversionRequest(null, "EUR", BigDecimal.valueOf(-100.00));

        // Perform the request
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Verify the response
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testConvertCurrency_InternalServerError() throws Exception {
        // Mock the service to throw an exception
        when(currencyConversionService.convertCurrency(any())).thenThrow(new RuntimeException("Internal Server Error"));

        // Perform the request
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Verify the response
        result.andExpect(status().isInternalServerError());
    }
}
