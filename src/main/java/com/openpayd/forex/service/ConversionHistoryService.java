package com.openpayd.forex.service;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.exception.ResourceNotFoundException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class ConversionHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ConversionHistoryService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ConversionHistoryRepository conversionHistoryRepository;

    @Autowired
    public ConversionHistoryService(ConversionHistoryRepository conversionHistoryRepository) {
        this.conversionHistoryRepository = conversionHistoryRepository;
    }

    public Page<ConversionHistoryResponse> getConversionHistory(String transactionId, String transactionDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversionHistory> conversionHistories;

        if (Objects.nonNull(transactionId)) {
            logger.debug("Fetching conversion history by transaction ID: {}", transactionId);
            conversionHistories = fetchByTransactionId(transactionId, pageable);
        } else if (Objects.nonNull(transactionDate)) {
            logger.debug("Fetching conversion history by transaction date: {}", transactionDate);
            conversionHistories = fetchByTransactionDate(transactionDate, pageable);
        } else {
            logger.debug("Fetching all conversion histories");
            conversionHistories = conversionHistoryRepository.findAll(pageable);
        }

        return mapToResponse(conversionHistories);
    }

    private Page<ConversionHistory> fetchByTransactionId(String transactionId, Pageable pageable) {
        Page<ConversionHistory> conversionHistories = conversionHistoryRepository.findByTransactionId(transactionId, pageable);
        if (conversionHistories.isEmpty()) {
            logger.error("No transaction found with ID: {}", transactionId);
            throw new ResourceNotFoundException("No transaction found with ID: " + transactionId);
        }
        logger.info("Found {} transactions with ID: {}", conversionHistories.getTotalElements(), transactionId);
        return conversionHistories;
    }

    private Page<ConversionHistory> fetchByTransactionDate(String transactionDate, Pageable pageable) {
        try {
            LocalDateTime date = LocalDateTime.parse(transactionDate, DATE_TIME_FORMATTER);
            Page<ConversionHistory> conversionHistories = conversionHistoryRepository.findByTransactionDate(date, pageable);
            if (conversionHistories.isEmpty()) {
                logger.error("No transactions found on date: {}", transactionDate);
                throw new ResourceNotFoundException("No transactions found on date: " + transactionDate);
            }
            logger.info("Found {} transactions on date: {}", conversionHistories.getTotalElements(), transactionDate);
            return conversionHistories;
        } catch (Exception e) {
            logger.error("Invalid date format: {}", transactionDate, e);
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
