package com.cine.cinedirectapi.config;

import com.cine.cinedirectapi.dto.ApiErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@Configuration
public class ErrorHandlerConfiguration {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    @Order(-2) // Ensure this handler takes precedence
    public ErrorWebExceptionHandler customErrorWebExceptionHandler() {
        return (exchange, ex) -> {
            if (exchange.getResponse().isCommitted()) {
                return Mono.error(ex);
            }

            // Set custom response for unmatched routes
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String message = "No static resources found for " + exchange.getRequest().getURI().getPath();
            byte[] errorResponse = null;
            try {
                errorResponse = objectMapper.writeValueAsBytes(ApiErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(message)
                        .build());
            } catch (JsonProcessingException e) {
                String customResponse = "{\"status\":" + HttpStatus.NOT_FOUND.value() + "\"message\": \"" + message + "\" }";
                errorResponse = customResponse.getBytes();
            }

            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory()
                    .wrap(errorResponse)));
        };
    }
}
