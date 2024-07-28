package com.openpayd.forex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Response payload for currency conversion")
public class CurrencyConversionResponse {

    @Schema(description = "Unique identifier for the conversion transaction", example = "abc123")
    private String transactionId;

    @Schema(description = "Amount after conversion", example = "85.00")
    private BigDecimal convertedAmount;

    @Schema(description = "Date of the conversion transaction", example = "2021-07-01 12:00:00")
    private LocalDateTime transactionDate;

    @Schema(description = "Source currency code", example = "USD")
    private String sourceCurrency;

    @Schema(description = "Target currency code", example = "EUR")
    private String targetCurrency;

    @Schema(description = "Exchange rate used for the conversion", example = "0.85")
    private BigDecimal exchangeRate;
}
