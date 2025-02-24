package com.picbank.authservice.constants;

import lombok.experimental.UtilityClass;

/**
 * Defines auth codes used throughout the application.
 */
@UtilityClass
public final class AuthConstants {
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String SECRET_HASH = "SECRET_HASH";
    public static final String HASH_ALGORITHM = "HmacSHA256";
}