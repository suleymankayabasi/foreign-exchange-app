package com.openpayd.forex.service;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ConversionHistoryServiceTest {

    @InjectMocks
    private ConversionHistoryService conversionHistoryService;

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return conversion history by transaction id")
    public void shouldReturnConversionHistoryByTransactionId() {
        String transactionId = "123";
        ConversionHistory conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionId(transactionId);
        Page<ConversionHistory> page = new PageImpl<>(List.of(conversionHistory));

        when(conversionHistoryRepository.findByTransactionId(transactionId, PageRequest.of(0, 10))).thenReturn(page);

        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(transactionId, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(transactionId, result.getContent().get(0).getTransactionId());
    }

    @Test
    @DisplayName("Should return conversion history by transaction date")
    public void shouldReturnConversionHistoryByTransactionDate() {
        String transactionDate = "2022-01-01 00:00:00";
        ConversionHistory conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionDate(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Page<ConversionHistory> page = new PageImpl<>(List.of(conversionHistory));

        when(conversionHistoryRepository.findByTransactionDate(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), PageRequest.of(0, 10))).thenReturn(page);

        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), result.getContent().get(0).getTransactionDate());
    }

    @Test
    @DisplayName("Should return all conversion history when no filters are provided")
    public void shouldReturnAllConversionHistoryWhenNoFiltersAreProvided() {
        ConversionHistory conversionHistory = new ConversionHistory();
        Page<ConversionHistory> page = new PageImpl<>(Arrays.asList(conversionHistory, conversionHistory));

        when(conversionHistoryRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(null, null, 0, 10);

        assertEquals(2, result.getContent().size());
    }
}