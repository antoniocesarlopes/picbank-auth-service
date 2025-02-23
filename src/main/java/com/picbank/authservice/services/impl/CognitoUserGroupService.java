package com.picbank.authservice.services.impl;

import com.picbank.authservice.components.CognitoProperties;
import com.picbank.authservice.constants.MessageConstants;
import com.picbank.authservice.exceptions.CognitoOperationException;
import com.picbank.authservice.model.enums.CognitoUserGroup;
import com.picbank.authservice.services.MessageService;
import com.picbank.authservice.services.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

/**
 * Service responsible for managing user groups in AWS Cognito.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoUserGroupService implements UserGroupService {

    private final CognitoProperties cognitoProperties;
    private final CognitoIdentityProviderClient cognitoClient;
    private final MessageService messageService;

    /**
     * Adds a user to a Cognito group in AWS.
     * Logs relevant information and throws exceptions with localized messages in case of errors.
     *
     * @param userGroup The Cognito group to which the user should be added.
     * @param username  The username of the user to be added.
     * @throws CognitoOperationException If any error occurs while interacting with AWS Cognito.
     */
    @Override
    public void addUserToGroup(CognitoUserGroup userGroup, String username) {
        log.info(messageService.getMessage(MessageConstants.AUTH_REGISTER_START, username, userGroup.getGroupName()));

        try {
            AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest.builder()
                    .userPoolId(cognitoProperties.getUserPoolId())
                    .username(username)
                    .groupName(userGroup.getGroupName())
                    .build();

            cognitoClient.adminAddUserToGroup(request);

            log.info(messageService.getMessage(MessageConstants.AUTH_ADD_USER_GROUP_SUCCESS, username, userGroup.getGroupName()));

        } catch (CognitoIdentityProviderException e) {
            String errorMessage = messageService.getMessage(MessageConstants.AUTH_ERROR_COGNITO, username, e.awsErrorDetails().errorMessage());
            log.error(errorMessage, e);
            throw new CognitoOperationException(errorMessage, e);

        } catch (SdkClientException e) {
            String errorMessage = messageService.getMessage(MessageConstants.AUTH_ERROR_INTERNAL, username, e.getMessage());
            log.error(errorMessage, e);
            throw new CognitoOperationException(errorMessage, e);

        } catch (Exception e) {
            String errorMessage = messageService.getMessage(MessageConstants.AUTH_ERROR_UNEXPECTED, username, e.getMessage());
            log.error(errorMessage, e);
            throw new CognitoOperationException(errorMessage, e);
        }
    }
}
