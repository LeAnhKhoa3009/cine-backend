package com.cine.cinemovieservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class SwaggerConfig {

    @Value("${server.port}")
    private String port;

    @Bean
    public OpenAPI customOpenAPI() {

        log.info("OpenAPI for Movie Service API is available at http://localhost:{}/swagger-ui.html", port);
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Service API")
                        .version("1.0")
                        .description("API documentation for Movie Service"));
    }
}


