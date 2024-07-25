package com.openpayd.forex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Response payload for currency conversion")
public class CurrencyConversionResponse {

    @Schema(description = "Unique identifier for the conversion transaction", example = "abc123")
    private String transactionId;

    @Schema(description = "Amount after conversion", example = "85.00")
    private BigDecimal convertedAmount;
}
