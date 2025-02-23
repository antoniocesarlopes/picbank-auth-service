package com.picbank.authservice.exceptions;

import com.picbank.authservice.constants.ErrorConstants;
import com.picbank.authservice.model.ErrorResponse;
import com.picbank.authservice.services.MessageService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static com.picbank.authservice.constants.MessageConstants.*;

/**
 * Global exception handler for the application.
 * <p>
 * This class centralizes exception handling and returns consistent error responses.
 * </p>
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    /**
     * Handles validation errors caused by invalid request data.
     *
     * @param ex the exception thrown due to validation failures.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} with details about the validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> (error instanceof FieldError) ?
                        ((FieldError) error).getField() + ": " + error.getDefaultMessage() :
                        error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorConstants.VALIDATION_ERROR,
                messageService.getMessage(ERROR_VALIDATION),
                details
        );

        log.warn("Validation error: {}", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles authentication and authorization failures.
     *
     * @param ex the {@link AuthException} thrown when authentication fails.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} with authentication failure details.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ErrorConstants.AUTH_ERROR,
                messageService.getMessage(AUTH_ERROR_VALIDATION),
                List.of(ex.getMessage())
        );

        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles all unexpected errors that are not explicitly caught by other exception handlers.
     *
     * @param ex the exception that occurred.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} with generic error details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorConstants.INTERNAL_SERVER_ERROR,
                messageService.getMessage(ERROR_INTERNAL),
                List.of(ex.getMessage())
        );

        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Builds an {@link ErrorResponse} with the provided parameters.
     *
     * @param status  the HTTP status code.
     * @param code    the error code.
     * @param message the error message.
     * @return the constructed {@link ErrorResponse}.
     */
    private ErrorResponse buildErrorResponse(HttpStatus status, String code, String message) {
        return new ErrorResponse()
                .status(status.value())
                .code(code)
                .message(message);
    }

    /**
     * Builds an {@link ErrorResponse} with additional details.
     *
     * @param status  the HTTP status code.
     * @param code    the error code.
     * @param message the error message.
     * @param details additional details about the error.
     * @return the constructed {@link ErrorResponse}.
     */
    private ErrorResponse buildErrorResponse(HttpStatus status, String code, String message, List<String> details) {
        return new ErrorResponse()
                .status(status.value())
                .code(code)
                .message(message)
                .details(details);
    }
}