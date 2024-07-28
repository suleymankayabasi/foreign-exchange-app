package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.mapper.ConversionHistoryMapper;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.service.ConversionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConversionHistoryController {

    @Value("${pagination.default-page-size}")
    private int defaultPageSize;

    @Value("${pagination.default-page-number}")
    private int defaultPageNumber;

    private final ConversionHistoryService conversionHistoryService;
    private final ConversionHistoryMapper conversionHistoryMapper;

    @GetMapping("/conversations")
    @Operation(summary = "Get conversion history", description = "Retrieve conversion history by transaction ID or transaction date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved conversion history",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input, please provide valid parameters",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Page<ConversionHistoryResponse>> getConversionHistory(
            @Parameter(description = "Transaction ID to filter the conversion history", example = "12345")
            @RequestParam(required = false) String transactionId,
            @Parameter(description = "Transaction date to filter the conversion history (format: yyyy-MM-dd HH:mm:ss)", example = "2023-07-01 12:00:00")
            @RequestParam(required = false) String transactionDate,
            @Parameter(description = "Page number for pagination", example = "0")
            @RequestParam(defaultValue = "${pagination.default-page-number}") int page,
            @Parameter(description = "Page size for pagination", example = "5")
            @RequestParam(defaultValue = "${pagination.default-page-size}") int size) {

        log.debug("Received request to get conversion history. Transaction ID: {}, Transaction Date: {}, Page: {}, Size: {}",
                transactionId, transactionDate, page, size);

        if ((transactionId == null || transactionId.trim().isEmpty()) &&
                (transactionDate == null || transactionDate.trim().isEmpty()) &&
                (defaultPageSize < 1 || defaultPageNumber < 0)) {
            log.error("Both transactionId and transactionDate cannot be null or empty at the same time.");
            throw new InvalidInputException("Both transactionId and transactionDate cannot be null or empty at the same time or page size and page number cannot be less than 1 or 0.");
        }

        Page<ConversionHistory> conversionHistories = conversionHistoryService.getConversionHistory(transactionId, transactionDate, page, size);
        Page<ConversionHistoryResponse> response = conversionHistories.map(conversionHistoryMapper::toResponse);

        log.info("Successfully retrieved conversion history. Number of records: {}", response.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
