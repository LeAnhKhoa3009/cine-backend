package com.cine.cinedirectapi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ServiceLocatorConfiguration {

    @Value("${cine.service.authentication.url}")
    private String authenticationServiceHost;


}
