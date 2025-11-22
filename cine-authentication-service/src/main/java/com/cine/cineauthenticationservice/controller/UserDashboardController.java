package com.cine.cineauthenticationservice.controller;

import com.cine.cineauthenticationservice.dto.*;
import com.cine.cineauthenticationservice.entity.User;
import com.cine.cineauthenticationservice.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDashboardController {

    private final UserService userService;

    public UserDashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Tag(name = "Fetch Users")
    public ResponseEntity<ApiResponse<Page<User>>> fetchAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pagination = Pageable.ofSize(10).withPage(0);
            Page<User> users = userService.fetchAllUsers(pagination);
            return ResponseEntity.ok(
                    ApiResponse.<Page<User>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(users)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<Page<User>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Tag(name = "Fetch User by ID")
    public ResponseEntity<ApiResponse<RetrieveUserReponseDTO>> fetchById(@PathVariable Long id) {
        try {
            return Optional.ofNullable(userService.findById(id))
                    .map(user -> ResponseEntity
                            .ok(ApiResponse.<RetrieveUserReponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(user)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("User not found with id " + id)
                                    .build()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/username/{email}")
    @Tag(name = "Fetch User by Email")
    public ResponseEntity<ApiResponse<RetrieveUserReponseDTO>> fetchByEmail(@PathVariable String email) {
        try {
            return Optional.ofNullable(userService.findByEmail(email))
                    .map(user -> ResponseEntity
                            .ok(ApiResponse.<RetrieveUserReponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(user)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("User not found with email " + email)
                                    .build()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping
    @Tag(name = "Save User")
    public ResponseEntity<ApiResponse<RetrieveUserReponseDTO>> save(
            @Valid @RequestBody SaveUserRequestDTO request) {
        try {
            RetrieveUserReponseDTO savedUser = userService.saveUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(savedUser)
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



    @DeleteMapping("/{id}")
    @Tag(name = "Deactivate User")
    public ResponseEntity<ApiResponse<DeactiveUserResponseDTO>> deactivate(@PathVariable Long id) {
        try {
            RetrieveUserReponseDTO user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<DeactiveUserResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("User not found with id " + id)
                                .build());
            }
            DeactiveUserResponseDTO deactivatedUser = userService.deactiveUser(user.getEmail());
            return ResponseEntity.ok(
                    ApiResponse.<DeactiveUserResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(deactivatedUser)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DeactiveUserResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/{id}")
    @Tag(name = "Restore User")
    public ResponseEntity<ApiResponse<RestoreUserResponseDTO>> restore(@PathVariable Long id) {
        try {
            RetrieveUserReponseDTO user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<RestoreUserResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("User not found with id " + id)
                                .build());
            }
            RestoreUserResponseDTO restoredUser = userService.restoreUser(user.getEmail());
            return ResponseEntity.ok(
                    ApiResponse.<RestoreUserResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(restoredUser)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<RestoreUserResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }
}
