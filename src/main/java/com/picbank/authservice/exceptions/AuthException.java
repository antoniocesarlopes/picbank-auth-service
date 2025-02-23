package com.picbank.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when authentication or authorization fails.
 * <p>
 * This exception is mapped to an HTTP 401 Unauthorized status.
 * </p>
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthException extends RuntimeException {

    /**
     * Constructs a new {@code AuthException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public AuthException(String message) {
        super(message);
    }
}
