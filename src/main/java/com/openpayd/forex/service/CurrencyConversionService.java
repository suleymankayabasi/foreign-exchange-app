package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionService.class);

    private final ExchangeRateService exchangeRateService;
    private final ConversionHistoryRepository conversionHistoryRepository;

    @Transactional
    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) throws ExternalServiceException {

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
        BigDecimal convertedAmount = request.getAmount().multiply(exchangeRate).setScale(6, RoundingMode.HALF_UP);
        logger.debug("Calculated converted amount: {}", convertedAmount);
        return convertedAmount;
    }

    @Transactional
    protected String saveConversionHistory(CurrencyConversionRequest request, BigDecimal convertedAmount) {
        String transactionId = UUID.randomUUID().toString();
        ConversionHistory conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionId(transactionId);
        conversionHistory.setSourceCurrency(request.getSourceCurrency());
        conversionHistory.setTargetCurrency(request.getTargetCurrency());
        conversionHistory.setAmount(request.getAmount());
        conversionHistory.setConvertedAmount(convertedAmount);
        conversionHistory.setTransactionDate(LocalDateTime.now());
        conversionHistoryRepository.save(conversionHistory);
        logger.info("Saved conversion history with transaction ID: {}", transactionId);

        return transactionId;
    }

    private CurrencyConversionResponse createResponse(String transactionId, BigDecimal convertedAmount) {
        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setTransactionId(transactionId);
        response.setConvertedAmount(convertedAmount);
        logger.debug("Created response: transactionId={}, convertedAmount={}", transactionId, convertedAmount);
        return response;
    }
}
