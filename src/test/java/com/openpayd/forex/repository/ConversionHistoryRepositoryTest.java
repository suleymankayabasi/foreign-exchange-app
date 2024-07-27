package com.openpayd.forex.repository;

import com.openpayd.forex.model.ConversionHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ConversionHistoryRepositoryTest {

    @Autowired
    private ConversionHistoryRepository repository;

    private ConversionHistory conversionHistory;

    @BeforeEach
    void setUp() {
        conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionId("test-id");
        conversionHistory.setSourceCurrency("USD");
        conversionHistory.setTargetCurrency("EUR");
        conversionHistory.setAmount(BigDecimal.valueOf(100));
        conversionHistory.setConvertedAmount(BigDecimal.valueOf(85));
        conversionHistory.setTransactionDate(LocalDateTime.now());

        repository.save(conversionHistory);
    }

    @Test
    void shouldFindByTransactionIdSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ConversionHistory> result = repository.findByTransactionId("test-id", pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTransactionId()).isEqualTo("test-id");
    }

    @Test
    void shouldFindByTransactionDateSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ConversionHistory> result = repository.findByTransactionDate(conversionHistory.getTransactionDate(), pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTransactionDate()).isEqualTo(conversionHistory.getTransactionDate());
    }

    @Test
    void shouldReturnEmptyWhenTransactionIdNotFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ConversionHistory> result = repository.findByTransactionId("non-existent-id", pageable);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenTransactionDateNotFound() {
        // Given
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ConversionHistory> result = repository.findByTransactionDate(date, pageable);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleDateFormatConsistency() {
        // Given
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ConversionHistory newHistory = new ConversionHistory();
        newHistory.setTransactionId("test-consistent-date-id");
        newHistory.setSourceCurrency("USD");
        newHistory.setTargetCurrency("EUR");
        newHistory.setAmount(BigDecimal.valueOf(100));
        newHistory.setConvertedAmount(BigDecimal.valueOf(85));
        newHistory.setTransactionDate(now);
        repository.save(newHistory);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConversionHistory> result = repository.findByTransactionDate(now, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTransactionDate()).isEqualTo(now);
    }

}
