package com.cine.cinemovieservice.dto;

import lombok.Builder;

@Builder
public record RetrieveImageDTO(Long id,String name,String contentType,long size,String url) {}
