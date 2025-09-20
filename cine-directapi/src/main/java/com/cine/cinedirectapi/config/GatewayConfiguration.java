package com.cine.cinedirectapi.config;

import com.cine.cinedirectapi.filter.JWTFilterSpec;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Resource
    private ServiceLocatorConfiguration serviceLocatorConfiguration;

    @Resource
    private JWTFilterSpec jwtFilterSpec;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("cine-authentication-service", r ->
                        r.path("/api/v1/authenticate/token",
                                        "/api/v1/authenticate/verify",
                                        "/api/v1/authenticate/authorize")
                                .and()
                                .method("POST")
                                .uri(serviceLocatorConfiguration.getAuthenticationServiceHost()))
                .route("cine-authentication-service", r ->
                        r.path("/api/v1/accounts/register")
                                .and()
                                .method("POST")
                                .uri(serviceLocatorConfiguration.getAuthenticationServiceHost()))
                .route("cine-authentication-service", r ->
                        r.path("/api/v1/accounts/update")
                                .and()
                                .method("POST")
                                .filters(jwtFilterSpec::filterForUser)
                                .uri(serviceLocatorConfiguration.getAuthenticationServiceHost()))
                .route("cine-authentication-service", r ->
                        r.path("/api/v1/users/**")
                                .and()
                                .method("POST", "GET", "DELETE")
                                .filters(jwtFilterSpec::filterForAdmin)
                                .uri(serviceLocatorConfiguration.getAuthenticationServiceHost()))
                .build();
    }
}
