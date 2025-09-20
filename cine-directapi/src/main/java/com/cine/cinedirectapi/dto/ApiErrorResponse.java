package com.cine.cinedirectapi.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiErrorResponse {
    private String message;
    private int status;
}
