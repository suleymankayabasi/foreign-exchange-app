package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.exception.ResourceNotFoundException;
import com.openpayd.forex.service.ConversionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConversionHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(ConversionHistoryController.class);

    private final ConversionHistoryService conversionHistoryService;

    @Autowired
    public ConversionHistoryController(ConversionHistoryService conversionHistoryService) {
        this.conversionHistoryService = conversionHistoryService;
    }

    @GetMapping("/conversion-history")
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
            @Parameter(description = "Page number for pagination", example = "0", required = true)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for pagination", example = "10", required = true)
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Received request to get conversion history. Transaction ID: {}, Transaction Date: {}, Page: {}, Size: {}",
                transactionId, transactionDate, page, size);

        try {
            Page<ConversionHistoryResponse> response = conversionHistoryService.getConversionHistory(transactionId, transactionDate, page, size);
            logger.info("Successfully retrieved conversion history. Number of records: {}", response.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (InvalidInputException e) {
            logger.error("Invalid input error retrieving conversion history", e);
            return ResponseEntity.badRequest().build();
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found error retrieving conversion history", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving conversion history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
