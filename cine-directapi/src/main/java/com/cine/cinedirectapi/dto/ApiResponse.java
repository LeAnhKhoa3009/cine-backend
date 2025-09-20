package com.cine.cinedirectapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T>{

    @JsonProperty("status")
    private ApiResponseStatus status;

    @JsonProperty("data")
    private T data;

    @JsonProperty("message")
    private String message;

    public enum ApiResponseStatus{
        SUCCESS, FAILURE, ERROR
    }
}
