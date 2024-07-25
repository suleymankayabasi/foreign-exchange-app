package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.DatabaseException;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class CurrencyConversionService {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private ConversionHistoryRepository conversionHistoryRepository;

    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) throws ExternalServiceException, DatabaseException {
        validateRequest(request);

        BigDecimal exchangeRate;
        try {
            exchangeRate = exchangeRateService.getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency());
        } catch (ExternalServiceException e) {
            throw new ExternalServiceException("Failed to retrieve exchange rate: " + e.getMessage());
        }

        BigDecimal convertedAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        String transactionId = UUID.randomUUID().toString();

        ConversionHistory conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionId(transactionId);
        conversionHistory.setSourceCurrency(request.getSourceCurrency());
        conversionHistory.setTargetCurrency(request.getTargetCurrency());
        conversionHistory.setAmount(request.getAmount());
        conversionHistory.setConvertedAmount(convertedAmount);

        try {
            conversionHistoryRepository.save(conversionHistory);
        } catch (Exception e) {
            throw new DatabaseException("Failed to save conversion history: " + e.getMessage());
        }

        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setTransactionId(transactionId);
        response.setConvertedAmount(convertedAmount);
        return response;
    }
    private void validateRequest(CurrencyConversionRequest request) {
        if (request.getSourceCurrency() == null || request.getSourceCurrency().trim().isEmpty()) {
            throw new InvalidInputException("Source currency cannot be empty");
        }
        if (request.getTargetCurrency() == null || request.getTargetCurrency().trim().isEmpty()) {
            throw new InvalidInputException("Target currency cannot be empty");
        }
    }

}
