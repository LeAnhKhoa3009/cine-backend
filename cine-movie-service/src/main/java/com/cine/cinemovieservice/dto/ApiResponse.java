package com.cine.cinemovieservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
