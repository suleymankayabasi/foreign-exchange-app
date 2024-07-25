package com.openpayd.forex.service;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
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

    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) {
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency());
        BigDecimal convertedAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        String transactionId = UUID.randomUUID().toString();

        ConversionHistory conversionHistory = new ConversionHistory();
        conversionHistory.setTransactionId(transactionId);
        conversionHistory.setSourceCurrency(request.getSourceCurrency());
        conversionHistory.setTargetCurrency(request.getTargetCurrency());
        conversionHistory.setAmount(request.getAmount());
        conversionHistory.setConvertedAmount(convertedAmount);

        conversionHistoryRepository.save(conversionHistory);

        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setTransactionId(transactionId);
        response.setConvertedAmount(convertedAmount);
        return response;
    }
}
