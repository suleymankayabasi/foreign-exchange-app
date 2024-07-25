package com.openpayd.forex.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyConversionRequest {

    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal amount;
}
