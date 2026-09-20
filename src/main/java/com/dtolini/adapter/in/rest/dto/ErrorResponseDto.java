package com.dtolini.adapter.in.rest.dto;

import java.time.Instant;

public record ErrorResponseDto(String code, String message, Instant timestamp) {

    public static ErrorResponseDto of(String code, String message) {
        return new ErrorResponseDto(code, message, Instant.now());
    }
}
