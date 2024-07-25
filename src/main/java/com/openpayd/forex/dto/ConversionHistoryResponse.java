package com.openpayd.forex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response for conversion history")
public class ConversionHistoryResponse {

    @Schema(description = "Transaction ID", example = "12345")
    private String transactionId;

    @Schema(description = "Source currency code", example = "USD")
    private String sourceCurrency;

    @Schema(description = "Target currency code", example = "EUR")
    private String targetCurrency;

    @Schema(description = "Amount to be converted", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Converted amount", example = "85.00")
    private BigDecimal convertedAmount;

    @Schema(description = "Transaction date and time", example = "2023-07-01 12:00:00")
    private LocalDateTime transactionDate;
}
