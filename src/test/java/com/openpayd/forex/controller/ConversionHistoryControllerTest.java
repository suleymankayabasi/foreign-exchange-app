package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.service.ConversionHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ConversionHistoryControllerTest {

    @InjectMocks
    private ConversionHistoryController conversionHistoryController;

    @Mock
    private ConversionHistoryService conversionHistoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return conversion history by transaction id")
    public void shouldReturnConversionHistoryByTransactionId() {
        String transactionId = "123";
        ConversionHistoryResponse conversionHistoryResponse = new ConversionHistoryResponse();
        conversionHistoryResponse.setTransactionId(transactionId);
        Page<ConversionHistoryResponse> page = new PageImpl<>(List.of(conversionHistoryResponse));

        when(conversionHistoryService.getConversionHistory(transactionId, null, 0, 10)).thenReturn(page);

        Page<ConversionHistoryResponse> result = conversionHistoryController.getConversionHistory(transactionId, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(transactionId, result.getContent().get(0).getTransactionId());
    }

    @Test
    @DisplayName("Should return conversion history by transaction date")
    public void shouldReturnConversionHistoryByTransactionDate() {
        String transactionDate = "2022-01-01 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(transactionDate, formatter);

        ConversionHistoryResponse conversionHistoryResponse = new ConversionHistoryResponse();
        conversionHistoryResponse.setTransactionDate(dateTime);
        Page<ConversionHistoryResponse> page = new PageImpl<>(List.of(conversionHistoryResponse));

        when(conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10)).thenReturn(page);

        Page<ConversionHistoryResponse> result = conversionHistoryController.getConversionHistory(null, transactionDate, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(transactionDate, result.getContent().get(0).getTransactionDate().format(formatter));
    }

    @Test
    @DisplayName("Should return all conversion history when no filters are provided")
    public void shouldReturnAllConversionHistoryWhenNoFiltersAreProvided() {
        ConversionHistoryResponse conversionHistoryResponse = new ConversionHistoryResponse();
        Page<ConversionHistoryResponse> page = new PageImpl<>(Arrays.asList(conversionHistoryResponse, conversionHistoryResponse));

        when(conversionHistoryService.getConversionHistory(null, null, 0, 10)).thenReturn(page);

        Page<ConversionHistoryResponse> result = conversionHistoryController.getConversionHistory(null, null, 0, 10);

        assertEquals(2, result.getContent().size());
    }
}