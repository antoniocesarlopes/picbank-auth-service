package com.picbank.authservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

/**
 * Configuration class for AWS Simple Email Service (SES).
 */
@Configuration
public class SesConfig {

    /**
     * Creates an SES client bean for sending emails.
     *
     * @return the SES client
     */
    @Bean
    public SesClient sesClient(@Value("${aws.access-key-id}") String accessKey,
                               @Value("${aws.secret-access-key}") String secretKey,
                               @Value("${aws.region}") String region) {
        return SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
