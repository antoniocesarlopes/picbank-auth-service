package com.picbank.authservice.services.impl;

import com.picbank.authservice.components.CognitoProperties;
import com.picbank.authservice.exceptions.AuthException;
import com.picbank.authservice.exceptions.CognitoOperationException;
import com.picbank.authservice.model.AuthResponse;
import com.picbank.authservice.model.LoginRequest;
import com.picbank.authservice.model.RegisterRequest;
import com.picbank.authservice.model.enums.CognitoUserGroup;
import com.picbank.authservice.services.MessageService;
import com.picbank.authservice.services.QueueService;
import com.picbank.authservice.utils.CognitoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import static com.picbank.authservice.constants.MessageConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CognitoAuthServiceTest {

    @Mock
    private QueueService queueService;

    @Mock
    private CognitoProperties cognitoProperties;

    @Mock
    private CognitoIdentityProviderClient cognitoClient;

    @Mock
    private MessageService messageService;

    @Mock
    private CognitoUtils cognitoUtils;

    @InjectMocks
    private CognitoAuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password123");
        registerRequest = new RegisterRequest("Test User", "password123", "test@example.com", "12345678900", true);
        when(cognitoProperties.getClientId()).thenReturn("test-client-id");
        when(cognitoProperties.getClientSecret()).thenReturn("test-client-secret");
        when(cognitoUtils.calculateSecretHash(anyString(), anyString(), anyString())).thenReturn("mockedHash");
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        InitiateAuthResponse authResponse = mock(InitiateAuthResponse.class);
        when(authResponse.authenticationResult()).thenReturn(AuthenticationResultType.builder()
                .accessToken("access-token")
                .expiresIn(3600)
                .tokenType("Bearer")
                .refreshToken("refresh-token")
                .idToken("id-token")
                .build());

        when(cognitoClient.initiateAuth(any(InitiateAuthRequest.class))).thenReturn(authResponse);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals(3600, response.getExpiresIn());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("id-token", response.getIdToken());

        verify(cognitoClient, times(1)).initiateAuth(any(InitiateAuthRequest.class));
    }

    @Test
    void shouldThrowAuthExceptionWhenAuthenticationFails() {
        CognitoIdentityProviderException cognitoException = mock(CognitoIdentityProviderException.class);
        when(cognitoException.awsErrorDetails()).thenReturn(mock(AwsErrorDetails.class));

        when(cognitoClient.initiateAuth(any(InitiateAuthRequest.class))).thenThrow(cognitoException);
        when(messageService.getMessage(AUTH_ERROR_COGNITO, loginRequest.getUsername(), cognitoException.awsErrorDetails().errorMessage())).thenReturn("Mocked Message AUTH_ERROR_COGNITO");
        when(messageService.getMessage(AUTH_LOGIN_START, loginRequest.getUsername())).thenReturn("Mocked Message AUTH_LOGIN_START");
        when(messageService.getMessage(AUTH_ERROR_INTERNAL)).thenReturn("Mocked Message AUTH_ERROR_INTERNAL");

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Mocked Message AUTH_ERROR_INTERNAL", exception.getMessage());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        SdkHttpResponse sdkHttpResponse = mock(SdkHttpResponse.class);
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);

        SignUpResponse signUpResponse = mock(SignUpResponse.class);
        when(signUpResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);

        when(cognitoClient.signUp(any(SignUpRequest.class))).thenReturn(signUpResponse);

        var response = authService.register(registerRequest);

        assertEquals(201, response.value());

        verify(cognitoClient, times(1)).signUp(any(SignUpRequest.class));
        verify(queueService, times(1)).sendMessage("test@example.com", CognitoUserGroup.MERCHANT.name());
    }

    @Test
    void shouldReturnBadRequestWhenRegistrationFails() {
        SdkHttpResponse sdkHttpResponse = mock(SdkHttpResponse.class);
        when(sdkHttpResponse.isSuccessful()).thenReturn(false); // Simulando falha

        SignUpResponse signUpResponse = mock(SignUpResponse.class);
        when(signUpResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);

        when(cognitoClient.signUp(any(SignUpRequest.class))).thenReturn(signUpResponse);

        var response = authService.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.value());

        verify(cognitoClient, times(1)).signUp(any(SignUpRequest.class));
        verify(queueService, never()).sendMessage(any(), any());
    }

    @Test
    void shouldThrowCognitoOperationExceptionWhenSignUpFails() {
        CognitoIdentityProviderException cognitoException = mock(CognitoIdentityProviderException.class);
        when(cognitoException.awsErrorDetails()).thenReturn(mock(AwsErrorDetails.class));

        when(messageService.getMessage(AUTH_REGISTER_START, registerRequest.getEmail())).thenReturn("Mocked Message AUTH_REGISTER_START");
        when(messageService.getMessage(AUTH_ERROR_COGNITO, registerRequest.getEmail(), cognitoException.awsErrorDetails().errorMessage())).thenReturn("Mocked Message AUTH_ERROR_COGNITO");

        when(cognitoClient.signUp(any(SignUpRequest.class))).thenThrow(cognitoException);

        CognitoOperationException exception = assertThrows(CognitoOperationException.class, () -> {
            authService.register(registerRequest);
        });

        assertTrue(exception.getMessage().contains("Mocked Message AUTH_ERROR_COGNITO"));
    }

}
