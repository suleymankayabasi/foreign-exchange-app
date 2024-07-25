package com.openpayd.forex.service;

import com.openpayd.forex.dto.ConversionHistoryResponse;
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
        } else if (transactionDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(transactionDate, formatter);
            histories = conversionHistoryRepository.findByTransactionDate(date, pageable);
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
