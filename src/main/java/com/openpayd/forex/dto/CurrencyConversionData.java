package com.openpayd.forex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversionData {
    private String transactionId;
    private BigDecimal convertedAmount;
    private LocalDateTime transactionDate;
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal exchangeRate;
}
