package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ExchangeRateData;
import com.openpayd.forex.dto.ExchangeRateResponse;
import com.openpayd.forex.mapper.ExchangeRateMapper;
import com.openpayd.forex.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    @GetMapping("/exchange-rates")
    @Operation(summary = "Get exchange rate", description = "Get exchange rate between two currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rate",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input, please provide valid currency codes",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @Parameter(
                    description = "Source currency code in ISO 4217 format",
                    required = true,
                    example = "USD"
            )
            @RequestParam("fromCurrency")
            @NotNull(message = "From currency code cannot be blank")
            @Pattern(regexp = "^[A-Z]{3}$", message = "From currency code must be a 3-letter uppercase string")
            String fromCurrency,

            @Parameter(
                    description = "Target currency code in ISO 4217 format",
                    required = true,
                    example = "EUR"
            )
            @RequestParam("toCurrency")
            @NotNull(message = "To currency code cannot be blank")
            @Pattern(regexp = "^[A-Z]{3}$", message = "To currency code must be a 3-letter uppercase string")
            String toCurrency) {

        log.info("Received request to get exchange rate from {} to {}", fromCurrency, toCurrency);

        ExchangeRateData exchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
        ExchangeRateResponse response = mapper.toResponse(exchangeRate);
        log.info("Successfully retrieved exchange rate: {}", exchangeRate);
        return ResponseEntity.ok(response);
    }
}
