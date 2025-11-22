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
@Tag(name = "Users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDashboardController {

    private final UserService userService;

    public UserDashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Tag(name = "Retrieve all users with pagination")
    public ResponseEntity<ApiResponse<Page<User>>> fetchAllUsers(
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

    @GetMapping("/{userId}")
    @Tag(name = "Retrieve user by id")
    public ResponseEntity<ApiResponse<RetrieveUserReponseDTO>> getUserById(@PathVariable Long userId) {
        try {
            return Optional.ofNullable(userService.findById(userId))
                    .map(user -> ResponseEntity
                            .ok(ApiResponse.<RetrieveUserReponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(user)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<RetrieveUserReponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("User not found with id " + userId)
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
    @Tag(name = "Retrieve user by email")
    public ResponseEntity<ApiResponse<RetrieveUserReponseDTO>> getUserByEmail(@PathVariable String email) {
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
    @Tag(name = "Save user in dashboard")
    public ResponseEntity<ApiResponse<RetrieveUserReponseDTO>> saveUser(
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



    @DeleteMapping("/{userId}")
    @Tag(name = "Deactivate user")
    public ResponseEntity<ApiResponse<DeactiveUserResponseDTO>> deactivateUser(@PathVariable Long userId) {
        try {
            RetrieveUserReponseDTO user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<DeactiveUserResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("User not found with id " + userId)
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

    @PostMapping("/{userId}")
    @Tag(name = "Restore user")
    public ResponseEntity<ApiResponse<RestoreUserResponseDTO>> restoreUser(@PathVariable Long userId) {
        try {
            RetrieveUserReponseDTO user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<RestoreUserResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("User not found with id " + userId)
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
