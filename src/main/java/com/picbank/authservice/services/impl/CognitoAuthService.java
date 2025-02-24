package com.picbank.authservice.services.impl;

import com.picbank.authservice.components.CognitoProperties;
import com.picbank.authservice.exceptions.AuthException;
import com.picbank.authservice.exceptions.CognitoOperationException;
import com.picbank.authservice.model.AuthResponse;
import com.picbank.authservice.model.LoginRequest;
import com.picbank.authservice.model.RegisterRequest;
import com.picbank.authservice.model.enums.CognitoUserGroup;
import com.picbank.authservice.services.AuthService;
import com.picbank.authservice.services.MessageService;
import com.picbank.authservice.services.QueueService;
import com.picbank.authservice.utils.CognitoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

import static com.picbank.authservice.constants.AuthConstants.*;
import static com.picbank.authservice.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoAuthService implements AuthService {

    private final QueueService queueService;
    private final CognitoProperties cognitoProperties;
    private final CognitoIdentityProviderClient cognitoClient;
    private final CognitoUtils cognitoUtils;
    private final MessageService messageService;

    /**
     * Builds the authentication response based on the Cognito response.
     *
     * @param response Cognito authentication response.
     * @return AuthResponse containing authentication details.
     */
    private AuthResponse getAuthResponse(InitiateAuthResponse response) {
        var authResult = response.authenticationResult();
        log.info(messageService.getMessage(AUTH_SUCCESS_TOKEN, authResult.expiresIn()));

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(authResult.accessToken());
        authResponse.setExpiresIn(authResult.expiresIn());
        authResponse.setTokenType(authResult.tokenType());
        authResponse.setRefreshToken(authResult.refreshToken());
        authResponse.setIdToken(authResult.idToken());
        return authResponse;
    }

    /**
     * Builds a SignUpRequest for Cognito user registration.
     *
     * @param registerRequest Request containing user details.
     * @param secretHash Secret hash for client authentication.
     * @return SignUpRequest object to be sent to Cognito.
     */
    private SignUpRequest buildSignUpRequest(RegisterRequest registerRequest, String secretHash) {
        return SignUpRequest.builder()
                .secretHash(secretHash)
                .clientId(cognitoProperties.getClientId())
                .username(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .userAttributes(
                        AttributeType.builder().name("email").value(registerRequest.getEmail()).build(),
                        AttributeType.builder().name("name").value(registerRequest.getName()).build(),
                        AttributeType.builder().name("custom:document").value(registerRequest.getDocument()).build()
                )
                .build();
    }

    /**
     * Initiates authentication with Cognito.
     *
     * @param request Login request containing user credentials.
     * @param secretHash Secret hash for authentication.
     * @return Cognito authentication response.
     */
    private InitiateAuthResponse getInitiateAuthResponse(LoginRequest request, String secretHash) {
        return cognitoClient.initiateAuth(InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(Map.of(
                        USERNAME, request.getUsername(),
                        PASSWORD, request.getPassword(),
                        SECRET_HASH, secretHash
                ))
                .clientId(cognitoProperties.getClientId())
                .build());
    }

    /**
     * Authenticates a user using AWS Cognito.
     *
     * @param request LoginRequest containing user credentials.
     * @return AuthResponse containing authentication details.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info(messageService.getMessage(AUTH_LOGIN_START, request.getUsername()));

        var secretHash = cognitoUtils.calculateSecretHash(
                cognitoProperties.getClientId(),
                cognitoProperties.getClientSecret(),
                request.getUsername()
        );

        try {
            var response = getInitiateAuthResponse(request, secretHash);
            return getAuthResponse(response);

        } catch (CognitoIdentityProviderException e) {
            log.error(messageService.getMessage(AUTH_ERROR_COGNITO, request.getUsername(), e.awsErrorDetails().errorMessage()), e);
            throw new AuthException(messageService.getMessage(AUTH_ERROR_INTERNAL));
        } catch (Exception e) {
            log.error(messageService.getMessage(AUTH_ERROR_UNEXPECTED, request.getUsername(), e.getMessage()), e);
            throw new AuthException(messageService.getMessage(AUTH_ERROR_UNEXPECTED));
        }
    }

    /**
     * Registers a new user in AWS Cognito.
     *
     * @param registerRequest Request containing user registration details.
     * @return HTTP Status indicating success or failure.
     */
    @Override
    public HttpStatus register(RegisterRequest registerRequest) {
        log.info(messageService.getMessage(AUTH_REGISTER_START, registerRequest.getEmail()));

        var secretHash = cognitoUtils.calculateSecretHash(
                cognitoProperties.getClientId(),
                cognitoProperties.getClientSecret(),
                registerRequest.getEmail()
        );

        var signUpRequest = buildSignUpRequest(registerRequest, secretHash);

        try {
            var response = cognitoClient.signUp(signUpRequest);

            if (response.sdkHttpResponse().isSuccessful()) {
                var group = registerRequest.getIsMerchant() ? CognitoUserGroup.MERCHANT : CognitoUserGroup.STANDARD;

                log.info(messageService.getMessage(AUTH_REGISTER_SUCCESS, registerRequest.getEmail(), group));
                queueService.sendMessage(registerRequest.getEmail(), group.name());

                return HttpStatus.CREATED;
            }

            log.warn(messageService.getMessage(AUTH_REGISTER_FAILURE, registerRequest.getEmail()));
            return HttpStatus.BAD_REQUEST;

        } catch (CognitoIdentityProviderException e) {
            String errorMessage = messageService.getMessage(AUTH_ERROR_COGNITO, registerRequest.getEmail(), e.awsErrorDetails().errorMessage());
            log.error(errorMessage, e);
            throw new CognitoOperationException(errorMessage, e);

        } catch (SdkClientException e) {
            String errorMessage = messageService.getMessage(AUTH_ERROR_INTERNAL, registerRequest.getEmail(), e.getMessage());
            log.error(errorMessage, e);
            throw new CognitoOperationException(errorMessage, e);

        } catch (Exception e) {
            String errorMessage = messageService.getMessage(AUTH_ERROR_UNEXPECTED, registerRequest.getEmail(), e.getMessage());
            log.error(errorMessage, e);
            throw new CognitoOperationException(errorMessage, e);
        }

    }

}

