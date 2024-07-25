package com.openpayd.forex.repository;

import com.openpayd.forex.model.ConversionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConversionHistoryRepository extends JpaRepository<ConversionHistory, Long> {
    Page<ConversionHistory> findByTransactionId(String transactionId, Pageable pageable);

    Page<ConversionHistory> findByTransactionDate(LocalDateTime transactionDate, Pageable pageable);
}