package com.picbank.authservice.services.impl;

import com.picbank.authservice.components.CognitoProperties;
import com.picbank.authservice.constants.MessageConstants;
import com.picbank.authservice.exceptions.CognitoOperationException;
import com.picbank.authservice.model.enums.CognitoUserGroup;
import com.picbank.authservice.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CognitoUserGroupServiceTest {

    @Mock
    private CognitoProperties cognitoProperties;

    @Mock
    private CognitoIdentityProviderClient cognitoClient;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private CognitoUserGroupService userGroupService;

    private final String username = "testUser";
    private final CognitoUserGroup userGroup = CognitoUserGroup.MERCHANT;

    @BeforeEach
    void setUp() {
        String userPoolId = "test-pool-id";
        when(cognitoProperties.getUserPoolId()).thenReturn(userPoolId);
        when(messageService.getMessage(anyString(), any(), any())).thenReturn("Mocked Message");
    }

    @Test
    void shouldAddUserToGroupSuccessfully() {
        // Act
        assertDoesNotThrow(() -> userGroupService.addUserToGroup(userGroup, username));

        // Assert
        verify(cognitoClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
    }

    @Test
    void shouldThrowCognitoOperationExceptionWhenCognitoErrorOccurs() {
        CognitoIdentityProviderException cognitoException = mock(CognitoIdentityProviderException.class);
        when(cognitoException.awsErrorDetails()).thenReturn(mock(AwsErrorDetails.class));
        doThrow(cognitoException).when(cognitoClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));

        // Act & Assert
        CognitoOperationException thrown = assertThrows(CognitoOperationException.class, () ->
                userGroupService.addUserToGroup(userGroup, username));

        assertNotNull(thrown);
        verify(messageService).getMessage(eq(MessageConstants.AUTH_ERROR_COGNITO), eq(username), any());
    }

    @Test
    void shouldThrowCognitoOperationExceptionWhenSdkClientExceptionOccurs() {
        SdkClientException exception = mock(SdkClientException.class);
        doThrow(exception).when(cognitoClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));

        // Act & Assert
        CognitoOperationException thrown = assertThrows(CognitoOperationException.class, () ->
                userGroupService.addUserToGroup(userGroup, username));

        assertNotNull(thrown);
        verify(messageService).getMessage(eq(MessageConstants.AUTH_ERROR_INTERNAL), eq(username), any());
    }

    @Test
    void shouldThrowCognitoOperationExceptionWhenUnexpectedExceptionOccurs() {
        RuntimeException exception = new RuntimeException("Unexpected error");
        doThrow(exception).when(cognitoClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));

        // Act & Assert
        CognitoOperationException thrown = assertThrows(CognitoOperationException.class, () ->
                userGroupService.addUserToGroup(userGroup, username));

        assertNotNull(thrown);
        verify(messageService).getMessage(eq(MessageConstants.AUTH_ERROR_UNEXPECTED), eq(username), any());
    }
}
