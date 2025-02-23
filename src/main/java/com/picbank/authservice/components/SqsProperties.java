package com.picbank.authservice.components;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for AWS SQS message queue integration.
 * <p>
 * This component stores essential configurations for interacting with AWS Simple Queue Service (SQS),
 * including queue URLs and processing settings.
 * </p>
 */
@Component
@Getter
public class SqsProperties {

    private final String queueUrl;
    private final String dlqUrl;
    private final long fixedRateMs;
    private final int maxMessages;
    private final int waitTimeSeconds;

    /**
     * Constructs a new instance of {@code SqsProperties} with values loaded from the application properties.
     *
     * @param queueUrl        The URL of the primary SQS queue.
     * @param dlqUrl          The URL of the Dead Letter Queue (DLQ) for failed messages.
     * @param fixedRateMs     The interval (in milliseconds) between message polling executions.
     * @param maxMessages     The maximum number of messages to retrieve in a single request.
     * @param waitTimeSeconds The amount of time (in seconds) to wait for messages before returning.
     */
    public SqsProperties(
            @Value("${aws.sqs.queue-url}") String queueUrl,
            @Value("${aws.sqs.dlq-url}") String dlqUrl,
            @Value("${aws.sqs.fixed-rate-ms}") long fixedRateMs,
            @Value("${aws.sqs.max-messages}") int maxMessages,
            @Value("${aws.sqs.wait-time-seconds}") int waitTimeSeconds) {
        this.queueUrl = queueUrl;
        this.dlqUrl = dlqUrl;
        this.fixedRateMs = fixedRateMs;
        this.maxMessages = maxMessages;
        this.waitTimeSeconds = waitTimeSeconds;
    }
}
