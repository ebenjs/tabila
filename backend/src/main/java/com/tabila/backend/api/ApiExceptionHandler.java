package com.tabila.backend.api;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                FieldError::getField,
                                fieldError -> fieldError.getDefaultMessage() == null ? "Invalide"
                                        : fieldError.getDefaultMessage(),
                                (left, right) -> left));

        return ResponseEntity.badRequest().body(
                Map.of("timestamp", Instant.now().toString(), "status", 400, "error", "Validation error", "fields",
                        fields));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp",
                        Instant.now().toString(),
                        "status",
                        status.value(),
                        "error",
                        status.getReasonPhrase(),
                        "message",
                        ex.getReason() == null ? "Erreur" : ex.getReason()));
    }
}
