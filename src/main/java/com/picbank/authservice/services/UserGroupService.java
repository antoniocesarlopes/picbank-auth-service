package com.picbank.authservice.services;

import com.picbank.authservice.model.enums.CognitoUserGroup;

public interface UserGroupService {
    void addUserToGroup(CognitoUserGroup userGroup, String username);
}
