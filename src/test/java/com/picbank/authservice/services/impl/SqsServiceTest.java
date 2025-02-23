package com.picbank.authservice.services.impl;

import com.picbank.authservice.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import static com.picbank.authservice.constants.MessageConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private SqsService sqsService;

    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/my-queue";
    private final String email = "test@example.com";
    private final String group = "test-group";

    @BeforeEach
    void setUp() {
        sqsService = new SqsService(sqsClient, messageService, queueUrl);
    }

    @Test
    void shouldSendMessageSuccessfully() {
        // Arrange
        String messageBody = String.format(SqsService.MESSAGE_BODY_FORMAT, email, group);
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();

        // Act
        sqsService.sendMessage(email, group);

        // Assert
        verify(sqsClient, times(1)).sendMessage(any(SendMessageRequest.class));
        verify(messageService, times(1)).getMessage(SQS_SEND_START, email, group);
        verify(messageService, times(1)).getMessage(SQS_SEND_SUCCESS, email, group);
    }

    @Test
    void shouldHandleSqsException() {
        // Arrange
        String messageBody = String.format(SqsService.MESSAGE_BODY_FORMAT, email, group);
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();

        SqsException sqsException = mock(SqsException.class);
        AwsErrorDetails awsErrorDetails = mock(AwsErrorDetails.class);
        when(awsErrorDetails.errorMessage()).thenReturn("Test error message");
        when(sqsException.awsErrorDetails()).thenReturn(awsErrorDetails);

        doThrow(sqsException).when(sqsClient).sendMessage(any(SendMessageRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> sqsService.sendMessage(email, group));
        verify(sqsClient, times(1)).sendMessage(any(SendMessageRequest.class));
        verify(messageService, times(1)).getMessage(SQS_SEND_START, email, group);
        verify(messageService, times(1)).getMessage(SQS_SEND_ERROR, email, group, "Test error message");
        verify(messageService, never()).getMessage(SQS_SEND_SUCCESS, email, group);
    }
}