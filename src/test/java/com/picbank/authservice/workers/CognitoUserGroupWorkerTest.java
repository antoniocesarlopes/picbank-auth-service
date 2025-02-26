package com.picbank.authservice.workers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picbank.authservice.components.SqsProperties;
import com.picbank.authservice.model.dtos.CognitoUserGroupMessage;
import com.picbank.authservice.model.enums.CognitoUserGroup;
import com.picbank.authservice.services.EmailService;
import com.picbank.authservice.services.MessageService;
import com.picbank.authservice.services.UserGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

import static com.picbank.authservice.constants.MessageConstants.WORKER_SQS_ERROR_DLQ;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CognitoUserGroupWorkerTest {

    @Mock private UserGroupService userGroupService;
    @Mock private SqsProperties sqsProperties;
    @Mock private ObjectMapper objectMapper;
    @Mock private MessageService messageService;
    @Mock private SqsClient sqsClient;
    @Mock private EmailService emailService;

    private CognitoUserGroupWorker worker;

    @BeforeEach
    void setUp() {
        worker = new CognitoUserGroupWorker(userGroupService, sqsProperties, objectMapper, messageService, sqsClient, emailService);
    }

    @Test
    void shouldProcessValidMessageSuccessfully() throws JsonProcessingException {
        String validJson = "{\"email\":\"test@example.com\", \"group\":\"Merchant\"}";
        Message message = Message.builder().body(validJson).receiptHandle("receipt123").build();

        CognitoUserGroupMessage payload = new CognitoUserGroupMessage("username", "test@example.com", CognitoUserGroup.MERCHANT.getGroupName());
        when(sqsProperties.getQueueUrl()).thenReturn("test-queue-url");
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(validJson, CognitoUserGroupMessage.class)).thenReturn(payload);
        when(messageService.getMessage(any(), any())).thenReturn("Mocked Message");

        worker.consumeMessages();

        verify(userGroupService).addUserToGroup(CognitoUserGroup.MERCHANT, "test@example.com");
        verify(emailService).sendEmail(any(), any(), any());
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleJsonProcessingException() throws JsonProcessingException {
        String invalidJson = "invalid-json";
        Message message = Message.builder().body(invalidJson).receiptHandle("receipt123").build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(invalidJson, CognitoUserGroupMessage.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        worker.consumeMessages();

        verify(sqsClient).sendMessage(any(SendMessageRequest.class)); // Enviado para DLQ
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleMissingFields() throws JsonProcessingException {
        String jsonMissingFields = "{\"email\": null, \"group\": null}";
        Message message = Message.builder().body(jsonMissingFields).receiptHandle("receipt123").build();
        CognitoUserGroupMessage payload = new CognitoUserGroupMessage(null,null, null);

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(jsonMissingFields, CognitoUserGroupMessage.class)).thenReturn(payload);

        worker.consumeMessages();

        verify(sqsClient).sendMessage(any(SendMessageRequest.class)); // Enviado para DLQ
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleInvalidGroup() throws JsonProcessingException {
        String jsonInvalidGroup = "{\"email\":\"test@example.com\", \"group\":\"InvalidGroup\"}";
        Message message = Message.builder().body(jsonInvalidGroup).receiptHandle("receipt123").build();
        CognitoUserGroupMessage payload = new CognitoUserGroupMessage("username","test@example.com", "InvalidGroup");

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(jsonInvalidGroup, CognitoUserGroupMessage.class)).thenReturn(payload);

        worker.consumeMessages();

        verify(sqsClient).sendMessage(any(SendMessageRequest.class)); // Enviado para DLQ
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleEmailSendFailure() throws JsonProcessingException {
        String validJson = "{\"email\":\"test@example.com\", \"group\":\"Merchant\"}";
        Message message = Message.builder().body(validJson).receiptHandle("receipt123").build();
        CognitoUserGroupMessage payload = new CognitoUserGroupMessage("username","test@example.com", "Merchant");

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(validJson, CognitoUserGroupMessage.class)).thenReturn(payload);
        doThrow(new RuntimeException("Email send failed")).when(emailService).sendEmail(any(), any(), any());

        worker.consumeMessages();

        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleDeleteMessageFailure() {
        String validJson = "{\"email\":\"test@example.com\", \"group\":\"Merchant\"}";
        Message message = Message.builder().body(validJson).receiptHandle("receipt123").build();
        SqsException sqsException = mock(SqsException.class);

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        doThrow(sqsException).when(sqsClient).deleteMessage(any(DeleteMessageRequest.class));

        worker.consumeMessages();

        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleSqsReceiveMessageFailure() {
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenThrow(mock(SqsException.class));

        worker.consumeMessages();

        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleGeneralProcessingError() throws JsonProcessingException {
        String validJson = "{\"email\":\"test@example.com\", \"group\":\"Merchant\"}";
        Message message = Message.builder().body(validJson).receiptHandle("receipt123").build();
        CognitoUserGroupMessage payload = new CognitoUserGroupMessage("username","test@example.com", "Merchant");

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(validJson, CognitoUserGroupMessage.class)).thenReturn(payload);
        doThrow(new RuntimeException("Unexpected error")).when(userGroupService).addUserToGroup(any(), any());

        worker.consumeMessages();

        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleMissingFieldsWhenKeysAreAbsent() throws JsonProcessingException {
        String jsonWithoutKeys = "{}";
        Message message = Message.builder().body(jsonWithoutKeys).receiptHandle("receipt123").build();

        CognitoUserGroupMessage payload = new CognitoUserGroupMessage("username", null, null);

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(jsonWithoutKeys, CognitoUserGroupMessage.class)).thenReturn(payload);

        worker.consumeMessages();

        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
        verify(sqsClient).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldHandleSqsExceptionWhenSendingToDlq() throws JsonProcessingException {
        String validJson = "{\"email\":\"test@example.com\", \"group\":\"Merchant\"}";
        Message message = Message.builder().body(validJson).receiptHandle("receipt123").build();
        CognitoUserGroupMessage payload = new CognitoUserGroupMessage("username","test@example.com", "Merchant");

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(message)).build());
        when(objectMapper.readValue(validJson, CognitoUserGroupMessage.class)).thenReturn(payload);
        doThrow(new IllegalArgumentException("Unexpected error")).when(userGroupService).addUserToGroup(any(), any());
        doThrow(SqsException.class).when(sqsClient).sendMessage(any(SendMessageRequest.class));

        worker.consumeMessages();

        verify(sqsClient, times(1)).sendMessage(any(SendMessageRequest.class));
        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
    }

}
