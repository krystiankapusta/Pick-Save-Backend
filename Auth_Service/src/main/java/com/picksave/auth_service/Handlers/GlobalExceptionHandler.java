package com.picksave.auth_service.Handlers;

import com.picksave.auth_service.Exceptions.CustomAppException;
import org.springframework.http.ResponseEntity;
import com.picksave.auth_service.Responses.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<ErrorResponse> handleCustomAppException(CustomAppException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

}
