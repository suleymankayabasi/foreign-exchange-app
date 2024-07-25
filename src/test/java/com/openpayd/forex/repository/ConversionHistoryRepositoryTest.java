package com.openpayd.forex.repository;

import com.openpayd.forex.model.ConversionHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ConversionHistoryRepositoryTest {

    @Autowired
    private ConversionHistoryRepository conversionHistoryRepository;

    @BeforeEach
    public void setup() {
        IntStream.rangeClosed(1, 20).forEach(i -> {
            ConversionHistory conversionHistory = new ConversionHistory();
            conversionHistory.setId((long) i);
            conversionHistory.setAmount(BigDecimal.valueOf(new Random().nextDouble()));
            conversionHistory.setConvertedAmount(BigDecimal.valueOf(new Random().nextDouble()));
            conversionHistory.setSourceCurrency("USD");
            conversionHistory.setTargetCurrency("TRY");
            conversionHistory.setTransactionDate(LocalDateTime.now());
            conversionHistory.setTransactionId("123");
            conversionHistoryRepository.save(conversionHistory);
        });
    }

    @Test
    @DisplayName("Should return conversion history by transaction id")
    public void shouldReturnConversionHistoryByTransactionId() {
        String transactionId = "123";
        Pageable pageable = PageRequest.of(0, 10);

        Page<ConversionHistory> page = conversionHistoryRepository.findByTransactionId(transactionId, pageable);

        assertEquals(10, page.getContent().size());
        assertEquals(transactionId, page.getContent().get(0).getTransactionId());
    }
}