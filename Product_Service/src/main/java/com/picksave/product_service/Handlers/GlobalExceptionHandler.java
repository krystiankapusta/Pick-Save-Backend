package com.picksave.product_service.Handlers;

import com.picksave.product_service.Responses.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidEnum(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        logger.info("Weight unit unit message -> {}", message);
        if (message != null) {
            if(message.contains("WeightUnit") && message.contains("Cannot coerce empty String")) {
                return ResponseEntity.badRequest().body(new MessageResponse("Weight unit cannot be empty. Allowed values: G, KG, ML, L, g, kg, ml, l"));
            }
            if(message.contains("WeightUnit")){
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid value for weight unit. Allowed values: G, KG, ML, L, g, kg, ml, l"));
            }
        }

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Malformed JSON request"));
    }
}
