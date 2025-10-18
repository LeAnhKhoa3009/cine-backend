package com.cine.cinemovieservice.exception;

public class NotModifiedException extends RuntimeException {
    private String etag;
    public NotModifiedException(String message, String etag) {
        super(message);
        this.etag = etag;
    }

    public String getEtag() {
        return etag;
    }
}