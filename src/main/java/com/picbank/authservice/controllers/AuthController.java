package com.picbank.authservice.controllers;

import com.picbank.authservice.api.AuthApi;
import com.picbank.authservice.model.AuthResponse;
import com.picbank.authservice.model.ConfirmEmailRequest;
import com.picbank.authservice.model.LoginRequest;
import com.picbank.authservice.model.RegisterRequest;
import com.picbank.authservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        var response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> register(RegisterRequest registerRequest) {
        var status = authService.register(registerRequest);
        return ResponseEntity.status(status).build();
    }

    @Override
    public ResponseEntity<Void> confirmEmail(ConfirmEmailRequest confirmEmailRequest) {
        authService.confirmEmail(confirmEmailRequest);
        return ResponseEntity.ok().build();
    }
}

