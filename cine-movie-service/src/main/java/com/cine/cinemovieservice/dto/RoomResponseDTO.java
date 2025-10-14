package com.cine.cinemovieservice.dto;

import com.cine.cinemovieservice.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponseDTO {
        private Long id;
        private String roomName;
        private String roomRow;
        private int roomColumn;
        private List<Seat> seats;
        private int totalSeats;
        private Boolean deleted;
}


