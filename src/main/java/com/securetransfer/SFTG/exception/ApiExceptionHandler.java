// src/main/java/com/example/sftg/exception/ApiExceptionHandler.java
package com.securetransfer.SFTG.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    private Map<String, Object> baseBody(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(
                baseBody(
                        HttpStatus.PAYLOAD_TOO_LARGE,
                        "File Too Large",
                        "Uploaded file exceeds the maximum allowed size (100MB)"
                ),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                baseBody(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(ForbiddenException ex) {
        return new ResponseEntity<>(
                baseBody(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(LinkExpiredException.class)
    public ResponseEntity<Object> handleLinkExpired(LinkExpiredException ex) {
        return new ResponseEntity<>(
                baseBody(HttpStatus.GONE, "Link Expired", ex.getMessage()),
                HttpStatus.GONE
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                baseBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleUnauthorized(BadCredentialsException ex) {
        return new ResponseEntity<>(
                baseBody(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Object> handleFileError(FileStorageException ex) {
        return new ResponseEntity<>(
                baseBody(HttpStatus.INTERNAL_SERVER_ERROR, "File Storage Error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return new ResponseEntity<>(
                baseBody(HttpStatus.BAD_REQUEST, "Validation Error", message),
                HttpStatus.BAD_REQUEST
        );
    }


    // MUST be LAST
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                baseBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                        "An unexpected error occurred. Please try again later."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
