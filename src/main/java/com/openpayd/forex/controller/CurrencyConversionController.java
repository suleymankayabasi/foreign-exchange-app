package com.openpayd.forex.controller;

import com.openpayd.forex.dto.CurrencyConversionData;
import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.mapper.CurrencyConversionMapper;
import com.openpayd.forex.service.CurrencyConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CurrencyConversionController {

    private final CurrencyConversionService currencyConversionService;
    private final CurrencyConversionMapper mapper = CurrencyConversionMapper.INSTANCE;

    @PostMapping("/currencies/convert")
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
            @Valid @NotNull(message = " Currency conversion request cannot be null or empty ")
            @org.springframework.web.bind.annotation.RequestBody CurrencyConversionRequest request) {

        log.debug("Received currency conversion request. Request details: {}", request);

        CurrencyConversionData currencyConversionData = currencyConversionService.convertCurrency(request);
        CurrencyConversionResponse response = mapper.toResponse(currencyConversionData);
        log.info("Successfully processed currency conversion request. Response details: {}", response);
        return ResponseEntity.ok(response);
    }
}
