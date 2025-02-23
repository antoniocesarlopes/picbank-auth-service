package com.picbank.authservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Configuration for the AWS SQS (Simple Queue Service) client.
 * <p>
 * This class provides a {@link SqsClient} bean to interact with AWS SQS queues,
 * using credentials from environment variables.
 * </p>
 */
@Configuration
public class SqsConfig {

    /**
     * Creates and configures the AWS SQS client.
     * <p>
     * The client is configured to use credentials from environment variables
     * and connects to the specified AWS region.
     * </p>
     *
     * @return A configured {@link SqsClient} instance.
     */
    @Bean
    public SqsClient sqsClient(@Value("${aws.access-key-id}") String accessKey,
                               @Value("${aws.secret-access-key}") String secretKey,
                               @Value("${aws.region}") String region) {
        return SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

}
