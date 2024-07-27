package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.service.ConversionHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ConversionHistoryController.class)
public class ConversionHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionHistoryService conversionHistoryService;

    private ConversionHistoryResponse conversionHistoryResponse;

    @BeforeEach
    void setUp() {
        conversionHistoryResponse = new ConversionHistoryResponse();
        conversionHistoryResponse.setTransactionId("test-id");
        conversionHistoryResponse.setConvertedAmount(BigDecimal.valueOf(85));
    }

    @Test
    void shouldReturnConversionHistoryByTransactionId() throws Exception {
        // Given
        Page<ConversionHistoryResponse> page = new PageImpl<>(Collections.singletonList(conversionHistoryResponse), PageRequest.of(0, 10), 1);
        when(conversionHistoryService.getConversionHistory("test-id", null, 0, 10)).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/conversion-history")
                        .param("transactionId", "test-id")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].transactionId").value("test-id"))
                .andExpect(jsonPath("$.content[0].convertedAmount").value(85));
    }

    @Test
    void shouldReturnConversionHistoryByTransactionDate() throws Exception {
        // Given
        Page<ConversionHistoryResponse> page = new PageImpl<>(Collections.singletonList(conversionHistoryResponse), PageRequest.of(0, 10), 1);
        when(conversionHistoryService.getConversionHistory(null, "2023-07-01 12:00:00", 0, 10)).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/conversion-history")
                        .param("transactionDate", "2023-07-01 12:00:00")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].transactionId").value("test-id"))
                .andExpect(jsonPath("$.content[0].convertedAmount").value(85));
    }

    @Test
    void shouldReturnBadRequestForMissingParameters() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/conversion-history")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyWhenNoResultsFound() throws Exception {
        // Given
        Page<ConversionHistoryResponse> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(conversionHistoryService.getConversionHistory("non-existent-id", null, 0, 10)).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/conversion-history")
                        .param("transactionId", "non-existent-id")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldHandleServerError() throws Exception {
        // Given
        when(conversionHistoryService.getConversionHistory("test-id", null, 0, 10))
                .thenThrow(new RuntimeException("Server error"));

        // When & Then
        mockMvc.perform(get("/api/conversion-history")
                        .param("transactionId", "test-id")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
