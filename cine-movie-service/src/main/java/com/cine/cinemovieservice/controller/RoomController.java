package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.*;
import com.cine.cinemovieservice.entity.Room;
import com.cine.cinemovieservice.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/rooms")
@Tag(name = "Rooms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Tag(name = "Fetch all rooms")
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> fetchAll() {
        try {
            return ResponseEntity.ok(
                    ApiResponse.<List<RoomResponseDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(roomService.fetchAll())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<RoomResponseDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Tag(name = "Retrieve room by room id")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> fetchById(@PathVariable @NotNull Long id) {
        try {
            return roomService.getDetails(id)
                    .map(room -> ResponseEntity.ok(
                            ApiResponse.<RoomResponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(room)
                                    .build()
                    ))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<RoomResponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("Room not found with id " + id)
                                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<RoomResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }


    @PostMapping
    @Tag(name = "Create a room")
    public ResponseEntity<ApiResponse<Room>> create(
            @Valid @RequestBody CreateRoomRequestDTO request) {
        try {
            Room savedRoom = roomService.save(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(savedRoom)
                            .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Delete a room by room id")
    public ResponseEntity<ApiResponse<Room>> delete(@PathVariable @NotNull Long id) {
        try {
            Optional<RoomResponseDTO> isActive = roomService.getDetails(id);
            roomService.delete(id);

            if (isActive.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Room>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Room not found with id " + id)
                                .build());
            }

            return ResponseEntity
                    .ok(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .message("Room deleted successfully")
                            .build());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @PutMapping("/{roomId}")
    @Tag(name = "Update room details")
    public ResponseEntity<ApiResponse<Room>> update(
            @PathVariable Long roomId,
            @RequestBody @Valid UpdateRoomRequestDTO updateRoomRequestDTO) {
        try {
            Room updatedRoom = roomService.update(updateRoomRequestDTO);

            if (updatedRoom == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Room>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Room not found with id " + roomId)
                                .build());
            }

            return ResponseEntity
                    .ok(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(updatedRoom)
                            .message("Room updated successfully")
                            .build());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }
    @PutMapping("/{id}/restore")
    @Tag(name = "Restore room by id")
    public ResponseEntity<ApiResponse<RestoreRoomResponseDTO>> restore(@PathVariable Long id) {
        try {
            Optional<RoomResponseDTO> restoredRoom = roomService.restore(id);

            if (restoredRoom.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<RestoreRoomResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Room not found or not deleted")
                                .build());
            }

            return ResponseEntity.ok(
                    ApiResponse.<RestoreRoomResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .message("Room restored successfully")
                            .data(RestoreRoomResponseDTO.builder().id(restoredRoom.get().getId()).build())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<RestoreRoomResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Error restoring room: " + e.getMessage())
                            .build());
        }
    }

}
