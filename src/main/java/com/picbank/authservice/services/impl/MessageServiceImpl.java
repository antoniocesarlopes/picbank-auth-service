package com.picbank.authservice.services.impl;

import com.picbank.authservice.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageSource messageSource;

    /**
     * Retrieves a localized message using the default locale.
     *
     * @param key  The message key.
     * @param args Optional arguments for message formatting.
     * @return The localized message.
     */
    @Override
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }
}

