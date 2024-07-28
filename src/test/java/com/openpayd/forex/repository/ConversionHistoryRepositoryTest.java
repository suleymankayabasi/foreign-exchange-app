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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ConversionHistoryRepositoryTest {

    @Autowired
    private ConversionHistoryRepository repository;

    private ConversionHistory history1;
    private ConversionHistory history2;
    private ConversionHistory history3;

    @BeforeEach
    void setUp() {

        repository.deleteAll();

        history1 = new ConversionHistory();
        history1.setTransactionId("tx123");
        history1.setTransactionDate(LocalDateTime.of(2024, 7, 28, 10, 0));
        history1.setSourceCurrency("USD");
        history1.setTargetCurrency("EUR");
        history1.setAmount(BigDecimal.valueOf(0.85));
        history1.setConvertedAmount(BigDecimal.valueOf(1000));

        history2 = new ConversionHistory();
        history2.setTransactionId("tx124");
        history2.setTransactionDate(LocalDateTime.of(2024, 7, 29, 12, 0));
        history2.setSourceCurrency("USD");
        history2.setTargetCurrency("GBP");
        history2.setAmount(BigDecimal.valueOf(1.25));
        history2.setConvertedAmount(BigDecimal.valueOf(2300));

        history3 = new ConversionHistory();
        history3.setTransactionId("tx125");
        history3.setTransactionDate(LocalDateTime.of(2024, 7, 29, 12, 0));
        history3.setSourceCurrency("USD");
        history3.setTargetCurrency("GBP");
        history3.setAmount(BigDecimal.valueOf(1.55));
        history3.setConvertedAmount(BigDecimal.valueOf(3300));

        repository.save(history1);
        repository.save(history2);
        repository.save(history3);
    }

    @Test
    void testFindByTransactionId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConversionHistory> page = repository.findByTransactionId("tx123", pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).containsExactly(history1);
    }

    @Test
    void testFindByTransactionDate() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConversionHistory> page = repository.findByTransactionDate(LocalDateTime.of(2024, 7, 29, 12, 0), pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).containsExactly(history2, history3);
    }

}
