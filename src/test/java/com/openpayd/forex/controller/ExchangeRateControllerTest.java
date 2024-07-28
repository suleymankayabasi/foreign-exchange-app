package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ExchangeRateData;
import com.openpayd.forex.dto.ExchangeRateResponse;
import com.openpayd.forex.mapper.ExchangeRateMapper;
import com.openpayd.forex.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private ExchangeRateMapper mapper;

    @Test
    public void testGetExchangeRate_Success() throws Exception {
        BigDecimal exchangeRate = new BigDecimal("10");
        ExchangeRateData data = new ExchangeRateData();
        data.setExchangeRate(exchangeRate);
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setExchangeRate(exchangeRate);

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(data);
        when(mapper.toResponse(data)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange-rates")
                        .param("fromCurrency", "USD")
                        .param("toCurrency", "EUR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.exchangeRate").value(10));
    }

    @Test
    public void testGetExchangeRate_BadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange-rates")
                        .param("fromCurrency", "USD")
                        .param("toCurrency", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
