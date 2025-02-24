package com.picbank.authservice.exceptions;

/**
 * Exception thrown when an error occurs during an AWS SQS operation.
 * <p>
 * This exception is used to wrap errors related to SQS related operations.
 * </p>
 */
public class SqsOperationException extends RuntimeException {

    /**
     * Constructs a new {@code SqsOperationException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the error.
     * @param cause   the underlying cause of the exception.
     */
    public SqsOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
