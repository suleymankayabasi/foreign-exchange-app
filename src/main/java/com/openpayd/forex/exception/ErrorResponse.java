package com.openpayd.forex.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
}
