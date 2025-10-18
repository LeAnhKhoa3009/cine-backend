package com.cine.cinemovieservice.dto;

import lombok.Builder;

@Builder
public record UploadImageReponseDTO(Long id, String name, String eTag) {}