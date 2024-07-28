package com.openpayd.forex.service;

import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.exception.ResourceNotFoundException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionHistoryService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ConversionHistoryRepository conversionHistoryRepository;

    public Page<ConversionHistory> getConversionHistory(String transactionId, String transactionDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversionHistory> conversionHistories;

        if (Objects.nonNull(transactionId)) {
            conversionHistories = fetchByTransactionId(transactionId, pageable);
        } else if (Objects.nonNull(transactionDate)) {
            conversionHistories = fetchByTransactionDate(transactionDate, pageable);
        } else {
            throw new InvalidInputException("Transaction ID or Transaction Date must be provided");
        }

        if (conversionHistories.isEmpty()) {
            throw new ResourceNotFoundException("No conversion history found for the provided criteria");
        }

        return conversionHistories;
    }

    private Page<ConversionHistory> fetchByTransactionId(String transactionId, Pageable pageable) {
        Page<ConversionHistory> conversionHistories = conversionHistoryRepository.findByTransactionId(transactionId, pageable);

        if (conversionHistories.isEmpty()) {
            throw new ResourceNotFoundException("No conversion history found for Transaction ID: " + transactionId);
        }

        log.info("Found {} transactions with ID: {}", conversionHistories, transactionId);
        return conversionHistories;
    }

    private Page<ConversionHistory> fetchByTransactionDate(String transactionDate, Pageable pageable) {
        try {
            LocalDateTime date = LocalDateTime.parse(transactionDate, DATE_TIME_FORMATTER);
            Page<ConversionHistory> conversionHistories = conversionHistoryRepository.findByTransactionDate(date, pageable);
            log.info("Found {} transactions on date: {}", conversionHistories.getTotalElements(), transactionDate);
            return conversionHistories;
        } catch (Exception e) {
            log.error("Invalid date format: {}", transactionDate, e);
            throw new InvalidInputException("Invalid date format. Expected format is yyyy-MM-dd HH:mm:ss");
        }
    }

}
