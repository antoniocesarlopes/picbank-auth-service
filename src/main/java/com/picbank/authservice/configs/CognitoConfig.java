package com.picbank.authservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

/**
 * Configuration for AWS Cognito Identity Provider client.
 * <p>
 * This class provides a configured {@link CognitoIdentityProviderClient} bean
 * to interact with AWS Cognito for authentication and user management.
 * </p>
 */
@Configuration
public class CognitoConfig {

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    /**
     * Creates and configures the Cognito Identity Provider client.
     *
     * @return A fully configured {@link CognitoIdentityProviderClient} instance.
     */
    @Bean
    public CognitoIdentityProviderClient cognitoClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                        )
                )
                .build();
    }
}
