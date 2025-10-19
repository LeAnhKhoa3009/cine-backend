package com.cine.cineauthenticationservice.controller;

import com.cine.cineauthenticationservice.dto.*;
import com.cine.cineauthenticationservice.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/accounts")
@Tag(name = "Accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserAccountController {

    private final UserService userService;

    public UserAccountController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> registerUser(
            @Valid @RequestBody RegisterRequestDTO request) {
        try {
            RegisterResponseDTO savedUser = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<RegisterResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(savedUser)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<RegisterResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<RegisterResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<RetrieveUserReponseDTO>> updateUser(@RequestBody ProfileUpdateRequestDTO request) {
        try {
            RetrieveUserReponseDTO retrieveUserDTO = userService.findByEmail(request.getEmail());
            if(retrieveUserDTO != null){
                SaveUserRequestDTO saveUserRequestDTO = SaveUserRequestDTO.builder()
                        .id(retrieveUserDTO.getId())
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .phoneNumber(request.getPhoneNumber())
                        .build();

                RetrieveUserReponseDTO updateddUser = userService.saveUser(saveUserRequestDTO);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                .data(updateddUser)
                                .build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message("User not found with email: " + request.getEmail())
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }
}
