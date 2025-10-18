package com.cine.cinemovieservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RawImageResponseDTO(String eTag, LocalDateTime updatedTime, String contentType, byte[] content) {
}
