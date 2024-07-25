package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.DatabaseException;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Service
public class CurrencyConversionService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionService.class);

    private final ExchangeRateService exchangeRateService;
    private final ConversionHistoryRepository conversionHistoryRepository;

    @Autowired
    public CurrencyConversionService(ExchangeRateService exchangeRateService, ConversionHistoryRepository conversionHistoryRepository) {
        this.exchangeRateService = exchangeRateService;
        this.conversionHistoryRepository = conversionHistoryRepository;
    }

    @Transactional
    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) throws ExternalServiceException, DatabaseException {
        validateRequest(request);

        logger.debug("Converting {} {} to {}", request.getAmount(), request.getSourceCurrency(), request.getTargetCurrency());

        BigDecimal exchangeRate = fetchExchangeRate(request);
        BigDecimal convertedAmount = calculateConvertedAmount(request, exchangeRate);
        String transactionId = saveConversionHistory(request, convertedAmount);

        return createResponse(transactionId, convertedAmount);
    }

    private BigDecimal fetchExchangeRate(CurrencyConversionRequest request) throws ExternalServiceException {
        try {
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency());
            logger.debug("Fetched exchange rate: {}", exchangeRate);
            return exchangeRate;
        } catch (ExternalServiceException e) {
            logger.error("Failed to retrieve exchange rate for {} to {}: {}", request.getSourceCurrency(), request.getTargetCurrency(), e.getMessage());
            throw new ExternalServiceException("Failed to retrieve exchange rate: " + e.getMessage());
        }
    }

    private BigDecimal calculateConvertedAmount(CurrencyConversionRequest request, BigDecimal exchangeRate) {
        BigDecimal convertedAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        logger.debug("Calculated converted amount: {}", convertedAmount);
        return convertedAmount;
    }

    @Transactional
    protected String saveConversionHistory(CurrencyConversionRequest request, BigDecimal convertedAmount) throws DatabaseException {
        String transactionId = UUID.randomUUID().toString();
        ConversionHistory conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionId(transactionId);
        conversionHistory.setSourceCurrency(request.getSourceCurrency());
        conversionHistory.setTargetCurrency(request.getTargetCurrency());
        conversionHistory.setAmount(request.getAmount());
        conversionHistory.setConvertedAmount(convertedAmount);

        try {
            conversionHistoryRepository.save(conversionHistory);
            logger.info("Saved conversion history with transaction ID: {}", transactionId);
        } catch (Exception e) {
            logger.error("Failed to save conversion history: {}", e.getMessage());
            throw new DatabaseException("Failed to save conversion history: " + e.getMessage());
        }
        return transactionId;
    }

    private CurrencyConversionResponse createResponse(String transactionId, BigDecimal convertedAmount) {
        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setTransactionId(transactionId);
        response.setConvertedAmount(convertedAmount);
        logger.debug("Created response: transactionId={}, convertedAmount={}", transactionId, convertedAmount);
        return response;
    }

    private void validateRequest(CurrencyConversionRequest request) {
        if (Objects.isNull(request.getSourceCurrency()) || request.getSourceCurrency().trim().isEmpty()) {
            logger.error("Validation failed: Source currency cannot be empty");
            throw new InvalidInputException("Source currency cannot be empty");
        }
        if (Objects.isNull(request.getTargetCurrency()) || request.getTargetCurrency().trim().isEmpty()) {
            logger.error("Validation failed: Target currency cannot be empty");
            throw new InvalidInputException("Target currency cannot be empty");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Validation failed: Amount must be greater than zero");
            throw new InvalidInputException("Amount must be greater than zero");
        }
        logger.debug("Validation successful for request: {}", request);
    }
}
