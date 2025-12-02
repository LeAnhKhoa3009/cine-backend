package com.cine.cinemovieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GenreResponseDTO {

    private Long id;

    private String name;

    private String icon;

    private boolean deleted;
}
