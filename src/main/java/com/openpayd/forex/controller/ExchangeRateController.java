package com.openpayd.forex.controller;

import com.openpayd.forex.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/exchange-rate")
    public BigDecimal getExchangeRate(@RequestParam String fromCurrency, @RequestParam String toCurrency) {
        return exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
    }
}