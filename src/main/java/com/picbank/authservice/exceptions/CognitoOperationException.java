package com.picbank.authservice.exceptions;

/**
 * Exception thrown when an error occurs during an AWS Cognito operation.
 * <p>
 * This exception is used to wrap errors related to Cognito user management, authentication,
 * and other related operations.
 * </p>
 */
public class CognitoOperationException extends RuntimeException {

    /**
     * Constructs a new {@code CognitoOperationException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the error.
     * @param cause   the underlying cause of the exception.
     */
    public CognitoOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
