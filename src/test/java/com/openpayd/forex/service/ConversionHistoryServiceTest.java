package com.openpayd.forex.service;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.exception.ResourceNotFoundException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ConversionHistoryServiceTest {

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @InjectMocks
    private ConversionHistoryService conversionHistoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGetConversionHistoryByTransactionId_Success() {
        // Arrange
        String transactionId = "12345";
        ConversionHistory history = new ConversionHistory();
        history.setTransactionId(transactionId);
        Page<ConversionHistory> page = new PageImpl<>(Collections.singletonList(history));
        when(conversionHistoryRepository.findByTransactionId(anyString(), any(Pageable.class))).thenReturn(page);

        // Act
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(transactionId, null, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionId, result.getContent().get(0).getTransactionId());
    }

    @Test
    public void shouldGetConversionHistoryByTransactionDate_Success() {
        // Arrange
        String transactionDate = "2023-07-01 12:00:00";
        ConversionHistory history = new ConversionHistory();
        history.setTransactionDate(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Page<ConversionHistory> page = new PageImpl<>(Collections.singletonList(history));
        when(conversionHistoryRepository.findByTransactionDate(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionDate, result.getContent().get(0).getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void shouldGetConversionHistoryByTransactionId_NoResults() {
        // Arrange
        String transactionId = "nonexistent";
        when(conversionHistoryRepository.findByTransactionId(anyString(), any(Pageable.class))).thenReturn(Page.empty());

        // Act & Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> conversionHistoryService.getConversionHistory(transactionId, null, 0, 10));
        assertEquals("No transaction found with ID: nonexistent", thrown.getMessage());
    }

    @Test
    public void shouldGetConversionHistoryByTransactionDate_NoResults() {
        // Arrange
        String transactionDate = "2023-07-01 12:00:00";
        when(conversionHistoryRepository.findByTransactionDate(any(LocalDateTime.class), any(Pageable.class))).thenReturn(Page.empty());

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10));
        assertEquals("Invalid date format. Expected format is yyyy-MM-dd HH:mm:ss", thrown.getMessage());
    }

    @Test
    public void shouldGetConversionHistory_InvalidDateFormat() {
        // Arrange
        String invalidDate = "invalid-date";

        // Act & Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> conversionHistoryService.getConversionHistory(null, invalidDate, 0, 10));
        assertEquals("Invalid date format. Expected format is yyyy-MM-dd HH:mm:ss", thrown.getMessage());
    }

    @Test
    public void shouldGetConversionHistory_NoFilter() {
        // Arrange
        ConversionHistory history = new ConversionHistory();
        Page<ConversionHistory> page = new PageImpl<>(Collections.singletonList(history));
        when(conversionHistoryRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(null, null, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
