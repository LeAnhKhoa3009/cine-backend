package com.cine.cinedirectapi.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class GatewayMetricsFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Log request details
        log.info("Request: {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
//        exchange.getRequest().getHeaders().forEach((key, value) ->
//                log.info("Header: {} = {}", key, value)
//        );

        // Proceed with the filter chain
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Ensure this filter runs early in the chain
    }
}
