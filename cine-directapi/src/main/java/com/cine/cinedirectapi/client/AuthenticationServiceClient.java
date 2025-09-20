package com.cine.cinedirectapi.client;

import com.cine.cinedirectapi.config.ServiceLocatorConfiguration;
import com.cine.cinedirectapi.dto.ApiResponse;
import com.cine.cinedirectapi.dto.AuthorizeRequestDTO;
import com.cine.cinedirectapi.dto.AuthorizeResponseDTO;
import com.cine.cinedirectapi.enumeration.UserRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class AuthenticationServiceClient {

    private final ObjectMapper objectMapper;
    private final ServiceLocatorConfiguration serviceLocatorConfiguration;
    private final RestTemplate restTemplate;

    public AuthenticationServiceClient(ServiceLocatorConfiguration serviceLocatorConfiguration, RestTemplate restTemplate) {
        this.serviceLocatorConfiguration = serviceLocatorConfiguration;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    private String buildUri(String path) {
        return serviceLocatorConfiguration.getAuthenticationServiceHost() + path;
    }

    public boolean authorize(String token, UserRole role) {
        try {
            String responseAsString = restTemplate.postForObject(buildUri("/api/v1/authenticate/authorize"),
                    AuthorizeRequestDTO.builder()
                            .accessToken(token)
                            .role(role)
                            .build(),
                    String.class);

            ApiResponse<AuthorizeResponseDTO> response = objectMapper.readValue(responseAsString, new TypeReference<ApiResponse<AuthorizeResponseDTO>>() {});
            return response.getStatus().equals(ApiResponse.ApiResponseStatus.SUCCESS)
                    && response.getData().isValid();
        } catch (Exception e) {
            log.error("Error while verifying token: {}", e.getMessage());
            return false;
        }
    }
}
