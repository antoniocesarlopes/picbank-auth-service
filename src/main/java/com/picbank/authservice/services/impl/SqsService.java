package com.picbank.authservice.services.impl;

import com.picbank.authservice.services.MessageService;
import com.picbank.authservice.services.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import static com.picbank.authservice.constants.MessageConstants.*;

/**
 * Service for interacting with AWS SQS.
 */
@Service
@Slf4j
public class SqsService implements QueueService {

    public static final String MESSAGE_BODY_FORMAT = "{\"email\": \"%s\", \"group\": \"%s\"}";
    private final SqsClient sqsClient;
    private final MessageService messageService;
    private final String queueUrl;

    /**
     * Constructs a new SqsService.
     *
     * @param sqsClient the AWS SQS client used to send messages
     * @param messageService the service used to handle message logging
     * @param queueUrl the URL of the SQS queue
     */
    public SqsService(SqsClient sqsClient,
                      MessageService messageService,
                      @Value("${aws.sqs.queue-url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.messageService = messageService;
        this.queueUrl = queueUrl;
    }

    /**
     * Sends a message to the configured SQS queue containing the user's email and group.
     *
     * @param email the user's email
     * @param group the user group to be assigned
     */
    @Override
    public void sendMessage(String email, String group) {
        String messageBody = String.format(MESSAGE_BODY_FORMAT, email, group);

        try {
            log.info(messageService.getMessage(SQS_SEND_START, email, group));

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMessageRequest);

            log.info(messageService.getMessage(SQS_SEND_SUCCESS, email, group));
        } catch (SqsException e) {
            String errorMessage = messageService.getMessage(SQS_SEND_ERROR, email, group, e.awsErrorDetails().errorMessage());
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

}
