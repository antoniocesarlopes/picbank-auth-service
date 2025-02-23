package com.picbank.authservice.model.enums;

import lombok.Getter;

@Getter
public enum CognitoUserGroup {
    MERCHANT("Merchant"),
    STANDARD("Standard");

    private final String groupName;

    CognitoUserGroup(String groupName) {
        this.groupName = groupName;
    }

}


