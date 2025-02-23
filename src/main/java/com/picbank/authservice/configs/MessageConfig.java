package com.picbank.authservice.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Configuration for message localization and internationalization (i18n).
 * <p>
 * This class sets up a {@link MessageSource} to load localized messages from
 * resource bundles, supporting multiple languages and UTF-8 encoding.
 * </p>
 */
@Configuration
public class MessageConfig {

    /**
     * Configures the {@link MessageSource} bean to load messages from the i18n/messages files.
     * <p>
     * The messages are stored in `classpath:i18n/messages` and support different locales.
     * UTF-8 encoding is enforced to ensure proper character representation.
     * </p>
     *
     * @return A configured {@link MessageSource} instance.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
