package com.openpayd.forex.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class FixerLatestResponse {

    private boolean success;
    private long timestamp;
    private String base;
    private Map<String, BigDecimal> rates;
}
