package com.example.hr_managment_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ServiceException {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleServiceException(ResponseStatusException e) {

        ErrorResponse<?> errorResponse =ErrorResponse.builder()
                .message(e.getMessage())
                .code(e.getStatusCode().value())
                .localDateTime(LocalDateTime.now())
                .details(e.getReason())
                .build();
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(org.springframework.security.core.AuthenticationException e) {
        ErrorResponse<?> errorResponse = ErrorResponse.builder()
                .message("Authentication failed: " + e.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .localDateTime(LocalDateTime.now())
                .details(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}

