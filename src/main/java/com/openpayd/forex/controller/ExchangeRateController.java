package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ExchangeRateRequest;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/exchange-rate")
    @Operation(summary = "Get exchange rate", description = "Get exchange rate between two currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rate",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input, please provide valid currency codes",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<BigDecimal> getExchangeRate(
            @Parameter(description = "Exchange rate request payload", required = true)
            @Valid @RequestBody ExchangeRateRequest request) throws ExternalServiceException {

        logger.info("Received request to get exchange rate from {} to {}", request.getFromCurrency(), request.getToCurrency());

        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(request.getFromCurrency(), request.getToCurrency());
        logger.info("Successfully retrieved exchange rate: {}", exchangeRate);
        return ResponseEntity.ok(exchangeRate);
    }
}
