package com.openpayd.forex.dto;

import lombok.Data;

@Data
public abstract class ApiResponse {
    private boolean success;
    private long timestamp;
}
