package com.picbank.authservice.controllers;

import com.picbank.authservice.model.AuthResponse;
import com.picbank.authservice.model.ConfirmEmailRequest;
import com.picbank.authservice.model.LoginRequest;
import com.picbank.authservice.model.RegisterRequest;
import com.picbank.authservice.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;
    private ConfirmEmailRequest confirmEmailRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("user@example.com");
        loginRequest.setPassword("password");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("user@example.com");
        registerRequest.setPassword("password");
        registerRequest.setName("User Name");
        registerRequest.setIsMerchant(false);

        authResponse = new AuthResponse();
        authResponse.setAccessToken("token123");
        authResponse.setExpiresIn(3600);
        authResponse.setTokenType("Bearer");

        confirmEmailRequest = new ConfirmEmailRequest();
        confirmEmailRequest.setEmail("user@example.com");
        confirmEmailRequest.setConfirmationCode("confirmCode123");
    }

    @Test
    void shouldReturnAuthResponseWhenLoginSuccess() {
        // Arrange
        when(authService.login(loginRequest)).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void shouldReturnStatusCreatedWhenRegisterSuccess() {
        // Arrange
        when(authService.register(registerRequest)).thenReturn(HttpStatus.CREATED);

        // Act
        ResponseEntity<Void> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void shouldReturnBadRequestWhenRegisterFails() {
        // Arrange
        when(authService.register(registerRequest)).thenReturn(HttpStatus.BAD_REQUEST);

        // Act
        ResponseEntity<Void> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void shouldReturnOkWhenConfirmEmailSuccess() {

        // Act
        var response = authController.confirmEmail(confirmEmailRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService, times(1)).confirmEmail(confirmEmailRequest);
    }
}