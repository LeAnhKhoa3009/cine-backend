package com.cine.cinemovieservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RetrieveImageFolderDTO {
    private Long id;
    private String name;
    private List<RetrieveImageDTO> images;
}
