package com.picbank.authservice.utils;

import com.picbank.authservice.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CognitoUtilsTest {

    private static final String CLIENT_ID = "testClientId";
    private static final String CLIENT_SECRET = "testClientSecret";
    private static final String USERNAME = "testUser";
    private static final String HASH_ALGORITHM = "HmacSHA256";
    private static final String ALGORITHM_ERROR = "Algorithm error";
    private static final String ALGORITHM_ERROR_MSG_FORMAT = "Error while calculating secret hash for user %s: %s";
    public static final String MOCKED_MESSAGE = "Mocked message";

    @Mock
    private MessageService messageService;

    @InjectMocks
    private CognitoUtils cognitoUtils;

    @BeforeEach
    void setUp() {
        when(messageService.getMessage(anyString(), any())).thenReturn(MOCKED_MESSAGE);
    }

    @Test
    void shouldCalculateSecretHashSuccessfully() {
        String expectedHash = calculateExpectedHash();

        String result = cognitoUtils.calculateSecretHash(CLIENT_ID, CLIENT_SECRET, USERNAME);

        assertNotNull(result);
        assertEquals(expectedHash, result);
        verify(messageService, times(5)).getMessage(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenMacAlgorithmFails() {
        try (var macMock = mockStatic(Mac.class)) {
            macMock.when(() -> Mac.getInstance(HASH_ALGORITHM)).thenThrow(new RuntimeException(ALGORITHM_ERROR));

            when(messageService.getMessage(any(), any(), any())).thenReturn(String.format(ALGORITHM_ERROR_MSG_FORMAT, USERNAME, ALGORITHM_ERROR));

            Exception exception = assertThrows(IllegalStateException.class, () ->
                    cognitoUtils.calculateSecretHash(CLIENT_ID, CLIENT_SECRET, USERNAME)
            );

            assertEquals(exception.getMessage(), String.format(ALGORITHM_ERROR_MSG_FORMAT, USERNAME, ALGORITHM_ERROR));
            verify(messageService, times(1)).getMessage(any(), any(), any());
        }
    }

    /**
     * Helper method to calculate the expected hash using the same algorithm as the real class.
     */
    private String calculateExpectedHash() {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(CLIENT_SECRET.getBytes(StandardCharsets.UTF_8), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(signingKey);
            mac.update(USERNAME.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(CLIENT_ID.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(String.format(ALGORITHM_ERROR_MSG_FORMAT, USERNAME, ALGORITHM_ERROR), e);
        }
    }
}
