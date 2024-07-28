package com.openpayd.forex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request payload for currency conversion")
public class CurrencyConversionRequest {

    @NotNull(message = "Source currency code cannot be null")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Source currency code must be a 3-letter uppercase string")
    @Schema(description = "Source currency code", example = "USD")
    private String sourceCurrency;

    @NotNull(message = "Target currency code cannot be null")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Target currency code must be a 3-letter uppercase string")
    @Schema(description = "Target currency code", example = "EUR")
    private String targetCurrency;

    @NotNull(message = "Amount cannot be null")
    @PositiveOrZero(message = "Amount must be a positive number or zero")
    @Schema(description = "Amount to convert", example = "100.00")
    private BigDecimal amount;
}
