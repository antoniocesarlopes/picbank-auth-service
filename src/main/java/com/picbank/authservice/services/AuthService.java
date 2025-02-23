package com.picbank.authservice.services;

import com.picbank.authservice.model.AuthResponse;
import com.picbank.authservice.model.LoginRequest;
import com.picbank.authservice.model.RegisterRequest;
import org.springframework.http.HttpStatus;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    HttpStatus register(RegisterRequest registerRequest);
}
