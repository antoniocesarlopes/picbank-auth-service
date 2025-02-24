package com.picbank.authservice.services.impl;

import com.picbank.authservice.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;

import static com.picbank.authservice.constants.MessageConstants.EMAIL_SENT_FAILURE;
import static com.picbank.authservice.constants.MessageConstants.EMAIL_SENT_SUCCESS;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesServiceTest {

    @Mock
    private SesClient sesClient;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private SesService sesService;

    @BeforeEach
    void setUp() {
        String senderEmail = "test@example.com";
        sesService = new SesService(sesClient, messageService, senderEmail);
    }

    @Test
    void shouldSendEmailSuccessfully() {
        // Arrange
        String recipient = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        sesService.sendEmail(recipient, subject, body);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(messageService, times(1)).getMessage(EMAIL_SENT_SUCCESS, recipient);
    }

    @Test
    void shouldHandleSesException() {
        // Arrange
        String recipient = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(SesException.class).when(sesClient).sendEmail(any(SendEmailRequest.class));

        // Act
        sesService.sendEmail(recipient, subject, body);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(messageService, times(1)).getMessage(EMAIL_SENT_FAILURE, recipient);
    }
}
