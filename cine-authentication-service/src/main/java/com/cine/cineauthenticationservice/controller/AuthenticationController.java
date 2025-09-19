package com.cine.cineauthenticationservice.controller;

import com.cine.cineauthenticationservice.dto.*;
import com.cine.cineauthenticationservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/authenticate")
@Tag(name = "Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthenticationResponseDTO>> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        try {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.<AuthenticationResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(authenticationService.authenticate(authenticationRequestDTO))
                            .build());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<AuthenticationResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyResponseDTO>> verify(@RequestBody VerifyRequestDTO verifyRequestDTO) {
        try{
            if (StringUtils.isBlank(verifyRequestDTO.getAccessToken())) {
                throw new RuntimeException("Token must not be empty");
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.<VerifyResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(authenticationService.verify(verifyRequestDTO.getAccessToken()))
                            .build());
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<VerifyResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }
}
