package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionData;
import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.ExchangeRateData;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.repository.ConversionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    @Value("${exchange.rate.decimal.places}")
    private int decimalPlaces;

    private final ExchangeRateService exchangeRateService;
    private final ConversionHistoryRepository conversionHistoryRepository;


    public CurrencyConversionData convertCurrency(CurrencyConversionRequest request) throws ExternalServiceException {

        log.debug("Converting {} {} to {}", request.getAmount(), request.getSourceCurrency(), request.getTargetCurrency());
        BigDecimal exchangeRate = fetchExchangeRate(request);
        BigDecimal convertedAmount = calculateConvertedAmount(request, exchangeRate);
        String transactionId = saveConversionHistory(request, convertedAmount);
        return CurrencyConversionData.builder()
                .transactionId(transactionId)
                .convertedAmount(convertedAmount)
                .transactionDate(LocalDateTime.now())
                .sourceCurrency(request.getSourceCurrency())
                .targetCurrency(request.getTargetCurrency())
                .exchangeRate(exchangeRate)
                .build();

    }

    private BigDecimal fetchExchangeRate(CurrencyConversionRequest request) {
        try {
            ExchangeRateData exchangeRate = exchangeRateService.getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency());
            log.debug("Fetched exchange rate: {}", exchangeRate);
            return exchangeRate.getExchangeRate();
        } catch (ExternalServiceException e) {
            log.error("Failed to retrieve exchange rate for {} to {}: {}", request.getSourceCurrency(), request.getTargetCurrency(), e.getMessage());
            throw new ExternalServiceException("Failed to retrieve exchange rate: " + e.getMessage());
        }
    }

    BigDecimal calculateConvertedAmount(CurrencyConversionRequest request, BigDecimal exchangeRate) {
        BigDecimal convertedAmount = request.getAmount().multiply(exchangeRate).setScale(decimalPlaces, RoundingMode.HALF_UP);
        log.debug("Calculated converted amount: {}", convertedAmount);
        return convertedAmount;
    }


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
        log.info("Saved conversion history with transaction ID: {}", transactionId);

        return transactionId;
    }
}
