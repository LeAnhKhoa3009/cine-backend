package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.ApiResponse;
import com.cine.cinemovieservice.dto.UpdateSeatRequestDTO;
import com.cine.cinemovieservice.entity.Seat;
import com.cine.cinemovieservice.service.SeatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/seats")
@Tag(name = "Seat")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<Seat>>> getAllSeatsByRoom(@PathVariable @NotNull Long roomId) {
        try {
            List<Seat> seats = seatService.fetchAllWithRoomId(roomId);
            return ResponseEntity.ok(
                    ApiResponse.<List<Seat>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(seats)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error fetching seats for room {}: {}", roomId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<Seat>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @GetMapping("/{roomId}/seats/{seatId}")
    public ResponseEntity<ApiResponse<Seat>> getSeatById(@PathVariable @NotNull Long roomId, @PathVariable @NotNull Long seatId) {
        try {
            Optional<Seat> seatOpt = seatService.getDetails(roomId, seatId);
            return seatOpt
                    .map(seat -> ResponseEntity.ok(
                            ApiResponse.<Seat>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(seat)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<Seat>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("Seat not found with id " + seatId)
                                    .build()));
        } catch (Exception e) {
            log.error("Error getting seat {}: {}", seatId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<Seat>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @PutMapping("/{seatId}")
    public ResponseEntity<ApiResponse<Seat>> updateSeat(
            @PathVariable Long seatId,
            @RequestBody @Valid UpdateSeatRequestDTO updateSeatRequestDTO) {
        try {
            updateSeatRequestDTO.setId(seatId);
            Seat updatedSeat = seatService.update(updateSeatRequestDTO);

            if (updatedSeat == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Seat>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Seat not found with id " + seatId)
                                .build());
            }
            return ResponseEntity.ok(
                    ApiResponse.<Seat>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(updatedSeat)
                            .message("Seat updated successfully")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error updating seat {}: {}", seatId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<Seat>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

}
