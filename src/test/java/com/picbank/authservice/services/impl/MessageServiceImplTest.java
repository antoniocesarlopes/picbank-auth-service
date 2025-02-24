package com.picbank.authservice.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageServiceImpl messageService;


    @Test
    void shouldReturnLocalizedMessage() {
        when(messageSource.getMessage("test.key", new Object[]{"arg1"}, Locale.getDefault()))
                .thenReturn("Test message with arg1");

        String message = messageService.getMessage("test.key", "arg1");
        assertEquals("Test message with arg1", message);
    }

    @Test
    void shouldReturnMessageWithoutArguments() {
        when(messageSource.getMessage("simple.key", new Object[]{}, Locale.getDefault()))
                .thenReturn("Simple message");

        String message = messageService.getMessage("simple.key");
        assertEquals("Simple message", message);
    }
}
