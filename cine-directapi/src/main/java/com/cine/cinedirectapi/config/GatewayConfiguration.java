package com.cine.cinedirectapi.config;

import com.cine.cinedirectapi.filter.JWTFilterSpec;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties(GlobalCorsProperties.class)
public class GatewayConfiguration {

    @Resource
    private ServiceLocatorConfiguration serviceLocatorConfiguration;

    @Resource
    private JWTFilterSpec jwtFilterSpec;

    @Resource
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration cors = new CorsConfiguration();

        // YAML: allowedOrigins: ["*"]
        // If you ever need credentials, use setAllowedOriginPatterns and a concrete origin instead of "*".
        cors.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));

        // YAML: allowedMethods: ["*"], allowedHeaders: ["*"], exposedHeaders: ["*"]
        cors.addAllowedMethod(CorsConfiguration.ALL);   // "*"
        cors.addAllowedHeader(CorsConfiguration.ALL);   // "*"
        cors.addExposedHeader(CorsConfiguration.ALL);   // "*"

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return new CorsWebFilter(source);
    }

    private static void dedupeKeepFirst(HttpHeaders headers, String name) {
        var values = headers.get(name);
        if (values != null && values.size() > 1) {
            headers.set(name, values.get(0)); // keep first, drop duplicates
        }
    }

    @Bean
    public GlobalFilter dedupeCorsHeadersFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> {
                HttpHeaders h = exchange.getResponse().getHeaders();
                dedupeKeepFirst(h, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                dedupeKeepFirst(h, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                return Mono.empty();
            });
            return chain.filter(exchange);
        };
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("cine-authentication-service", r ->
                        r.path("/api/v1/authenticate/token",
                                        "/api/v1/authenticate/verify",
                                        "/api/v1/authenticate/authorize")
                                .and()
                                .method("POST", "OPTIONS")
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
                .route("cine-movie-service", r ->
                        r.path("/api/v1/movies/**", "/api/v1/genres/**", "/api/v1/rooms/**", "/api/v1/seats/**")
                                .and()
                                .method("POST", "PUT", "GET", "DELETE")
                                .filters(jwtFilterSpec::filterForAdmin)
                                .uri(serviceLocatorConfiguration.getMovieServiceHost()))
                .build();
    }
}
