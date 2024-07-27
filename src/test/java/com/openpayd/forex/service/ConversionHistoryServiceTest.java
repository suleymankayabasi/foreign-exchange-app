package com.openpayd.forex.service;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionHistoryServiceTest {

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @InjectMocks
    private ConversionHistoryService conversionHistoryService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private ConversionHistory conversionHistory;
    private Page<ConversionHistory> conversionHistoryPage;

    @BeforeEach
    void setUp() {
        conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionId("123");
        conversionHistory.setSourceCurrency("USD");
        conversionHistory.setTargetCurrency("EUR");
        conversionHistory.setAmount(BigDecimal.valueOf(100.0));
        conversionHistory.setConvertedAmount(BigDecimal.valueOf(90.0));
        conversionHistory.setTransactionDate(LocalDateTime.now());

        conversionHistoryPage = new PageImpl<>(Collections.singletonList(conversionHistory));
    }

    @Test
    void shouldReturnConversionHistoryWhenTransactionIdIsProvided() {
        // Given
        String transactionId = "123";
        when(conversionHistoryRepository.findByTransactionId(eq(transactionId), any(Pageable.class)))
                .thenReturn(conversionHistoryPage);

        // When
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(transactionId, null, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(conversionHistoryRepository).findByTransactionId(eq(transactionId), pageableCaptor.capture());
        assertEquals(PageRequest.of(0, 10), pageableCaptor.getValue());
    }


    @Test
    void shouldReturnConversionHistoryWhenTransactionDateIsProvided() {
        // Given
        String transactionDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        when(conversionHistoryRepository.findByTransactionDate(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(conversionHistoryPage);

        // When
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(conversionHistoryRepository).findByTransactionDate(any(LocalDateTime.class), pageableCaptor.capture());
        assertEquals(PageRequest.of(0, 10), pageableCaptor.getValue());
    }

    @Test
    void shouldThrowInvalidInputExceptionWhenTransactionDateFormatIsInvalid() {
        // Given
        String invalidTransactionDate = "invalid-date";

        // When & Then
        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> conversionHistoryService.getConversionHistory(null, invalidTransactionDate, 0, 10)
        );

        assertEquals("Invalid date format. Expected format is yyyy-MM-dd HH:mm:ss", exception.getMessage());
    }

    @Test
    void shouldFetchByTransactionIdSuccessfully() {
        // Given
        String transactionId = "123";
        when(conversionHistoryRepository.findByTransactionId(eq(transactionId), any(Pageable.class)))
                .thenReturn(conversionHistoryPage);

        // When
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(transactionId, null, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionId, result.getContent().get(0).getTransactionId());
        verify(conversionHistoryRepository).findByTransactionId(eq(transactionId), pageableCaptor.capture());
        assertEquals(PageRequest.of(0, 10), pageableCaptor.getValue());
    }

    @Test
    void shouldFetchByTransactionDateSuccessfully() {
        // Given
        String transactionDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime parsedDate = LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        when(conversionHistoryRepository.findByTransactionDate(eq(parsedDate), any(Pageable.class)))
                .thenReturn(conversionHistoryPage);

        // When
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory(null, transactionDate, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        LocalDateTime actualTransactionDate = result.getContent().get(0).getTransactionDate().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(parsedDate, actualTransactionDate);
        verify(conversionHistoryRepository).findByTransactionDate(eq(parsedDate), pageableCaptor.capture());
        assertEquals(PageRequest.of(0, 10), pageableCaptor.getValue());
    }


    @Test
    void shouldMapToResponseCorrectly() {
        // Given
        when(conversionHistoryRepository.findByTransactionId(eq("123"), any(Pageable.class)))
                .thenReturn(conversionHistoryPage);

        // When
        Page<ConversionHistoryResponse> result = conversionHistoryService.getConversionHistory("123", null, 0, 10);

        // Then
        assertNotNull(result);
        List<ConversionHistoryResponse> content = result.getContent();
        assertEquals(1, content.size());
        ConversionHistoryResponse response = content.get(0);
        assertEquals(conversionHistory.getTransactionId(), response.getTransactionId());
        assertEquals(conversionHistory.getSourceCurrency(), response.getSourceCurrency());
        assertEquals(conversionHistory.getTargetCurrency(), response.getTargetCurrency());
        assertEquals(conversionHistory.getAmount(), response.getAmount());
        assertEquals(conversionHistory.getConvertedAmount(), response.getConvertedAmount());
        assertEquals(conversionHistory.getTransactionDate(), response.getTransactionDate());
    }
}
