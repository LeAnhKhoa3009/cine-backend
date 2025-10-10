package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.ApiResponse;
import com.cine.cinemovieservice.dto.CreateRoomRequestDTO;
import com.cine.cinemovieservice.dto.UpdateRoomRequestDTO;
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
    public ResponseEntity<ApiResponse<List<Room>>> getAllRooms() {
        try {
            return ResponseEntity
                    .ok(ApiResponse.<List<Room>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(roomService.fetchAll())
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Room>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Room>> getRoomById(@PathVariable @NotNull Long roomId) {
        try {
            return roomService.getDetails(roomId)
                    .map(room -> ResponseEntity
                            .ok(ApiResponse.<Room>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(room)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<Room>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("Room not found with id " + roomId)
                                    .build()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Room>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Room>> createRoom(
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

    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Room>> deleteRoom(@PathVariable @NotNull Long roomId) {
        try {
            Optional<Room> isActive = roomService.getDetails(roomId);
            roomService.delete(roomId);

            if (isActive.isEmpty()) {
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
    public ResponseEntity<ApiResponse<Room>> updateRoom(
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
}
