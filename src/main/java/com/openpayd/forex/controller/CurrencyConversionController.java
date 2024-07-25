package com.openpayd.forex.controller;

import com.openpayd.forex.dto.CurrencyConversionRequest;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import com.openpayd.forex.exception.DatabaseException;
import com.openpayd.forex.exception.ExternalServiceException;
import com.openpayd.forex.service.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrencyConversionController {

    @Autowired
    private CurrencyConversionService currencyConversionService;

    @PostMapping("/currency-conversion")
    public CurrencyConversionResponse convertCurrency(@RequestBody CurrencyConversionRequest request) throws ExternalServiceException, DatabaseException {
        return currencyConversionService.convertCurrency(request);
    }
}
