package com.app.cisco.ciscoservice.exceptions;

import com.app.cisco.ciscoservice.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for HTTP 400 Errors.
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            if (errors.containsKey(error.getField())) {
                errors.put(error.getField(), String.format("%s, %s", errors.get(error.getField()), error.getDefaultMessage()));
            } else {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        });

        return new ApiResponse(errors, "VALIDATION_FAILED");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse handleUserNotFoundExceptions(UserNotFoundException ex) {
        return new ApiResponse(null, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PhoneNotFoundException.class)
    public ApiResponse handlePhoneNotFoundExceptions(PhoneNotFoundException ex) {
        return new ApiResponse(null, ex.getMessage());
    }


}
