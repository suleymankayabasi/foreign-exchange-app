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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrencyConversionController {

    @Autowired
    private CurrencyConversionService currencyConversionService;

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
    public CurrencyConversionResponse convertCurrency(
            @RequestBody(description = "Details of the currency conversion request", required = true,
                    content = @Content(schema = @Schema(implementation = CurrencyConversionRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody CurrencyConversionRequest request)
            throws ExternalServiceException, DatabaseException {
        return currencyConversionService.convertCurrency(request);
    }
}
