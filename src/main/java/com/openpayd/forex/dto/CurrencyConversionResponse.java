package com.openpayd.forex.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyConversionResponse {

    private String transactionId;
    private BigDecimal convertedAmount;
}
