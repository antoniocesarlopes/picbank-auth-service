package com.picbank.authservice.utils;

import com.picbank.authservice.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.picbank.authservice.constants.AppConstants.Auth.HASH_ALGORITHM;
import static com.picbank.authservice.constants.MessageConstants.*;

/**
 * Utility class for AWS Cognito operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public final class CognitoUtils {

    private final MessageService messageService;

    /**
     * Calculates the secret hash for AWS Cognito authentication.
     *
     * @param clientId     The client ID.
     * @param clientSecret The client secret.
     * @param username     The username.
     * @return The computed secret hash in Base64 encoding.
     */
    public String calculateSecretHash(String clientId, String clientSecret, String username) {
        log.debug(messageService.getMessage(COGNITO_HASH_START, username));

        try {
            SecretKeySpec signingKey = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(HASH_ALGORITHM);

            log.debug(messageService.getMessage(COGNITO_HASH_INIT, HASH_ALGORITHM));
            mac.init(signingKey);

            log.debug(messageService.getMessage(COGNITO_HASH_UPDATE, username));
            mac.update(username.getBytes(StandardCharsets.UTF_8));

            log.debug(messageService.getMessage(COGNITO_HASH_FINALIZE, clientId));
            byte[] rawHmac = mac.doFinal(clientId.getBytes(StandardCharsets.UTF_8));

            String secretHash = Base64.getEncoder().encodeToString(rawHmac);
            log.info(messageService.getMessage(COGNITO_HASH_SUCCESS, username));
            return secretHash;
        } catch (Exception e) {
            String errorMessage = messageService.getMessage(COGNITO_HASH_ERROR, username, e.getMessage());
            log.error(errorMessage, e);
            throw new IllegalStateException(errorMessage, e);
        }
    }
}
