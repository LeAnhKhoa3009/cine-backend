package com.cine.cinedirectapi.filter;

import com.cine.cinedirectapi.enumeration.UserRole;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.stereotype.Component;

@Component
public class JWTFilterSpec {

    @Resource
    private JWTFilterService jwtFilterService;


    public GatewayFilterSpec filterForUser(GatewayFilterSpec f) {
        return f.filter((exchange, chain) -> jwtFilterService.filter(exchange, chain, UserRole.USER));
    }

    public GatewayFilterSpec filterForAdmin(GatewayFilterSpec f) {
        return f.filter((exchange, chain) -> jwtFilterService.filter(exchange, chain, UserRole.ADMIN));
    }
}
