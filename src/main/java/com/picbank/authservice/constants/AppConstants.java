package com.picbank.authservice.constants;

public interface AppConstants {

    String LOCALE_PARAMETER = "lang";

    interface Auth{
        String USERNAME = "USERNAME";
        String PASSWORD = "PASSWORD";
        String SECRET_HASH = "SECRET_HASH";
        String HASH_ALGORITHM = "HmacSHA256";
    }

}