package com.openpayd.forex.service;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.exception.ResourceNotFoundException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ConversionHistoryService {

    @Autowired
    private ConversionHistoryRepository conversionHistoryRepository;

    public Page<ConversionHistoryResponse> getConversionHistory(String transactionId, String transactionDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversionHistory> histories;

        if (transactionId != null) {
            histories = conversionHistoryRepository.findByTransactionId(transactionId, pageable);
            if (histories.isEmpty()) {
                throw new ResourceNotFoundException("No transaction found with ID: " + transactionId);
            }
        } else if (transactionDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(transactionDate, formatter);
                histories = conversionHistoryRepository.findByTransactionDate(date, pageable);
                if (histories.isEmpty()) {
                    throw new ResourceNotFoundException("No transactions found on date: " + transactionDate);
                }
            } catch (Exception e) {
                throw new InvalidInputException("Invalid date format. Expected format is yyyy-MM-dd HH:mm:ss");
            }
        } else {
            histories = conversionHistoryRepository.findAll(pageable);
        }

        return histories.map(history -> new ConversionHistoryResponse(
                history.getTransactionId(),
                history.getSourceCurrency(),
                history.getTargetCurrency(),
                history.getAmount(),
                history.getConvertedAmount(),
                history.getTransactionDate()));
    }
}
