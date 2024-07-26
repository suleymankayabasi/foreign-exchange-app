package com.openpayd.forex.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FixerLatestResponse extends ApiResponse {
    private String base;
    private String date;
    private Map<String, BigDecimal> rates;
}