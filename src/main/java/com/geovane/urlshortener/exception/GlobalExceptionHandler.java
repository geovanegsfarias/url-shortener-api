package com.geovane.urlshortener.exception;

import com.geovane.urlshortener.dto.ErrorResponseDto;
import io.micrometer.common.lang.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleShortCodeNotFound(ShortCodeNotFoundException e, WebRequest request) {
        ErrorResponseDto body = mapExceptionToErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
            (MethodArgumentNotValidException e, @NonNull HttpHeaders headers, HttpStatusCode status, @NonNull WebRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error occurred";
        ErrorResponseDto body = mapExceptionToErrorResponseDto(
                status.value(),
                errorMessage,
                request
        );
        return ResponseEntity.status(status).headers(headers).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception e, WebRequest request) {
        ErrorResponseDto body = mapExceptionToErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error occurred",
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ErrorResponseDto mapExceptionToErrorResponseDto(int status, String message, WebRequest request) {
        return new ErrorResponseDto(
                status,
                Instant.now(),
                message,
                request.getDescription(false)
        );
    }
}