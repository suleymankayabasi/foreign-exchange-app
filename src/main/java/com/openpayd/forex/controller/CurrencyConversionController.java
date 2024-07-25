package com.openpayd.forex.controller;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.DatabaseException;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.CurrencyConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrencyConversionController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);

    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public CurrencyConversionController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @PostMapping("/currency-conversion")
    @Operation(summary = "Convert currency", description = "Convert currency with given source and target currency codes and amount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted currency",
                    content = @Content(schema = @Schema(implementation = CurrencyConversionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input, please provide valid request data",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<CurrencyConversionResponse> convertCurrency(
            @RequestBody(description = "Details of the currency conversion request", required = true,
                    content = @Content(schema = @Schema(implementation = CurrencyConversionRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody CurrencyConversionRequest request) {

        logger.info("Received currency conversion request. Request details: {}", request);

        try {
            CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);
            logger.info("Successfully processed currency conversion request. Response details: {}", response);
            return ResponseEntity.ok(response);
        } catch (ExternalServiceException e) {
            logger.error("External service error during currency conversion", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        } catch (DatabaseException e) {
            logger.error("Database error during currency conversion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error during currency conversion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
