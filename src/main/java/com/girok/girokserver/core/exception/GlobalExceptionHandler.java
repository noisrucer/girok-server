package com.girok.girokserver.core.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        // Extract error info
        ErrorInfo errorInfo = e.getErrorInfo();

        // Build response body
        ErrorResponse errorResponse = new ErrorResponse(
                errorInfo.getStatusCode(),
                errorInfo.getErrorCode(),
                e.getMessage()
        );

        return ResponseEntity
                .status(errorInfo.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "";
        String messageCode = fieldError != null && fieldError.getCode() != null ? fieldError.getCode() : "";
        ErrorResponse errorResponse = getErrorResponse(messageCode, errorMessage);
        return new ResponseEntity<>(errorResponse, headers, BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(400, "INVALID_INPUT_FORMAT", e.getMessage());
        return new ResponseEntity<>(errorResponse, headers, BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(500, "INTERNAL_ERROR", e.getMessage());
        return new ResponseEntity<>(errorResponse, headers, INTERNAL_SERVER_ERROR);
    }

    public ErrorResponse getErrorResponse(String originCode, String message) {
        return switch (originCode) {
            case "NotBlank" -> new ErrorResponse(BAD_REQUEST.value(), "EMPTY_FIELD", message);
            case "Email" -> new ErrorResponse(BAD_REQUEST.value(), "INVALID_EMAIL_FORMAT", message);
            case "Pattern" -> new ErrorResponse(BAD_REQUEST.value(), "INVALID_INPUT_FORMAT", message);
            case "Max", "Min", "Size" -> new ErrorResponse(BAD_REQUEST.value(), "INVALID_FIELD_SIZE", message);
            default -> new ErrorResponse(INTERNAL_SERVER_ERROR.value(), "UNEXPECTED_ERROR", message);
        };
    }

}
