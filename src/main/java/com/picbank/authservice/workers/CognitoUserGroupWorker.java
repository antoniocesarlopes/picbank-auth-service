package com.picbank.authservice.workers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picbank.authservice.components.SqsProperties;
import com.picbank.authservice.exceptions.InvalidSqsMessageException;
import com.picbank.authservice.model.dtos.CognitoUserGroupMessage;
import com.picbank.authservice.model.enums.CognitoUserGroup;
import com.picbank.authservice.services.EmailService;
import com.picbank.authservice.services.MessageService;
import com.picbank.authservice.services.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

import static com.picbank.authservice.constants.MessageConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoUserGroupWorker {

    private final UserGroupService userGroupService;
    private final SqsProperties sqsProperties;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final SqsClient sqsClient;
    private final EmailService emailService;

    /**
     * Periodically consumes messages from the SQS queue based on the configured interval.
     * If a message fails to process, it is moved to the Dead Letter Queue (DLQ).
     */
    @Scheduled(fixedRateString = "#{sqsProperties.fixedRateMs}")
    public void consumeMessages() {
        log.info(messageService.getMessage(WORKER_SQS_CHECKING, sqsProperties.getQueueUrl()));

        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(sqsProperties.getQueueUrl())
                    .maxNumberOfMessages(sqsProperties.getMaxMessages())
                    .waitTimeSeconds(sqsProperties.getWaitTimeSeconds())
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            log.debug(messageService.getMessage(WORKER_SQS_RETRIEVED, messages.size()));

            for (Message message : messages) {
                processMessageSafely(message);
            }
        } catch (SqsException e) {
            log.error(messageService.getMessage(WORKER_SQS_ERROR_CONSUMING), e);
        }
    }

    /**
     * Handles message processing safely, ensuring errors are logged and failed messages go to the DLQ.
     *
     * @param message The SQS message to be processed.
     */
    private void processMessageSafely(Message message) {
        try {
            log.info(messageService.getMessage(WORKER_SQS_PROCESSING, message.body()));
            processMessage(message.body());

            deleteMessage(message.receiptHandle());

        } catch (InvalidSqsMessageException e) {
            log.error(messageService.getMessage(WORKER_SQS_INVALID_MESSAGE, message.body()), e);
            sendToDlq(message.body());
            deleteMessage(message.receiptHandle());
        } catch (Exception e) {
            log.error(messageService.getMessage(WORKER_SQS_ERROR_PROCESSING, message.body()), e);
            sendToDlq(message.body());
            deleteMessage(message.receiptHandle());
        }
    }

    /**
     * Processes an SQS message by adding the user to a Cognito group.
     *
     * @param messageBody JSON string containing "email" and "group".
     * @throws JsonProcessingException If message parsing fails.
     * @throws InvalidSqsMessageException If the message contains invalid or missing fields.
     */
    private void processMessage(String messageBody) throws JsonProcessingException {
        log.debug(messageService.getMessage(WORKER_SQS_PROCESSING, messageBody));

        CognitoUserGroupMessage payload = objectMapper.readValue(messageBody, CognitoUserGroupMessage.class);
        validatePayload(payload, messageBody);

        try {
            CognitoUserGroup userGroup = CognitoUserGroup.valueOf(payload.group().toUpperCase());
            userGroupService.addUserToGroup(userGroup, payload.email());

            sendEmail(payload, userGroup);

            log.info(messageService.getMessage(WORKER_SQS_PROCESSED_SUCCESS, payload.email(), userGroup));

        } catch (IllegalArgumentException e) {
            log.error(messageService.getMessage(WORKER_SQS_INVALID_GROUP, messageBody, e.getMessage()));
            throw new InvalidSqsMessageException(
                    messageService.getMessage(WORKER_SQS_INVALID_GROUP, payload.group()), e);
        }
    }

    /**
     *
     * @param payload
     * @param userGroup
     */
    private void sendEmail(CognitoUserGroupMessage payload, CognitoUserGroup userGroup) {
        try {
            emailService.sendEmail(
                    payload.email(),
                    messageService.getMessage(EMAIL_SUBJECT_USER_ACCOUNT_READY),
                    messageService.getMessage(EMAIL_BODY_USER_ACCOUNT_READY, payload.email(), userGroup)
            );
        } catch (Exception e) {
            log.error(messageService.getMessage(EMAIL_SENT_FAILURE, payload.email()), e);
        }
    }

    /**
     * Validates the required fields in the SQS message payload.
     *
     * @param payload    The parsed message object.
     * @param rawMessage The original message body for logging purposes.
     * @throws InvalidSqsMessageException If required fields are missing.
     */
    private void validatePayload(CognitoUserGroupMessage payload, String rawMessage) {
        if (payload.email() == null || payload.group() == null) {
            log.error(messageService.getMessage(WORKER_SQS_INVALID_FIELDS, rawMessage));
            throw new InvalidSqsMessageException(messageService.getMessage(WORKER_SQS_INVALID_FIELDS, rawMessage));
        }
    }

    /**
     * Deletes a message from the SQS queue after successful processing.
     *
     * @param receiptHandle The message receipt handle.
     */
    private void deleteMessage(String receiptHandle) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(sqsProperties.getQueueUrl())
                .receiptHandle(receiptHandle)
                .build();
        sqsClient.deleteMessage(deleteRequest);
        log.debug(messageService.getMessage(WORKER_SQS_DELETED));
    }

    /**
     * Sends failed messages to the Dead Letter Queue (DLQ).
     *
     * @param messageBody The message content.
     */
    private void sendToDlq(String messageBody) {
        try {
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(sqsProperties.getDlqUrl())
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMessageRequest);
            log.warn(messageService.getMessage(WORKER_SQS_SENT_DLQ, messageBody));

        } catch (SqsException e) {
            log.error(messageService.getMessage(WORKER_SQS_ERROR_DLQ, messageBody), e);
        }
    }
}
