package com.ebbe3000.spring_boot_library.dto;

import java.time.Instant;

public record ErrorResponse(
        String error,
        String message,
        Instant timestamp) {

    public ErrorResponse(String error, String message) {
        this(error, message, Instant.now());
    }
}
