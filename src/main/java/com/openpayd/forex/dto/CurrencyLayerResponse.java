package com.openpayd.forex.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyLayerResponse extends ApiResponse {
    private String terms;
    private String privacy;
    private String source;
    private Map<String, BigDecimal> quotes;
}