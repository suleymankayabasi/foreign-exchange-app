package com.openpayd.forex.service;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversionHistoryServiceTest {

    @InjectMocks
    private ConversionHistoryService conversionHistoryService;

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetConversionHistory_WhenTransactionIdIsProvided_ThenReturnsConversionHistory() {
        String transactionId = "123";
        Pageable pageable = PageRequest.of(0, 10);
        ConversionHistory history = new ConversionHistory(); // Assume a valid ConversionHistory object
        Page<ConversionHistory> page = new PageImpl<>(List.of(history), pageable, 1);

        when(conversionHistoryRepository.findByTransactionId(transactionId, pageable)).thenReturn(page);

        Page<ConversionHistory> result = conversionHistoryService.getConversionHistory(transactionId, null, 0, 10);
        assertEquals(1, result.getTotalElements());
        verify(conversionHistoryRepository, times(1)).findByTransactionId(transactionId, pageable);
    }

    @Test
    void testGetConversionHistory_WhenTransactionDateIsProvided_ThenReturnsConversionHistory() {
        String transactionDate = "2024-07-27 12:00:00";
        Pageable pageable = PageRequest.of(0, 10);
        ConversionHistory history = new ConversionHistory(); // Assume a valid ConversionHistory object
        Page<ConversionHistory> page = new PageImpl<>(List.of(history), pageable, 1);

        when(conversionHistoryRepository.findByTransactionDate(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), pageable)).thenReturn(page);

        Page<ConversionHistory> result = conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10);
        assertEquals(1, result.getTotalElements());
        verify(conversionHistoryRepository, times(1)).findByTransactionDate(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), pageable);
    }

    @Test
    void testGetConversionHistory_WhenNeitherTransactionIdNorDateIsProvided_ThrowsInvalidInputException() {
        Exception exception = assertThrows(InvalidInputException.class, () -> conversionHistoryService.getConversionHistory(null, null, 0, 10));
        assertEquals("Transaction ID or Transaction Date must be provided", exception.getMessage());
    }

    @Test
    void testGetConversionHistory_WhenTransactionIdIsNotFound_ThrowsResourceNotFoundException() {
        String transactionId = "123";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConversionHistory> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(conversionHistoryRepository.findByTransactionId(transactionId, pageable)).thenReturn(emptyPage);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> conversionHistoryService.getConversionHistory(transactionId, null, 0, 10));
        assertEquals("No conversion history found for Transaction ID: " + transactionId, exception.getMessage());
    }

    @Test
    void testGetConversionHistory_WhenTransactionDateIsNotFound_ThrowsResourceNotFoundException() {
        String transactionDate = "2024-07-27 12:00:00";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConversionHistory> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(conversionHistoryRepository.findByTransactionDate(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), pageable)).thenReturn(emptyPage);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10));
        assertEquals("No conversion history found for the provided criteria", exception.getMessage());
    }

    @Test
    void testGetConversionHistory_WhenTransactionDateFormatIsInvalid_ThrowsInvalidInputException() {
        String invalidDate = "invalid-date-format";

        Exception exception = assertThrows(InvalidInputException.class, () -> conversionHistoryService.getConversionHistory(null, invalidDate, 0, 10));
        assertEquals("Invalid date format. Expected format is yyyy-MM-dd HH:mm:ss", exception.getMessage());
    }
}
