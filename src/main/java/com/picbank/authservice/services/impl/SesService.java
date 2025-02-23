package com.picbank.authservice.services.impl;

import com.picbank.authservice.services.EmailService;
import com.picbank.authservice.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import static com.picbank.authservice.constants.MessageConstants.EMAIL_SENT_FAILURE;
import static com.picbank.authservice.constants.MessageConstants.EMAIL_SENT_SUCCESS;

/**
 * Service responsible for sending emails using AWS SES.
 */
@Service
@Slf4j
public class SesService implements EmailService {

    private final SesClient sesClient;
    private final MessageService messageService;
    private final String senderEmail;

    /**
     * Constructs a new SesService.
     *
     * @param sesClient the AWS SES client used to send emails
     * @param messageService the service used to handle email messages and log messages
     * @param senderEmail the email address used as the sender
     */
    public SesService(SesClient sesClient,
                      MessageService messageService,
                      @Value("${aws.ses.sender-email}") String senderEmail) {
        this.sesClient = sesClient;
        this.messageService = messageService;
        this.senderEmail = senderEmail;
    }

    /**
     * Sends an email notification to a user.
     *
     * @param recipient The recipient's email address.
     * @param subject   The email subject.
     * @param body      The email body.
     */
    @Override
    public void sendEmail(String recipient, String subject, String body) {
        try {
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(Destination.builder().toAddresses(recipient).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).build())
                            .body(Body.builder().text(Content.builder().data(body).build()).build())
                            .build())
                    .source(senderEmail)
                    .build();

            sesClient.sendEmail(emailRequest);
            log.info(messageService.getMessage(EMAIL_SENT_SUCCESS, recipient));

        } catch (SesException e) {
            log.error(messageService.getMessage(EMAIL_SENT_FAILURE, recipient), e);
        }
    }
}
