package com.picbank.authservice.components;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for AWS Cognito authentication.
 * <p>
 * This component holds essential credentials and identifiers needed to integrate with AWS Cognito.
 * </p>
 */
@Component
@Getter
public class CognitoProperties {

    private final String userPoolId;
    private final String clientId;
    private final String clientSecret;

    /**
     * Constructs a new instance of {@code CognitoProperties} with values loaded from the application properties.
     *
     * @param userPoolId  The unique identifier of the AWS Cognito User Pool.
     * @param clientId    The client ID used for authentication.
     * @param clientSecret The client secret used for authentication.
     */
    public CognitoProperties(@Value("${aws.cognito.userPoolId}") String userPoolId,
                             @Value("${spring.security.oauth2.client.registration.cognito.client-id}") String clientId,
                             @Value("${spring.security.oauth2.client.registration.cognito.client-secret}") String clientSecret) {
        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
