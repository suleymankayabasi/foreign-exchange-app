package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.service.ConversionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConversionHistoryController {

    @Autowired
    private ConversionHistoryService conversionHistoryService;

    @GetMapping("/conversion-history")
    public Page<ConversionHistoryResponse> getConversionHistory(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String transactionDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return conversionHistoryService.getConversionHistory(transactionId, transactionDate, page, size);
    }

}