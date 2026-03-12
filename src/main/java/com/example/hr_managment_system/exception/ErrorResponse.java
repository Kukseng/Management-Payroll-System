package com.example.hr_managment_system.exception;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ErrorResponse<T>(
        String message,
        Integer code,
        LocalDateTime localDateTime,
        T details
) {
}
