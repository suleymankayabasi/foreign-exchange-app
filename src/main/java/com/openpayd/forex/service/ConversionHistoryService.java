package com.openpayd.forex.service;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
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

    public Page<ConversionHistoryResponse> getConversionHistory(String transactionId, String transactionDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversionHistory> conversionHistories = Objects.nonNull(transactionId)
                ? fetchByTransactionId(transactionId, pageable)
                : (Objects.nonNull(transactionDate)
                ? fetchByTransactionDate(transactionDate, pageable)
                : Page.empty());

        return mapToResponse(conversionHistories);
    }

    private Page<ConversionHistory> fetchByTransactionId(String transactionId, Pageable pageable) {
        Page<ConversionHistory> conversionHistories = conversionHistoryRepository.findByTransactionId(transactionId, pageable);
        log.info("Found {} transactions with ID: {}", conversionHistories.getTotalElements(), transactionId);
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

    private Page<ConversionHistoryResponse> mapToResponse(Page<ConversionHistory> conversionHistories) {
        return conversionHistories.map(conversionHistory -> new ConversionHistoryResponse(
                conversionHistory.getTransactionId(),
                conversionHistory.getSourceCurrency(),
                conversionHistory.getTargetCurrency(),
                conversionHistory.getAmount(),
                conversionHistory.getConvertedAmount(),
                conversionHistory.getTransactionDate()));
    }
}
