package com.capstone.employeeSystem.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;


import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Get the list of all validation errors from the BindingResult
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(objectError -> {
                    FieldError fieldError = (FieldError) objectError;
                    return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        // Return the validation error messages in the response body
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>(errorMessage, HttpStatus.BAD_REQUEST, null));
    }

    // Handle Access Denied Exception (Unauthorized role)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO<String>> handleAccessDeniedException(AccessDeniedException ex) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "You do not have permission to access this resource.";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseDTO<>(errorMessage, HttpStatus.FORBIDDEN, null));
    }

    // Handle Authentication Exception (Unauthenticated user)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDTO<String>> handleAuthenticationException(AuthenticationException ex) {
        String errorMessage = "You need to authenticate to access this resource.";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseDTO<>(errorMessage, HttpStatus.UNAUTHORIZED, null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDTO<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDTO<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
    }
}
