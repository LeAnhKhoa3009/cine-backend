package com.cine.cinemovieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoomRequestDTO {
    private Long id;
    private String roomName;
    private String roomRow;
    private Integer roomColumn;
}
