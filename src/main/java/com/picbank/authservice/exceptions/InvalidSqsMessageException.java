package com.picbank.authservice.exceptions;

/**
 * Exception thrown when an SQS message contains invalid or missing fields.
 * <p>
 * This exception is used to handle cases where the incoming SQS message
 * does not conform to the expected structure.
 * </p>
 */
public class InvalidSqsMessageException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidSqsMessageException} with the specified detail message.
     *
     * @param message the detail message explaining the issue.
     */
    public InvalidSqsMessageException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code InvalidSqsMessageException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the issue.
     * @param cause   the underlying cause of the exception.
     */
    public InvalidSqsMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
