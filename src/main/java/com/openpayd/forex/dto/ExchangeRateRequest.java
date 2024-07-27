package com.openpayd.forex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for fetching the exchange rate")
public class ExchangeRateRequest {

    @NotNull(message = "From currency code cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "From currency code must be a 3-letter uppercase string")
    @Schema(description = "The currency code to convert from", example = "USD")
    private String fromCurrency;

    @NotNull(message = "To currency code cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "To currency code must be a 3-letter uppercase string")
    @Schema(description = "The currency code to convert to", example = "EUR")
    private String toCurrency;
}