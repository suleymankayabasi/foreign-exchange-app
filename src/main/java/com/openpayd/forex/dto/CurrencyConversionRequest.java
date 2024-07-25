package com.openpayd.forex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request payload for currency conversion")
public class CurrencyConversionRequest {

    @Schema(description = "Source currency code", example = "USD")
    private String sourceCurrency;

    @Schema(description = "Target currency code", example = "EUR")
    private String targetCurrency;

    @Schema(description = "Amount to convert", example = "100.00")
    private BigDecimal amount;
}
