package com.cine.cinemovieservice.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageValidator {
    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB
    private static final String[] ALLOWED = {"image/png", "image/jpeg", "image/webp", "image/gif"};

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is empty");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("file exceeds 5MB");
        String contentType = file.getContentType();
        if (contentType == null || !isAllowed(contentType))
            throw new IllegalArgumentException("unsupported content-type: " + contentType);
    }

    private boolean isAllowed(String ct) {
        for (String a : ALLOWED) if (a.equalsIgnoreCase(ct)) return true;
        return false;
    }
}
