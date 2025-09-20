package com.cine.cinedirectapi.filter;

import com.cine.cinedirectapi.client.AuthenticationServiceClient;
import com.cine.cinedirectapi.dto.ApiErrorResponse;
import com.cine.cinedirectapi.enumeration.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class JWTFilterService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final AuthenticationServiceClient authenticationServiceClient;

    public JWTFilterService(AuthenticationServiceClient authenticationServiceClient) {
        this.authenticationServiceClient = authenticationServiceClient;
    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain, UserRole role) {
        if (role != null) {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if(authenticationServiceClient.authorize(token, role)) {
                    return chain.filter(exchange);
                }
            }

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String message = "Invalid credentials";
            byte[] errorResponse = null;
            try {
                errorResponse = objectMapper.writeValueAsBytes(ApiErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message(message)
                        .build());
            } catch (JsonProcessingException e) {
                String customResponse = "{\"status\":" + HttpStatus.UNAUTHORIZED.value() + "\"message\": \"" + message + "\" }";
                errorResponse = customResponse.getBytes();
            }
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory()
                    .wrap(errorResponse)));
        }

        return chain.filter(exchange);
    }
}
