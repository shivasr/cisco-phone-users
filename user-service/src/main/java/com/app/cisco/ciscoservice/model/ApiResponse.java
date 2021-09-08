package com.app.cisco.ciscoservice.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private Map<String, String> errors;

    private String message;
}
